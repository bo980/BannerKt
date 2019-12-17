package com.liang.banner

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout

class BannerIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Indicator {

    private var widthDip = 0
    private var heightDip = widthDip
    private var marginDip = widthDip

    private var itemViewBackgroundId = 0

    private val itemView by lazy {
        LinearLayout(context).apply {
            gravity = Gravity.CENTER
        }
    }

    init {
        init(attrs, defStyleAttr)
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicator, defStyle, 0)
        this.widthDip = a.getDimensionPixelSize(R.styleable.BannerIndicator_indicatorItemWidth, 10)
        this.heightDip =
            a.getDimensionPixelSize(R.styleable.BannerIndicator_indicatorItemHeight, widthDip)
        this.marginDip =
            a.getDimensionPixelSize(R.styleable.BannerIndicator_indicatorItemMargin, widthDip)
        this.itemViewBackgroundId =
            a.getResourceId(
                R.styleable.BannerIndicator_indicatorItemBackground,
                R.drawable.banner_indicator_background
            )
        a.recycle()
        addView(itemView)
    }

    override fun initCount(count: Int) {
        itemView.removeAllViews()
        repeat(count) {
            val view = View(context)
            view.setBackgroundResource(itemViewBackgroundId)
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(widthDip, heightDip)
            params.leftMargin = marginDip
            view.layoutParams = params
            itemView.addView(view)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        repeat(itemView.childCount) {
            itemView.getChildAt(it)?.isSelected = it == position
        }
    }
}