package com.liang.banner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BannerBindingAdapter<T> : BannerAdapter<T, BannerBindingAdapter.BannerHolder>() {

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerHolder {
        var view: View? = onCreateView(parent, viewType)
        if (view == null) {
            val viewDataBinding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                getItemLayoutId(viewType),
                parent,
                false
            )
            view = viewDataBinding.root
        }
        return BannerHolder(view)
    }

    protected abstract fun getItemLayoutId(viewType: Int): Int

    protected open fun onCreateView(parent: ViewGroup, viewType: Int): View? {
        return null
    }

    final override fun onBindBannerViewHolder(holder: BannerHolder, position: Int) {
        val viewDataBinding =
            DataBindingUtil.getBinding<ViewDataBinding>(holder.itemView)
        onBindViewHolder(viewDataBinding, getItem(position), position)
    }

    protected abstract fun onBindViewHolder(
        viewDataBinding: ViewDataBinding?,
        item: T,
        position: Int
    )

    class BannerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

