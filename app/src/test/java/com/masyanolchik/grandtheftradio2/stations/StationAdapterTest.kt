package com.masyanolchik.grandtheftradio2.stations

import android.content.Context
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.R
import com.masyanolchik.grandtheftradio2.domain.Game
import com.masyanolchik.grandtheftradio2.domain.Song
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stations.StationAdapter
import com.masyanolchik.grandtheftradio2.stations.StationAdapter.Companion.GAME_VIEW_TYPE
import com.masyanolchik.grandtheftradio2.stations.StationAdapter.Companion.RADIO_STATION_VIEW_TYPE
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class StationAdapterTest {
    private val context: Context = RuntimeEnvironment.getApplication()

    @Before
    fun setup() {
        context.setTheme(R.style.Theme_GrandTheftRadio2)
    }

    @After
    fun stopDi() {
        stopKoin()
    }

    @Test
    fun testStationAdapterTest_gameLabelViewHolder_boundAsExpected() {
        val gameLabelViewHolder =
            StationAdapter.StationsViewHolder.GameLabelViewHolder.getInstance(context,LinearLayout(context))
        val labelTextView: TextView = gameLabelViewHolder.itemView.findViewById(R.id.game_label)

        gameLabelViewHolder.bind(GAME)

        assertThat(labelTextView.text).isEqualTo(GAME.gameName)
    }

    @Test
    fun testStationAdapterTest_gameLabelViewHolder_wrongItemHandled() {
        val gameLabelViewHolder =
            StationAdapter.StationsViewHolder.GameLabelViewHolder.getInstance(context,LinearLayout(context))

        try {
            gameLabelViewHolder.bind(STATION)
        } catch (ex: Exception) {
            assertThat(ex).isInstanceOf(RuntimeException::class.java)
            assertThat(ex.message)
                .isEqualTo("Unknown item provided to ${StationAdapter.StationsViewHolder.GameLabelViewHolder::class.simpleName}")
        }
    }

    @Test
    fun testStationAdapterTest_radioStationViewHolder_boundAsExpected() {
        val radioStationViewHolder =
            StationAdapter.StationsViewHolder.RadioStationViewHolder.getInstance(
                context,
                LinearLayout(context),
                { _ -> },
                { _, _ -> }
            )
        val genreIcon: ImageView = radioStationViewHolder.itemView.findViewById(R.id.leading_icon)
        val title: TextView = radioStationViewHolder.itemView.findViewById(R.id.station_name)
        val trailingImageButton: ImageButton =
            radioStationViewHolder.itemView.findViewById(R.id.trailing_icon)

        radioStationViewHolder.bind(STATION)

        assertThat(title.text).isEqualTo(STATION.name)
        assertThat(trailingImageButton.isVisible).isFalse()
        assertThat(shadowOf(genreIcon.drawable).createdFromResId).isEqualTo(R.drawable.hip_hop)
        assertThat(shadowOf(trailingImageButton.drawable).createdFromResId).isEqualTo(R.drawable.favorite)
    }

    @Test
    fun testStationAdapterTest_radioStationViewHolder_hipHopHasCorrectIcon() {
        val radioStationViewHolder =
            StationAdapter.StationsViewHolder.RadioStationViewHolder.getInstance(
                context,
                LinearLayout(context),
                { _ -> },
                { _, _ -> }
            )
        val genreIcon: ImageView = radioStationViewHolder.itemView.findViewById(R.id.leading_icon)

        radioStationViewHolder.bind(STATION.copy(genre = "Hip Hop"))

        assertThat(shadowOf(genreIcon.drawable).createdFromResId).isEqualTo(R.drawable.hip_hop)
    }

    @Test
    fun testStationAdapterTest_radioStationViewHolder_countryHasCorrectIcon() {
        val radioStationViewHolder =
            StationAdapter.StationsViewHolder.RadioStationViewHolder.getInstance(
                context,
                LinearLayout(context),
                { _ -> },
                { _, _ -> }
            )
        val genreIcon: ImageView = radioStationViewHolder.itemView.findViewById(R.id.leading_icon)

        radioStationViewHolder.bind(STATION.copy(genre = "Country"))

        assertThat(shadowOf(genreIcon.drawable).createdFromResId).isEqualTo(R.drawable.country)
    }

    @Test
    fun testStationAdapterTest_radioStationViewHolder_technoHasCorrectIcon() {
        val radioStationViewHolder =
            StationAdapter.StationsViewHolder.RadioStationViewHolder.getInstance(
                context,
                LinearLayout(context),
                { _ -> },
                { _, _ -> }
            )
        val genreIcon: ImageView = radioStationViewHolder.itemView.findViewById(R.id.leading_icon)

        radioStationViewHolder.bind(STATION.copy(genre = "Techno"))

        assertThat(shadowOf(genreIcon.drawable).createdFromResId).isEqualTo(R.drawable.techno)
    }

    @Test
    fun testStationAdapterTest_radioStationViewHolder_rockHasCorrectIcon() {
        val radioStationViewHolder =
            StationAdapter.StationsViewHolder.RadioStationViewHolder.getInstance(
                context,
                LinearLayout(context),
                { _ -> },
                { _, _ -> }
            )
        val genreIcon: ImageView = radioStationViewHolder.itemView.findViewById(R.id.leading_icon)

        radioStationViewHolder.bind(STATION.copy(genre = "Rock"))

        assertThat(shadowOf(genreIcon.drawable).createdFromResId).isEqualTo(R.drawable.rock)
    }

    @Test
    fun testStationAdapterTest_radioStationViewHolder_funkHasCorrectIcon() {
        val radioStationViewHolder =
            StationAdapter.StationsViewHolder.RadioStationViewHolder.getInstance(
                context,
                LinearLayout(context),
                { _ -> },
                { _, _ -> }
            )
        val genreIcon: ImageView = radioStationViewHolder.itemView.findViewById(R.id.leading_icon)

        radioStationViewHolder.bind(STATION.copy(genre = "Funk"))

        assertThat(shadowOf(genreIcon.drawable).createdFromResId).isEqualTo(R.drawable.funk)
    }


    @Test
    fun testStationAdapterTest_radioStationViewHolder_clickListenersWorking() {
        var receivedStationOnClick: Station? = null
        var receivedFavoriteStationOnClick: Station? = null
        var selected = true
        val radioStationViewHolder =
            StationAdapter.StationsViewHolder.RadioStationViewHolder.getInstance(
                context,
                LinearLayout(context),
                { station ->
                    receivedStationOnClick = station
                },
                { station, isSelected ->
                    receivedFavoriteStationOnClick = station
                    selected = isSelected
                }
            )
        val trailingImageButton: ImageButton =
            radioStationViewHolder.itemView.findViewById(R.id.trailing_icon)

        radioStationViewHolder.bind(STATION)
        radioStationViewHolder.itemView.performClick()
        trailingImageButton.performClick()

        assertThat(receivedStationOnClick).isNotNull()
        assertThat(receivedFavoriteStationOnClick).isNotNull()
        assertThat(selected).isFalse()
    }

    @Test
    fun testStationAdapterTest_radioStationViewHolder_wrongItemHandled() {
        val radioStationViewHolder =
            StationAdapter.StationsViewHolder.RadioStationViewHolder.getInstance(
                context,
                LinearLayout(context),
                { _ -> },
                { _, _ -> }
            )

        try {
            radioStationViewHolder.bind(GAME)
        } catch (ex: Exception) {
            assertThat(ex).isInstanceOf(RuntimeException::class.java)
            assertThat(ex.message)
                .isEqualTo("Unknown item provided to ${StationAdapter.StationsViewHolder.RadioStationViewHolder::class.simpleName}")
        }
    }

    @Test
    fun testStationAdapterTest_stationsItemTreeDiff_areItemsTheSameWithTheSameObject() {
        val stationItemTreeItemDiff = StationAdapter.StationsTreeItemDiff()

        val result = stationItemTreeItemDiff.areItemsTheSame(GAME, GAME)

        assertThat(result).isTrue()
    }

    @Test
    fun testStationAdapterTest_stationsItemTreeDiff_areItemsTheSameWithTheDifferentObject() {
        val stationItemTreeItemDiff = StationAdapter.StationsTreeItemDiff()

        val result = stationItemTreeItemDiff.areItemsTheSame(GAME, GAME.copy())

        assertThat(result).isFalse()
    }

    @Test
    fun testStationAdapterTest_stationsItemTreeDiff_areContentsTheSame() {
        val stationItemTreeItemDiff = StationAdapter.StationsTreeItemDiff()

        val result = stationItemTreeItemDiff.areContentsTheSame(GAME.copy(), GAME.copy())

        assertThat(result).isTrue()
    }

    @Test
    fun testStationAdapterTest_stationsItemTreeDiff_areContentsTheSameDifferentObject() {
        val stationItemTreeItemDiff = StationAdapter.StationsTreeItemDiff()

        val result = stationItemTreeItemDiff.areContentsTheSame(GAME.copy(id = 1), GAME.copy())

        assertThat(result).isFalse()
    }

    @Test
    fun testStationAdapterTest_getItemViewType_returnsCorrectViewTypes() {
        val adapter = StationAdapter(
            { _ -> },
            { _, _ -> }
        )

        adapter.submitList(
            listOf(
                GAME,
                STATION,
                Song(0,0,0,"","",0L,"","","",0L)
            )
        )

        assertThat(adapter.getItemViewType(0)).isEqualTo(GAME_VIEW_TYPE)
        assertThat(adapter.getItemViewType(1)).isEqualTo(RADIO_STATION_VIEW_TYPE)
        try {
            adapter.getItemViewType(2)
        } catch (ex: Exception) {
            assertThat(ex).isInstanceOf(RuntimeException::class.java)
            assertThat(ex.message).isEqualTo("Unknown view type")
        }
    }

    @Test
    fun testStationAdapterTest_onCreateViewHolder_returnsCorrectViewHolder() {
        val adapter = StationAdapter(
            { _ -> },
            { _, _ -> }
        )
        val parentViewGroup = LinearLayout(context)

        assertThat(adapter.onCreateViewHolder(parentViewGroup,GAME_VIEW_TYPE))
            .isInstanceOf(StationAdapter.StationsViewHolder.GameLabelViewHolder::class.java)
        assertThat(adapter.onCreateViewHolder(parentViewGroup, RADIO_STATION_VIEW_TYPE))
            .isInstanceOf(StationAdapter.StationsViewHolder.RadioStationViewHolder::class.java)
        try {
            adapter.onCreateViewHolder(parentViewGroup, 999)
        } catch (ex: Exception) {
            assertThat(ex).isInstanceOf(RuntimeException::class.java)
            assertThat(ex.message).isEqualTo("Unknown view type")
        }
    }

    @Test
    fun testStationAdapterTest_onBindViewHolder_viewHolderIsBound() {
        val adapter = StationAdapter(
            { _ -> },
            { _, _ -> }
        )
        val parentViewGroup = LinearLayout(context)
        adapter.submitList(listOf(GAME, STATION))

        val gameLabelViewHolder = adapter.onCreateViewHolder(parentViewGroup, GAME_VIEW_TYPE)
        val labelTextView: TextView = gameLabelViewHolder.itemView.findViewById(R.id.game_label)
        val radioStationViewHolder = adapter.onCreateViewHolder(parentViewGroup, RADIO_STATION_VIEW_TYPE)
        val radioTitle: TextView = radioStationViewHolder.itemView.findViewById(R.id.station_name)

        adapter.onBindViewHolder(gameLabelViewHolder, 0)
        adapter.onBindViewHolder(radioStationViewHolder, 1)

        assertThat(labelTextView.text).isEqualTo(GAME.gameName)
        assertThat(radioTitle.text).isEqualTo(STATION.name)
    }

    companion object {
        val GAME = Game(
            id = 0,
            gameName = "GameName",
            universe = "2D"
        )

        val STATION =
            Station(
                id = 0,
                game = GAME,
                name = "Station name",
                genre = "Genre",
                picLink = "picLink",
                songs = listOf()
            )
    }
}