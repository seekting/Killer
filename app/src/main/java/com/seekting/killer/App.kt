package com.seekting.killer

import androidx.multidex.MultiDexApplication
import com.litesuits.common.data.DataKeeper
import com.seekting.model.wenzikong.RetrofitManager

class App : MultiDexApplication() {

    companion object {
        lateinit var dataKeeper: DataKeeper
    }

    override fun onCreate() {
        super.onCreate()
        RetrofitManager.getInstance().init()
        dataKeeper = DataKeeper(this, packageName)
    }
}