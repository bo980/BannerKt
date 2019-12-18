package com.liang.banner

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.liang.banner.adapter.BannerAdapter
import kotlin.math.abs

/**
 * BannerView
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
class BannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(
    context,
    attrs,
    defStyle
), LifecycleObserver, ViewTreeObserver.OnGlobalLayoutListener {

    private val indicators = arrayListOf<Indicator>()
    private val defaultInterval = 5000
    private var lifecycle: Lifecycle? = null
    private var direction = 0

    var orientation = ORIENTATION_HORIZONTAL
        set(value) {
            if (value != field) {
                field = value
                viewPager.orientation = value
            }
        }
    var interval = 0L

    var isRunning = false
        private set
    var userInputEnabled = true
        set(value) {
            if (value != field) {
                field = value
                viewPager.isUserInputEnabled = value
            }
        }

    var pageTransformer: PageTransformer? = null
        set(value) {
            if (value != null) {
                field = value.apply {
                    orientation = this@BannerView.orientation
                }
                viewPager.setPageTransformer(field)
            }
        }

    var adapter: BannerAdapter<*, *>? = null
        set(value) {
            value?.let {
                field = it
                viewPager.viewTreeObserver.addOnGlobalLayoutListener(this)
                viewPager.adapter = field
                it.registerAdapterDataObserver(adapterDataObserver)
            }
        }


    private val adapterDataObserver by lazy {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                adapter?.let {
                    viewPager.setCurrentItem(it.itemCount / 2, false)
                    indicators.forEach { indicator ->
                        indicator.initCount(it.getBannerCount())
                        if (it.getBannerCount() > 0) {
                            indicator.onPageSelected((it.itemCount / 2) % it.getBannerCount())
                        }
                    }
                }
            }
        }
    }

    override fun onGlobalLayout() {
        adapter?.let {
            viewPager.setCurrentItem(it.itemCount / 2, false)
            indicators.forEach { indicator ->
                indicator.initCount(it.getBannerCount())
                if (it.getBannerCount() > 0) {
                    indicator.onPageSelected((it.itemCount / 2) % it.getBannerCount())
                }
            }
        }
        viewPager.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    private val runnable by lazy {
        Runnable {
            next()
        }
    }

    private val viewPager by lazy {
        ViewPager2(context, attrs, defStyle).apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    if (state == 0 && isRunning) {
                        postDelayed(runnable, interval)
                    } else {
                        removeCallbacks(runnable)
                    }
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    this@BannerView.adapter?.let {
                        indicators.forEach { indicator ->
                            indicator.onPageScrolled(
                                position % it.getBannerCount(),
                                positionOffset,
                                positionOffsetPixels
                            )
                        }
                    }
                }

                override fun onPageSelected(position: Int) {
                    this@BannerView.adapter?.let {
                        indicators.forEach { indicator ->
                            indicator.onPageSelected(
                                position % it.getBannerCount()
                            )
                        }
                    }
                }
            })
        }
    }

    init {
        init(attrs, defStyle)
        if (context is LifecycleOwner) {
            lifecycle = context.lifecycle.apply {
                addObserver(this@BannerView)
            }
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.BannerView, defStyle, 0
        )
        interval = a.getInt(R.styleable.BannerView_interval, defaultInterval).toLong()
        direction = a.getInt(R.styleable.BannerView_direction, LEFT)
        orientation = a.getInt(R.styleable.BannerView_orientation, ORIENTATION_HORIZONTAL)
        userInputEnabled = a.getBoolean(R.styleable.BannerView_userInputEnabled, true)
        a.recycle()
        addView(viewPager)
//        pageTransformer = ScaleTransformer(orientation = orientation)
//        pageTransformer = RotationTransformer(orientation = orientation)
    }

    @Synchronized
    private fun start() {
        removeCallbacks(runnable)
        postDelayed(runnable, interval)
        isRunning = true
    }

    private fun next() {
        adapter?.let {
            val totalCount = it.itemCount
            if (totalCount > 1) {
                var currentItem = viewPager.currentItem
                val nextItem =
                    if (direction == RIGHT) --currentItem else ++currentItem
                when {
                    nextItem < 0 -> {
                        viewPager.setCurrentItem(totalCount - 1, false)
                    }
                    nextItem >= totalCount -> {
                        viewPager.setCurrentItem(0, false)
                    }
                    else -> {
                        viewPager.setCurrentItem(nextItem, true)
                    }
                }
            }
        }
    }

    private fun stop() {
        removeCallbacks(runnable)
        isRunning = false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapter?.unregisterAdapterDataObserver(adapterDataObserver)
        onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        lifecycle?.removeObserver(this)
    }

    fun addIndicator(indicator: Indicator) {
        indicators.add(indicator)
    }

    fun removeIndicator(indicator: Indicator) {
        indicators.remove(indicator)
    }

    fun removeAllIndicator() {
        indicators.clear()
    }

    companion object {
        const val LEFT = 0
        const val RIGHT = 1
        const val ORIENTATION_HORIZONTAL = ViewPager2.ORIENTATION_HORIZONTAL
        const val ORIENTATION_VERTICAL = ViewPager2.ORIENTATION_VERTICAL
    }

    abstract class PageTransformer(var orientation: Int) : ViewPager2.PageTransformer
}
