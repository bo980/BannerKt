package com.liang.bannerkt

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.liang.banner.Indicator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter by lazy {
        BannerAdapterImp()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bannerView.addIndicator(bannerIndicator)
        bannerView.adapter = adapter
        adapter.submit(arrayListOf("001","002","003","004","005","006","007"))
    }
}
