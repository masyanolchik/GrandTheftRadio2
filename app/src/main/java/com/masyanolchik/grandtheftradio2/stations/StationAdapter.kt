package com.masyanolchik.grandtheftradio2.stations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.masyanolchik.grandtheftradio2.R
import com.masyanolchik.grandtheftradio2.domain.Game
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import java.lang.RuntimeException


class StationAdapter(
    private val onTileClickCallback: (Station) -> Unit,
    private val onTrailingButtonClickCallback: (Station) -> Unit
): ListAdapter<StationsTreeItem, StationAdapter.StationsViewHolder>(StationsTreeItemDiff()) {
    abstract class StationsViewHolder(protected val view: View): ViewHolder(view) {
        abstract fun bind(stationsTreeItem: StationsTreeItem)

        class GameLabelViewHolder(view: View): StationsViewHolder(view) {
            private val title: TextView = view.findViewById(R.id.game_label)

            override fun bind(stationsTreeItem: StationsTreeItem) {
                if(stationsTreeItem is Game) {
                    title.text = stationsTreeItem.gameName
                } else {
                    throw throw RuntimeException("Unknown item provided to ${this::class.simpleName}")
                }
            }

            companion object {
                fun getInstance(context: Context, parent: ViewGroup) = GameLabelViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.game_title_item, parent, false)
                )
            }
        }

        class RadioStationViewHolder(
            view: View,
            private val onTileClickCallback: (Station) -> Unit,
            private val onTrailingButtonClickCallback: (Station) -> Unit,
        ): StationsViewHolder(view) {
            private val genreIcon: ImageView = view.findViewById(R.id.leading_icon)
            private val title: TextView = view.findViewById(R.id.station_name)
            private val trailingImageButton: ImageButton = view.findViewById(R.id.trailing_icon)

            override fun bind(stationsTreeItem: StationsTreeItem) {
                if(stationsTreeItem is Station) {
                    genreIcon.setImageDrawable(
                        view.context.getDrawable(
                            getGenreIconForStation(
                                stationsTreeItem
                            )
                        )
                    )
                    val animatorSet = AnimatorSet().apply {
                        val scaleDownAnim = ValueAnimator.ofFloat(1f,0.9f).apply {
                            addUpdateListener {
                                view.scaleX = it.animatedValue as Float
                                view.scaleY = it.animatedValue as Float
                            }
                        }
                        val scaleUpAnim = ValueAnimator.ofFloat(0.9f, 1f).apply {
                            addUpdateListener {
                                view.scaleX = it.animatedValue as Float
                                view.scaleY = it.animatedValue as Float
                            }
                        }
                        playSequentially(scaleDownAnim,scaleUpAnim)
                    }
                    view.setOnTouchListener { v, event ->
                        when(event.action) {
                            MotionEvent.ACTION_UP -> {
                                v.performClick()
                            }
                            MotionEvent.ACTION_DOWN -> {
                                animatorSet.cancel()
                                animatorSet.start()
                                true
                            }
                            else -> true
                        }
                    }
                    view.setOnClickListener {
                        onTileClickCallback.invoke(stationsTreeItem)
                    }
                    title.text = stationsTreeItem.name
                    val trailingIcon = if(stationsTreeItem.favorite) {
                        R.drawable.favorite
                    } else {
                        R.drawable.favorite_borderless
                    }

                    trailingImageButton.setImageDrawable(AppCompatResources.getDrawable(view.context, trailingIcon))
                    trailingImageButton.setOnClickListener {
                        onTrailingButtonClickCallback.invoke(
                            stationsTreeItem,
                        )
                    }
                } else {
                    throw throw RuntimeException("Unknown item provided to ${this::class.simpleName}")
                }
            }

            // It should not be there, feels more like a job for the presenter
            private fun getGenreIconForStation(station: Station): Int {
                return when(station.genre) {
                    "Hip Hop" -> R.drawable.hip_hop
                    "Country" -> R.drawable.country
                    "Techno" -> R.drawable.techno
                    "Rock" -> R.drawable.rock
                    "Funk" -> R.drawable.funk
                    else -> R.drawable.hip_hop
                }
            }
            companion object {
                fun getInstance(
                    context: Context,
                    parent: ViewGroup,
                    onTileClickCallback: (Station) -> Unit,
                    onTrailingButtonClickCallback: (Station) -> Unit
                ) = RadioStationViewHolder(
                        LayoutInflater.from(context)
                            .inflate(R.layout.station_card_item, parent, false),
                        onTileClickCallback,
                        onTrailingButtonClickCallback
                    )
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is Game -> GAME_VIEW_TYPE
            is Station -> RADIO_STATION_VIEW_TYPE
            else -> throw RuntimeException("Unknown view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationsViewHolder {
        return when(viewType) {
            GAME_VIEW_TYPE ->
                StationsViewHolder.GameLabelViewHolder.getInstance(parent.context, parent)
            RADIO_STATION_VIEW_TYPE ->
                StationsViewHolder.RadioStationViewHolder.getInstance(
                    parent.context,
                    parent,
                    onTileClickCallback,
                    onTrailingButtonClickCallback
                )
            else -> throw RuntimeException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: StationsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StationsTreeItemDiff : DiffUtil.ItemCallback<StationsTreeItem>() {
        override fun areItemsTheSame(
            oldItem: StationsTreeItem,
            newItem: StationsTreeItem
        ): Boolean = oldItem === newItem

        override fun areContentsTheSame(
            oldItem: StationsTreeItem,
            newItem: StationsTreeItem
        ): Boolean = oldItem == newItem

    }

    companion object {
        const val GAME_VIEW_TYPE = 0
        const val RADIO_STATION_VIEW_TYPE = 1
    }
}