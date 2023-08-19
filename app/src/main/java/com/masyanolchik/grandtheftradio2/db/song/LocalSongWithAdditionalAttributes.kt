package com.masyanolchik.grandtheftradio2.db.song

import androidx.room.Embedded
import androidx.room.Relation

data class LocalSongWithAdditionalAttributes(
    @Embedded var localSong: LocalSong,
    @Relation(
        parentColumn = "songId",
        entityColumn = "songId",
    )
    var prevNextSongs: LocalSongPrevNext,
)