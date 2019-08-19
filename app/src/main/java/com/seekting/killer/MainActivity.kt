package com.seekting.killer

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.seekting.common.mvp.MVPActivity
import com.seekting.demo2020.HomePresenterImpl
import com.seekting.demo2020.IHomePresenter
import com.seekting.demo2020.IHomeView
import com.seekting.model.wenzikong.HomeData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MVPActivity<IHomePresenter, IHomeView>(), IHomeView {


    lateinit var binding: com.seekting.killer.databinding.ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setContentView(R.layout.activity_main)
        lifecycle.addObserver(mPresenter)
    }

    override fun createPresenter(): IHomePresenter {
        return HomePresenterImpl()
    }

    override fun createUi(): IHomeView {
        return this
    }

    override fun showLoading() {
        binding.progressView.visibility = View.VISIBLE
        binding.indicator.visibility = View.GONE
        binding.viewPager.visibility = View.GONE
    }

    override fun showLoadFail() {
        binding.progressView.visibility = View.GONE
        indicator.visibility = View.GONE
        binding.viewPager.visibility = View.GONE
    }

    override fun showLoadedSuc(data: HomeData) {
        binding.progressView.visibility = View.GONE
        indicator.visibility = View.VISIBLE
        binding.viewPager.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mPresenter)
    }
}
