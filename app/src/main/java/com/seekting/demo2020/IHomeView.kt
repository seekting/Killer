package com.seekting.demo2020

import com.seekting.common.mvp.Ui
import com.seekting.model.wenzikong.HomeData

interface IHomeView : Ui {

    fun showLoading()
    fun showLoadFail()
    fun showLoadedSuc(data: HomeData)

}