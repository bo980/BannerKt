package com.liang.banner

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller

/**
 * Created by Administrator on 2017/8/9.
 */
class BannerScroller @JvmOverloads constructor(
    context: Context?,
    interpolator: Interpolator? = null
) : Scroller(context, interpolator) {

    private var scrollFactor = 1.0F

    /**
     * Set the factor by which the duration will change
     */
    fun setScrollDurationFactor(scrollFactor: Float) {
        this.scrollFactor = scrollFactor
    }

    override fun startScroll(
        startX: Int,
        startY: Int,
        dx: Int,
        dy: Int,
        duration: Int
    ) {
        super.startScroll(startX, startY, dx, dy, (duration * scrollFactor).toInt())
    }
}