package com.masyanolchik.grandtheftradio2

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.view.isVisible
import java.lang.Float.min

class MiniPlayerOnTouchListener(private val onDismissCallback:(View)->Unit) : OnTouchListener {
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val displayMetrics = view.context.resources.displayMetrics
        val cardWidth = view.width
        val cardStart = (displayMetrics.widthPixels.toFloat() / 2) - (cardWidth / 2)

        when(event.action) {
            MotionEvent.ACTION_UP -> {
                if(view.x-cardStart < cardWidth/2-cardWidth+20 || view.x >= cardWidth/2) {
                    view.animate().alpha(0f).setDuration(100).start()
                    view.animate().x(cardStart).setDuration(100).start()
                    view.isVisible = false
                    onDismissCallback(view)
                } else {
                    view.performClick()
                    view.animate().x(cardStart).setDuration(100).start()
                    view.animate().alpha(1f).setDuration(100).start()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val newX = event.rawX

                if(cardWidth/2f + 100 < newX || cardWidth/2f - 100 > newX) {
                    view.animate()
                        .translationX(min(cardWidth / 2f, newX - (cardWidth / 2)))
                        .setDuration(0)
                        .start()
                    view.animate()
                        .alpha(0.5f)
                        .setDuration(0)
                        .start()
                }
            }
        }
        return true
    }
}