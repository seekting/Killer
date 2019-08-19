package com.seekting.demo2020

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.seekting.model.wenzikong.ApiManager
import com.seekting.model.wenzikong.HomeData
import io.reactivex.functions.Consumer

class HomePresenterImpl : IHomePresenter() {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        val observable = ApiManager.requestHomePage()
        observable.subscribe(object : Consumer<HomeData> {
            @Throws(Exception::class)
            override fun accept(s: HomeData) {
                Log.d("seekting", "ExampleInstrumentedTest.accept()$s")
                mUi.showLoadedSuc(s)
//                mHomeData = s

            }
        }, object : Consumer<Throwable> {
            @Throws(Exception::class)
            override fun accept(throwable: Throwable) {
                Log.d("seekting", "ExampleInstrumentedTest.accept()", throwable)
            }
        })
    }
}