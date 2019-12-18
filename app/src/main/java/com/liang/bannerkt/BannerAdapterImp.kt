package com.liang.bannerkt

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.liang.banner.adapter.BannerAdapter
import com.liang.banner.adapter.BannerBindingAdapter

class BannerAdapterImp : BannerAdapter<String, BannerBindingAdapter.BannerHolder>() {

    val bg = arrayOf(
        android.R.color.holo_blue_bright,
        android.R.color.holo_green_dark,
        android.R.color.holo_red_light,
        android.R.color.holo_orange_light
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BannerBindingAdapter.BannerHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.banner_item_view, parent, false)
        return BannerBindingAdapter.BannerHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindBannerViewHolder(holder: BannerBindingAdapter.BannerHolder, position: Int) {
        holder.itemView.setBackgroundResource(bg[position % bg.size])
        (holder.itemView as TextView).text =
            "holder: ${getItem(position)}"
    }
}