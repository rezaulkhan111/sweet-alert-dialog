package cn.pedant.sweet_alert

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.MotionEvent
import android.view.View

internal object Constants {
    //make bg a little bit darker
    @SuppressLint("ClickableViewAccessibility")
    val FOCUS_TOUCH_LISTENER = View.OnTouchListener { v, event ->
        val drawable = v.background
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_BUTTON_PRESS -> {
                drawable.setColorFilter(0x20000000, PorterDuff.Mode.SRC_ATOP)
                v.invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                drawable.clearColorFilter()
                v.invalidate()
            }
        }
        false
    }
}