package com.seekting.model.wenzikong

import android.app.Activity
import android.os.Bundle
import android.util.Log

class ModelActivity:Activity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitManager.getInstance().init()

        Log.d("seekting", "ExampleInstrumentedTest.request()")
        val observable = ApiManager.requestHomePage()
        observable.subscribe({ s -> Log.d("seekting", "ExampleInstrumentedTest.accept()$s") }, { throwable -> Log.d("seekting", "ExampleInstrumentedTest.accept()", throwable) })
    }
}