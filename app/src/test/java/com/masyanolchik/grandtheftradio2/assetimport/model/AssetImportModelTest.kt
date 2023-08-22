package com.masyanolchik.grandtheftradio2.assetimport.model

import com.google.common.truth.Truth.assertThat
import com.google.gson.JsonSyntaxException
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stationstree.StationsTree
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import com.masyanolchik.grandtheftradio2.domain.Result
import org.mockito.kotlin.doThrow
import kotlin.IllegalStateException

@RunWith(MockitoJUnitRunner::class)
class AssetImportModelTest {
    private val mockedStationTree = mock(StationsTree::class.java)

    @Test
    fun testAssetImportModel_stationTreeReinitialized() = runTest {
        val assetImportModel = AssetImportModel(mockedStationTree)
        val expectedStationTreeList = mutableListOf<Station>()
        doAnswer {
            val stationList = it.arguments[0] as List<Station>
            expectedStationTreeList.addAll(stationList)
            null
        }.`when`(mockedStationTree).reinitialize(any())

        val result = assetImportModel.buildMediaTreeFromStationsList(SERIALIZED_STRING)

        assertThat(expectedStationTreeList).isNotEmpty()
        assertThat(result.first()).isInstanceOf(Result.Completed::class.java)
    }

    @Test
    fun testAssetImportModel_returnsErrorWhenStringIncorrect() = runTest {
        val assetImportModel = AssetImportModel(mockedStationTree)

        val result = assetImportModel.buildMediaTreeFromStationsList("station")

        assertThat(result.first()).isInstanceOf(Result.Error::class.java)
        assertThat((result.first() as Result.Error).throwable).isInstanceOf(JsonSyntaxException::class.java)
    }

    @Test
    fun testAssetImportModel_returnsErrorWhenStringEmpty() = runTest {
        val assetImportModel = AssetImportModel(mockedStationTree)

        val result = assetImportModel.buildMediaTreeFromStationsList("")

        assertThat(result.first()).isInstanceOf(Result.Error::class.java)
        assertThat((result.first() as Result.Error).throwable)
            .isInstanceOf(NullPointerException::class.java)
    }

    @Test
    fun testAssetImportModel_returnsErrorWhenReinitializedFails() = runTest {
        val assetImportModel = AssetImportModel(mockedStationTree)
        doThrow(IllegalStateException("the message"))
            .`when`(mockedStationTree).reinitialize(any())
        val result = assetImportModel.buildMediaTreeFromStationsList(SERIALIZED_STRING)

        assertThat(result.first()).isInstanceOf(Result.Error::class.java)
        assertThat((result.first() as Result.Error).throwable)
            .isInstanceOf(IllegalStateException::class.java)
    }

    companion object {
        const val SERIALIZED_STRING = "[{\n" +
                "        \"id\": \"1\",\n" +
                "        \"game\": {\n" +
                "            \"id\": \"1\",\n" +
                "            \"gameName\": \"Grand Theft Auto\",\n" +
                "            \"universe\": \"2D\"\n" +
                "        },\n" +
                "        \"name\": \"The Fergus Buckner Show FM\",\n" +
                "        \"genre\": \"Country\",\n" +
                "        \"tags\": [{\n" +
                "            \"tagName\": \"Country\"\n" +
                "        }\n" +
                "        ],\n" +
                "        \"picLink\": \"https://static.wikigta.org/en/images/thumb/0/09/Gta1cover.jpg/600px-Gta1cover.jpg\",\n" +
                "        \"songs\": [{\n" +
                "            \"id\": \"16\",\n" +
                "            \"prevSongId\": \"16\",\n" +
                "            \"nextSongId\": \"16\",\n" +
                "            \"artist\": \"Sideways Hank O'Mally (and The Alabama Bottle Boys)\",\n" +
                "            \"title\": \"The Ballad of Chapped-Lips Calhoun\",\n" +
                "            \"msOffset\": \"0\",\n" +
                "            \"link\": \"https://www.dropbox.com/scl/fi/gs6ktbjj6jezzfgmlswtk/Hank-O-Malley-The-Ballad-of-Chapped-Lips-Calhoun.mp3?rlkey=ym7g6vtq7383do363wwhu1gtg&dl=1\",\n" +
                "            \"radioName\": \"The Fergus Buckner Show FM\",\n" +
                "            \"picLink\": \"https://static.wikigta.org/en/images/thumb/0/09/Gta1cover.jpg/600px-Gta1cover.jpg\",\n" +
                "            \"msTotalLength\": \"189384\"\n" +
                "        }]\n" +
                "    }]"
    }
}