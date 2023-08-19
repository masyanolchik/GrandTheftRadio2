package com.masyanolchik.grandtheftradio2.db.song

import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.domain.Song
import org.junit.Test

class LocalSongExtensionsTest {
    @Test
    fun convertSongToLocalSong() {
        val song = Song(
            id = 0,
            artist = "Bob Doe",
            prevSongId = 1,
            nextSongId = 1,
            title = "No name",
            msOffset = 0L,
            link = "link",
            radioName = "station no name",
            picLink = "link",
            msTotalLength = 30L
        )

        val localSong = song.fromDomain()
        val localSongPrevNext = song.fromPrevNextDomain()

        assertThat(localSong.songId).isEqualTo(song.id)
        assertThat(localSongPrevNext.nextSongId).isEqualTo(song.nextSongId)
        assertThat(localSongPrevNext.prevSongId).isEqualTo(song.prevSongId)
        assertThat(localSong.artist).isEqualTo(song.artist)
        assertThat(localSong.title).isEqualTo(song.title)
        assertThat(localSong.msOffset).isEqualTo(song.msOffset)
        assertThat(localSong.link).isEqualTo(song.link)
        assertThat(localSong.radioName).isEqualTo(song.radioName)
        assertThat(localSong.picLink).isEqualTo(song.picLink)
        assertThat(localSong.msTotalLength).isEqualTo(song.msTotalLength)
    }

    @Test
    fun convertLocalSongToSong() {
        val localSong = LocalSong(
            songId = 0,
            artist = "Bob Doe",
            title = "No name",
            msOffset = 0L,
            link = "link",
            radioName = "station no name",
            picLink = "link",
            msTotalLength = 30L
        )
        val localPrevNextSong = LocalSongPrevNext(
            songId = 0,
            prevSongId = 1,
            nextSongId = 1,
        )
        val songWithAdditionalAttributes = LocalSongWithAdditionalAttributes(
            localSong,
            localPrevNextSong
        )

        val song = songWithAdditionalAttributes.toDomain()

        assertThat(songWithAdditionalAttributes.localSong.songId)
            .isEqualTo(song.id)
        assertThat(songWithAdditionalAttributes.prevNextSongs.nextSongId)
            .isEqualTo(song.nextSongId)
        assertThat(songWithAdditionalAttributes.prevNextSongs.prevSongId)
            .isEqualTo(song.prevSongId)
        assertThat(songWithAdditionalAttributes.localSong.artist)
            .isEqualTo(song.artist)
        assertThat(songWithAdditionalAttributes.localSong.title)
            .isEqualTo(song.title)
        assertThat(songWithAdditionalAttributes.localSong.msOffset)
            .isEqualTo(song.msOffset)
        assertThat(songWithAdditionalAttributes.localSong.link)
            .isEqualTo(song.link)
        assertThat(songWithAdditionalAttributes.localSong.radioName)
            .isEqualTo(song.radioName)
        assertThat(songWithAdditionalAttributes.localSong.picLink)
            .isEqualTo(song.picLink)
        assertThat(songWithAdditionalAttributes.localSong.msTotalLength)
            .isEqualTo(song.msTotalLength)
    }
}