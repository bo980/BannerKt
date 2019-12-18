package com.liang.banner

import android.view.View
import kotlin.math.abs

class RotationTransformer @JvmOverloads constructor(
    orientation: Int = 0
) : BannerView.PageTransformer(orientation) {
    override fun transformPage(page: View, position: Float) {
        if (orientation == 0) {
            val width = page.width
            var pivotX = 0f
            if (position <= 1 && position > 0) {// right scrolling
                pivotX = 0f
            } else if (position < 0 && position >= -1) {// left scrolling
                pivotX = width.toFloat()
            }
            page.pivotX = pivotX
            page.rotationY = 90f * position
        } else {
            val height = page.height
            var pivotY = 0f
            if (position <= 1 && position > 0) {// down scrolling
                pivotY = 0f
            } else if (position < 0 && position >= -1) {// up scrolling
                pivotY = height.toFloat()
            }
            page.pivotY = pivotY
            page.rotationX = -90f * position
        }
    }
}


class ScaleTransformer @JvmOverloads constructor(
    private val minScale: Float = .5f,
    private val minAlpha: Float = 0.5f,
    orientation: Int = 0
) : BannerView.PageTransformer(orientation) {
    override fun transformPage(page: View, position: Float) {
        val alphaFactor = minAlpha + (1 - minAlpha) * (1 - abs(position))
        val scaleFactor = minScale + (1 - minScale) * (1 - abs(position))
        if (orientation == 0) {
            page.scaleY = scaleFactor
        } else {
            page.scaleX = scaleFactor
        }
        page.alpha = alphaFactor
    }
}
