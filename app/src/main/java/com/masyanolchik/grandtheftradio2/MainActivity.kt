package com.masyanolchik.grandtheftradio2

import android.content.ComponentName
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MainActivity : AppCompatActivity(), MediaControllerHost {
    private lateinit var miniPlayer: View
    private lateinit var playPauseButton: MaterialButton
    private lateinit var albumCoverView: ImageView
    private lateinit var artistTitleView: TextView
    private lateinit var trackNameView: TextView

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController

        miniPlayer = findViewById<View?>(R.id.mini_player).apply {
            setOnTouchListener(MiniPlayerOnTouchListener{
                controller?.stop()
            })
        }
        savedInstanceState?.let {
            miniPlayer.isVisible = it.getBoolean(MINI_PLAYER_IS_VISIBLE_KEY)
        }

        albumCoverView = findViewById(R.id.cover_image)
        artistTitleView = findViewById(R.id.title)
        trackNameView = findViewById(R.id.subtitle)
        playPauseButton = findViewById<MaterialButton?>(R.id.play_pause).apply {
            setOnClickListener {
                val controllerCopy = controller
                if(controllerCopy != null && controllerCopy.isPlaying) {
                    controllerCopy.pause()
                } else if(controllerCopy != null && !controllerCopy.isPlaying) {
                    controllerCopy.play()
                }
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { menuItem ->
            navController.currentDestination?.let {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
                    .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
                    .build()
                toolbar.setTitle(R.string.bottom_nav_title_import)
                when (menuItem.itemId) {
                    R.id.fav_dest_menu -> {
                        if (it.id != R.id.fav_dest) {
                            toolbar.setTitle(R.string.bottom_nav_title_favorites)
                            navController.navigate(R.id.fav_dest, null, navOptions)
                        }
                    }

                    R.id.twod_dest_menu -> {
                        if (!toolbar.title.equals(getString(R.string.bottom_nav_title_2d))) {
                            toolbar.setTitle(R.string.bottom_nav_title_2d)
                            navController.navigate(
                                R.id.stations_dest,
                                bundleOf("eraName" to "2D"),
                                navOptions
                            )
                        }
                    }

                    R.id.threed_dest_menu -> {
                        if (!toolbar.title.equals(getString(R.string.bottom_nav_title_3d))) {
                            toolbar.setTitle(R.string.bottom_nav_title_3d)
                            navController.navigate(
                                R.id.stations_dest,
                                bundleOf("eraName" to "3D"),
                                navOptions
                            )
                        }
                    }

                    R.id.hd_dest_menu -> {
                        if (!toolbar.title.equals(getString(R.string.bottom_nav_title_hd))) {
                            toolbar.setTitle(R.string.bottom_nav_title_hd)
                            navController.navigate(
                                R.id.stations_dest,
                                bundleOf("eraName" to "HD"),
                                navOptions
                            )
                        }
                    }

                    R.id.import_dest_menu -> {
                        if (it.id != R.id.import_dest) {
                            navController.navigate(R.id.import_dest, null, navOptions)
                        }
                    }

                    else -> throw IllegalStateException("Unknown destination")
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun initializeController() {
        controllerFuture =
            MediaController.Builder(
                this,
                SessionToken(this, ComponentName(this, PlaybackService::class.java))
            ).buildAsync()
        controllerFuture.addListener({ setController() }, MoreExecutors.directExecutor())
    }

    override fun onStart() {
        super.onStart()
        initializeController()
    }

    private fun setController() {
        val controller = this.controller ?: return

        if(controller.isLoading || controller.isPlaying) {
            miniPlayer.isVisible = true
        }

        isPlayingUiSwitch(controller.isPlaying)

        updateMediaMetadataUI(controller.mediaMetadata)

        controller.addListener(
            object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    playPauseButton.icon =
                        AppCompatResources.getDrawable(this@MainActivity, R.drawable.error)
                }

                override fun onIsLoadingChanged(isLoading: Boolean) {
                    if(isLoading) {
                        miniPlayer.isVisible = true
                        miniPlayer.alpha = 1f
                        val circularProgressDrawable =
                            CircularProgressDrawable(this@MainActivity)
                        playPauseButton.icon = circularProgressDrawable
                        circularProgressDrawable.start()
                    } else {
                        isPlayingUiSwitch(controller.isPlaying)
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    isPlayingUiSwitch(isPlaying)
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    updateMediaMetadataUI(mediaMetadata)
                }
            }
        )
    }

    private fun isPlayingUiSwitch(isPlaying: Boolean) {
        if(isPlaying) {
            playPauseButton.icon =
                AppCompatResources.getDrawable(this@MainActivity, R.drawable.pause)
        } else {
            playPauseButton.icon =
                AppCompatResources.getDrawable(this@MainActivity, R.drawable.play_arrow)
        }
    }

    private fun updateMediaMetadataUI(mediaMetadata: MediaMetadata) {
        if(mediaMetadata.mediaType == MediaMetadata.MEDIA_TYPE_MUSIC) {
            val trackName = mediaMetadata.title
            val artistName = mediaMetadata.artist
            val albumCoverUri = mediaMetadata.artworkUri.toString()

            trackNameView.text = trackName
            artistTitleView.text = artistName
            albumCoverView.load(albumCoverUri)
        }
    }

    override fun onStop() {
        super.onStop()
        MediaController.releaseFuture(controllerFuture)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            this.putBoolean(MINI_PLAYER_IS_VISIBLE_KEY, miniPlayer.isVisible)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        savedInstanceState?.let {
            miniPlayer.isVisible = it.getBoolean(MINI_PLAYER_IS_VISIBLE_KEY)
        }
    }

    override fun getHostMediaController() = controller

    companion object {
        private const val MINI_PLAYER_IS_VISIBLE_KEY = "mini_player_visible"
    }
}