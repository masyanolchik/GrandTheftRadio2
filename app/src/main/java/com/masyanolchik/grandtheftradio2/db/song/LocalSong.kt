package com.masyanolchik.grandtheftradio2.db.song

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.masyanolchik.grandtheftradio2.domain.Song


@Entity(
    tableName="songs",
)
data class LocalSong(
    @PrimaryKey val songId: Int,
    val artist: String,
    val title: String,
    val msOffset: Long,
    val link: String,
    val radioName: String,
    val picLink: String,
    val msTotalLength: Long,
)

fun LocalSong.toDomain() =
    Song(
        id = songId,
        artist = artist,
        title = title,
        msOffset = msOffset,
        link = link,
        radioName = radioName,
        picLink = picLink,
        msTotalLength = msTotalLength,
    )

fun Song.fromDomain() =
    LocalSong(
        songId = id,
        artist = artist,
        title = title,
        msOffset = msOffset,
        link = link,
        radioName = radioName,
        picLink = picLink,
        msTotalLength = msTotalLength,
    )