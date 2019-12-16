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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * BannerView
 */
class BannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(
    context,
    attrs,
    defStyle
), LifecycleObserver {

    private val defaultInterval = 5000L

    private var lifecycle: Lifecycle? = null
    private var direction = BannerViewPager.RIGHT

    private val runnable by lazy {
        Runnable {
            next()
            start()
        }
    }

    var interval = defaultInterval

    private val viewPager by lazy {
        ViewPager2(context, attrs, defStyle).apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                }
            })
        }
    }

    private val adapter by lazy {
        object : RecyclerView.Adapter<BannerHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): BannerHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.banner_item_view, parent, false)
                return BannerHolder(view)
            }

            override fun getItemCount(): Int {
                return if (banners.size > 1) Int.MAX_VALUE else banners.size
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: BannerHolder, position: Int) {
                (holder.itemView as TextView).text =
                    "holder: ${banners[position % banners.size]}"
            }
        }
    }

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
                addObserver(this@BannerView)
            }
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.BannerView, defStyle, 0
        )
        a.recycle()
        addView(viewPager)
        viewPager.adapter = adapter
        viewPager.viewTreeObserver.addOnGlobalLayoutListener {
            viewPager.setCurrentItem(adapter.itemCount / 2, false)
        }
    }


    @Synchronized
    private fun start() {
        removeCallbacks(runnable)
        postDelayed(runnable, interval)
    }

    private fun next() {
        val totalCount = adapter.itemCount
        var currentItem = viewPager.currentItem
        if (totalCount > 1) {
            val nextItem =
                if (direction == LEFT) --currentItem else ++currentItem
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

    private fun stop() {
        removeCallbacks(runnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onPause()
        banners.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        start()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        lifecycle?.removeObserver(this)
    }

    class BannerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val LEFT = 0
        const val RIGHT = 1
    }
}
