package com.masyanolchik.grandtheftradio2

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSourceBitmapLoader
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CacheBitmapLoader
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaConstants
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.R
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture
import com.masyanolchik.grandtheftradio2.domain.Song
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stationstree.StationsTree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.guava.asListenableFuture
import org.koin.android.ext.android.inject

@UnstableApi class PlaybackService: MediaLibraryService() {
    private var currentSong: Song? = null
    private var currentOffSet = 0L
    private var currentSongHasBeenSought = false
    private val librarySessionCallback = CustomMediaSessionCallback()

    private lateinit var player: ExoPlayer
    private lateinit var mediaLibrarySession: MediaLibrarySession
    private val stationsTree: StationsTree by inject()
    private val coroutineScope: CoroutineScope by inject()

    override fun onCreate() {
        super.onCreate()

        initializeSessionAndPlayer()
        setListener(MediaSessionServiceListener())
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaLibrarySession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaLibrarySession.setSessionActivity(getSingleTopActivity())
        mediaLibrarySession.release()
        player.release()
        clearListener()
        super.onDestroy()
    }

    private fun initializeSessionAndPlayer() {
        player =
            ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                .build()

        player.repeatMode = Player.REPEAT_MODE_ALL
        player.addListener(object: Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val curMediaItem = player.currentMediaItem
                currentSong?.let {
                    if(isPlaying && curMediaItem != null && curMediaItem.mediaId == it.toMediaItem().mediaId && !currentSongHasBeenSought) {
                        player.seekTo(currentOffSet)
                        currentSongHasBeenSought = true
                    }
                }
            }
        })

        mediaLibrarySession =
            MediaLibrarySession.Builder(this, player, librarySessionCallback)
                .setSessionActivity(getSingleTopActivity())
                .setBitmapLoader(CacheBitmapLoader(DataSourceBitmapLoader(/* context= */ this)))
                .build()
    }

    private fun getSingleTopActivity(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    }
    private inner class CustomMediaSessionCallback: MediaLibrarySession.Callback {
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> =
            coroutineScope.async {
                if (params != null && params.isRecent) {
                    LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
                } else {
                    params?.extras?.putInt(
                        MediaConstants.EXTRAS_KEY_CONTENT_STYLE_PLAYABLE,
                        MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM)

                    val root = stationsTree.getRoot()?.toMediaItem()
                    if(root == null) {
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_IO)
                    } else {
                        LibraryResult.ofItem(root, params)
                    }
                }

            }.asListenableFuture()



        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> =
            coroutineScope.async {
                val item = stationsTree.getItem(mediaId)
                if(item == null) {
                    LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                } else {
                    LibraryResult.ofItem(item.toMediaItem(), /* params= */ null)
                }
            }.asListenableFuture()

        override fun onSubscribe(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> =
            coroutineScope.async {
                val children =
                    stationsTree.getChildren(parentId)

                session.notifyChildrenChanged(browser, parentId, children.size, params)
                LibraryResult.ofVoid()
            }.asListenableFuture()

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> =
            coroutineScope.async {
                LibraryResult.ofItemList(stationsTree.getChildren(parentId).map { it.toMediaItem() }, params)
            }.asListenableFuture()

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>>
        {
            val currentOrderedNewChildrenDeferred = coroutineScope.async {
                val upcomingMediaItem = stationsTree.getItem(mediaItems.first().mediaId)
                if(upcomingMediaItem is Station) {
                    val (song, offset) = upcomingMediaItem.getCurrentSongWithSeekPosition(System.currentTimeMillis())
                    currentSong = song
                    currentOffSet = offset
                    currentSongHasBeenSought = false
                    val newChildren = stationsTree
                        .getChildren(mediaItems.first().mediaId)
                        .toMutableList()
                        .map { it as Song }

                    buildList {
                        val firstSong = newChildren.first { song == it }
                        val position = newChildren.indexOf(firstSong)
                        add(firstSong)
                        addAll(newChildren.takeLast(newChildren.size - 1 - position))
                        addAll(newChildren.take(position))
                    }.map { it.toMediaItem() }.toMutableList()
                } else {
                    buildList {
                        if(upcomingMediaItem != null) {
                            add(upcomingMediaItem.toMediaItem())
                        }
                    }.toMutableList()
                }

            }

            return currentOrderedNewChildrenDeferred.asListenableFuture()
        }
    }

    private inner class MediaSessionServiceListener : Listener {

        /**
         * This method is only required to be implemented on Android 12 or above when an attempt is made
         * by a media controller to resume playback when the {@link MediaSessionService} is in the
         * background.
         */
        @SuppressLint("MissingPermission")
        override fun onForegroundServiceStartNotAllowedException() {
            val notificationManagerCompat = NotificationManagerCompat.from(this@PlaybackService)
            ensureNotificationChannel(notificationManagerCompat)
            val pendingIntent =
                TaskStackBuilder.create(this@PlaybackService).run {
                    addNextIntent(Intent(this@PlaybackService, MainActivity::class.java))
                    getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
                }
            val builder =
                NotificationCompat.Builder(this@PlaybackService, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.media3_notification_small_icon)
                    .setContentTitle("Dummy content title")
                    .setStyle(
                        NotificationCompat.BigTextStyle().bigText("Dummy big text")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
        if (Util.SDK_INT < 26 || notificationManagerCompat.getNotificationChannel(CHANNEL_ID) != null) {
            return
        }

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Media notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationManagerCompat.createNotificationChannel(channel)
    }



    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "gtr_notification_channel_id"
    }
}