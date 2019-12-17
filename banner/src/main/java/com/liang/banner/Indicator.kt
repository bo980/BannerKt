package com.liang.banner

interface Indicator {

    fun initCount(count: Int)

    fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    )

    fun onPageSelected(position: Int)
}