package com.liang.banner.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class BannerAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    private val banners = arrayListOf<T>()

    final override fun getItemCount(): Int {
        return if (banners.size > 1) Int.MAX_VALUE else banners.size
    }

    fun submit(items: Collection<T>) {
        banners.addAll(items)
        notifyDataSetChanged()
    }

    final override fun onBindViewHolder(holder: VH, position: Int) {
        onBindBannerViewHolder(holder, position % banners.size)
    }

    final override fun getItemViewType(position: Int): Int {
        return getBannerItemViewType(position % banners.size)
    }

    final override fun getItemId(position: Int): Long {
        return getBannerItemId(position % banners.size)
    }

    protected open fun getBannerItemId(position: Int): Long {
        return RecyclerView.NO_ID
    }

    protected open fun getBannerItemViewType(position: Int): Int {
        return 0
    }

    protected abstract fun onBindBannerViewHolder(holder: VH, position: Int)

    fun getItem(position: Int): T {
        return banners[position]
    }

    fun getBannerCount(): Int {
        return banners.size
    }
}

