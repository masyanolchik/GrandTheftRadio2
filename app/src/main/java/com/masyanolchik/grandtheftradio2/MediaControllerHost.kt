package com.masyanolchik.grandtheftradio2

import androidx.media3.session.MediaController

interface MediaControllerHost {
    fun getHostMediaController(): MediaController?
}