package com.masyanolchik.grandtheftradio2.favorites

import android.content.ComponentName
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.util.concurrent.ListenableFuture
import com.masyanolchik.grandtheftradio2.MediaControllerHost
import com.masyanolchik.grandtheftradio2.PlaybackService
import com.masyanolchik.grandtheftradio2.R
import com.masyanolchik.grandtheftradio2.domain.Game
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stations.StationAdapter
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import org.koin.android.scope.createScope
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.inject
import org.koin.core.scope.Scope

/**
 * A [Fragment] for the favorites section.
 * Use the [FavoritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class FavoritesFragment : Fragment(), FavoritesContract.View, KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }

    private val favoritesPresenter: FavoritesContract.Presenter by inject()

    private lateinit var errorTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var stationAdapter: StationAdapter

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    private fun getSpanSizeLookup(adapter: StationAdapter, position: Int): Int {
        return when(adapter.getItemViewType(position)) {
            StationAdapter.GAME_VIEW_TYPE -> 2
            else -> 1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeController()
        progressBar = view.findViewById(R.id.progress_bar)
        errorTextView = view.findViewById(R.id.error_text)
        recyclerView = view.findViewById(R.id.stations_list)

        stationAdapter = StationAdapter(this::onStationTileClick,this::onTrailingTileIconClick)
        recyclerView.apply {
            recyclerView.layoutManager =
                GridLayoutManager(
                    context,
                    2,
                    GridLayoutManager.VERTICAL,
                    false
                )
                    .apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int) =
                                getSpanSizeLookup(stationAdapter, position)
                        }
                    }
            adapter = stationAdapter
        }
        favoritesPresenter.setView(this)
        favoritesPresenter.prepareFavoriteStations()
    }

    private fun onStationTileClick(station: Station) {
        favoritesPresenter.prepareStationSongs(station)
    }

    private fun onTrailingTileIconClick(station: Station) {
        favoritesPresenter.updateStation(station.apply {
            favorite = !favorite
        })
    }

    private fun initializeController() {
        val context = requireContext()
        controllerFuture =
            MediaController.Builder(
                context,
                SessionToken(context, ComponentName(context, PlaybackService::class.java))
            ).buildAsync()
    }

    override fun showLoadingProgress() {
        requireActivity().runOnUiThread {
            errorTextView.isVisible = false
            recyclerView.isVisible = false
            progressBar.isVisible = true
        }

    }

    override fun hideLoadingProgress() {
        requireActivity().runOnUiThread {
            errorTextView.isVisible = false
            recyclerView.isVisible = true
            progressBar.isVisible = false
        }

    }

    override fun showErrorScreen() {
        requireActivity().runOnUiThread {
            errorTextView.isVisible = true
            recyclerView.isVisible = false
            progressBar.isVisible = false
        }

    }

    override fun playMediaItems(mediaItems: List<MediaItem>, startOffsetMs: Long) {
        requireActivity().runOnUiThread {
            val hostActivity = requireActivity()
            if(hostActivity is MediaControllerHost) {
                val controller = hostActivity.getHostMediaController()
                controller?.clearMediaItems()
                controller?.addMediaItems(mediaItems)
                val seekListener = object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if(isPlaying) {
                            controller?.seekTo(startOffsetMs)
                            controller?.removeListener(this)
                        }
                    }
                }
                controller?.addListener(seekListener)
                controller?.play()
            }
        }
    }

    override fun updateList(listItems: List<StationsTreeItem>) {
        requireActivity().runOnUiThread {
            val stationWithFavoritesHeader = buildList {
                add(Game(
                    id = -1,
                    gameName = getString(R.string.bottom_nav_title_favorites),
                    universe = ""
                ))
                addAll(listItems)
            }
            stationAdapter.submitList(stationWithFavoritesHeader)
            stationAdapter.notifyDataSetChanged()
        }
    }

    override fun onDetach() {
        favoritesPresenter.onDetach()
        super.onDetach()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment FavoritesFragment.
         */
        @JvmStatic
        fun newInstance() = FavoritesFragment()
    }
}