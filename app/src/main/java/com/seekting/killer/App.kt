package com.seekting.killer

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.litesuits.common.data.DataKeeper
import com.seekting.killer.model.ScoreItemManager
import com.seekting.model.wenzikong.RetrofitManager
import com.umeng.commonsdk.UMConfigure

class App : MultiDexApplication() {

    companion object {
        lateinit var dataKeeper: DataKeeper
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        RetrofitManager.getInstance().init()
        UMConfigure.init(this, "5d874200570df35eae000dea", "unknown", UMConfigure.DEVICE_TYPE_PHONE,
                null)
        dataKeeper = DataKeeper(this, packageName)
        ScoreItemManager.getInstance().init(this);
    }
}