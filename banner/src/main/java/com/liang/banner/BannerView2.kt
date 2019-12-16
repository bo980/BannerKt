package com.liang.banner

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager


/**
 * BannerView
 */
class BannerView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(
    context,
    attrs,
    defStyle
), ViewPager.OnPageChangeListener, LifecycleObserver {

    private val defaultInterval = 5000

    private var lifecycle: Lifecycle? = null

    var interval = 0L
        set(value) {
            field = value
            viewPager.interval = field
        }

    var slideBorderMode = 0
        set(value) {
            field = value
            viewPager.slideBorderMode = field
        }

    var isBorderAnimation = false
        set(value) {
            field = value
            viewPager.isBorderAnimation = field
        }

    private val viewPager by lazy {
        BannerViewPager(context, attrs)
    }

    private val adapter by lazy { PosterPagerAdapter() }

    private val banners = arrayListOf<Any>()

    fun setData(list: ArrayList<Any>) {
        banners.clear()
        banners.addAll(list)
        adapter.notifyDataSetChanged()
    }

    init {
        init(attrs, defStyle)
        if (context is LifecycleOwner) {
            lifecycle = context.lifecycle.apply {
                addObserver(this@BannerView2)
            }
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.BannerView, defStyle, 0
        )
        interval = a.getInt(R.styleable.BannerView_interval, defaultInterval).toLong()
        slideBorderMode = a.getInt(
            R.styleable.BannerView_slideBorderMode,
            BannerViewPager.SLIDE_BORDER_MODE_CYCLE
        )
        isBorderAnimation = a.getBoolean(R.styleable.BannerView_isBorderAnimation, true)
        a.recycle()

        addView(viewPager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(this)
        viewPager.currentItem = adapter.count / 2
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewPager.stopAutoScroll()
        banners.clear()
    }

    private inner class PosterPagerAdapter : PagerAdapter() {
        val itemViews = arrayListOf<View>()
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int {
            return if (banners.size > 1) Int.MAX_VALUE else banners.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return this@BannerView2.instantiateItem(container, position)
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
//            itemViews.add(`object`)
        }

    }

    @SuppressLint("InflateParams")
    private fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.banner_item_view, null
        )

        (view as TextView).text = banners[position % banners.size].toString()
        container.addView(view)
        return view
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        viewPager.startAutoScroll()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        viewPager.stopAutoScroll()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        lifecycle?.removeObserver(this)
    }
}
