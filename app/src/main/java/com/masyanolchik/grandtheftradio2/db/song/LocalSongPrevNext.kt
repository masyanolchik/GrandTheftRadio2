package com.masyanolchik.grandtheftradio2.db.song

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.masyanolchik.grandtheftradio2.domain.Song

@Entity(
    primaryKeys = ["songId","prevSongId","nextSongId"],
    indices = [Index("songId"), Index("prevSongId"), Index("nextSongId")],
    foreignKeys = [
        ForeignKey(
            parentColumns = ["songId"],
            childColumns = ["songId"],
            entity = LocalSong::class,
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            parentColumns = ["songId"],
            childColumns = ["prevSongId"],
            entity = LocalSong::class,
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            parentColumns = ["songId"],
            childColumns = ["nextSongId"],
            entity = LocalSong::class,
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
)
data class LocalSongPrevNext(
    val songId: Int,
    val prevSongId: Int,
    val nextSongId: Int,
)

fun Song.fromPrevNextDomain() =
    LocalSongPrevNext(
        songId =  id,
        prevSongId = prevSongId,
        nextSongId = nextSongId,
    )