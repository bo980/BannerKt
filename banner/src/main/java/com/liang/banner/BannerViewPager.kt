package com.liang.banner

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Interpolator
import androidx.viewpager.widget.ViewPager

/**
 * Created by Administrator on 2017/8/9.
 */
class BannerViewPager @JvmOverloads constructor(
    paramContext: Context,
    paramAttributeSet: AttributeSet? = null
) : ViewPager(paramContext, paramAttributeSet) {
    var interval = DEFAULT_INTERVAL
    private var direction = RIGHT
    private var isCycle = true
    var slideBorderMode = SLIDE_BORDER_MODE_NONE
    var isBorderAnimation = true
    private var isAutoScroll = false
    private var isStopByTouch = false
    private var touchX = 0f
    private var downX = 0f
    private var scroller: BannerScroller? = null

    private val runnable by lazy {
        Runnable {
            if (isAutoScroll) {
                scrollOnce()
                startScroll()
            }
        }
    }


    init {
        setViewPagerScroller()
    }


    @JvmOverloads
    fun startAutoScroll(delayTimeInMills: Long = interval) {
        interval = delayTimeInMills
        adapter?.let {
            if (it.count > 1) {
                isAutoScroll = true
                startScroll()
            }
        }
    }

    private fun startScroll() {
        removeCallbacks(runnable)
        postDelayed(runnable, interval)
    }


    fun stopAutoScroll() {
        isAutoScroll = false
        removeCallbacks(runnable)
    }


    fun setScrollDurationFactor(scrollFactor: Float) {
        scroller?.setScrollDurationFactor(scrollFactor)
    }

    private fun setViewPagerScroller() {
        try {
            val scrollerField =
                ViewPager::class.java.getDeclaredField("mScroller")
            scrollerField.isAccessible = true
            val interpolatorField =
                ViewPager::class.java.getDeclaredField("sInterpolator")
            interpolatorField.isAccessible = true
            scroller = BannerScroller(
                context,
                interpolatorField[null] as Interpolator
            )
            scrollerField[this] = scroller
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scrollOnce() {
        adapter?.let {
            val totalCount = it.count
            if (totalCount > 1) {
                val nextItem =
                    if (direction == LEFT) --currentItem else ++currentItem
                if (nextItem < 0) {
                    if (isCycle) {
                        setCurrentItem(totalCount - 1, isBorderAnimation)
                    }
                } else if (nextItem == totalCount) {
                    if (isCycle) {
                        setCurrentItem(0, isBorderAnimation)
                    }
                } else {
                    setCurrentItem(nextItem, true)
                }
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && isAutoScroll) {
            isStopByTouch = true
            stopAutoScroll()
        } else if (ev.action == MotionEvent.ACTION_UP && isStopByTouch) {
            startAutoScroll()
        }
        if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT || slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
            touchX = ev.x
            if (ev.action == MotionEvent.ACTION_DOWN) {
                downX = touchX
            }
            val currentItem = currentItem
            val adapter = adapter
            val pageCount = adapter?.count ?: 0
            if (currentItem == 0 && downX <= touchX || currentItem == pageCount - 1 && downX >= touchX) {
                if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    if (pageCount > 1) {
                        setCurrentItem(pageCount - currentItem - 1, isBorderAnimation)
                    }
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                return super.onTouchEvent(ev)
            }
        }
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(ev)
    }

    fun getDirection(): Int {
        return if (direction == LEFT) LEFT else RIGHT
    }

    fun setDirection(direction: Int) {
        this.direction = direction
    }

    companion object {
        const val DEFAULT_INTERVAL = 1500L
        const val LEFT = 0
        const val RIGHT = 1
        const val SLIDE_BORDER_MODE_NONE = 0
        const val SLIDE_BORDER_MODE_CYCLE = 1
        const val SLIDE_BORDER_MODE_TO_PARENT = 2
    }
}