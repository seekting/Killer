package com.seekting.killer

import androidx.multidex.MultiDexApplication
import com.litesuits.common.data.DataKeeper
import com.seekting.model.wenzikong.RetrofitManager
import com.umeng.commonsdk.UMConfigure

class App : MultiDexApplication() {

    companion object {
        lateinit var dataKeeper: DataKeeper
    }

    override fun onCreate() {
        super.onCreate()
        RetrofitManager.getInstance().init()
        UMConfigure.init(this, "5d874200570df35eae000dea", "unknown", UMConfigure.DEVICE_TYPE_PHONE,
                null)
        dataKeeper = DataKeeper(this, packageName)
    }
}