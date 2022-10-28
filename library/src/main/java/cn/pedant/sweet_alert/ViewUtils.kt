package cn.pedant.sweet_alert

import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.StateListDrawable
import android.view.View

object ViewUtils {
    fun getDrawable(view: View): Array<Drawable>? {
        val drawable = view.background as StateListDrawable
        val dcs = drawable.constantState as DrawableContainer.DrawableContainerState?
        return dcs?.children
    }
}