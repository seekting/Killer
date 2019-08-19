package com.seekting.model.wenzikong

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.http.GET
import retrofit2.http.Url


interface ApiService {
    //
    @GET("/wenzikong/data.json")
    fun requestHomePage(): Observable<HomeData>

    @GET
    fun requestDetail(@Url url: String): Observable<Detail>
}

class ApiManager {


    companion object {
        fun requestHomePage(): Observable<HomeData> {
            return RetrofitManager.getInstance().getRetrofit()
                    //动态代理创建GithubAPI对象
                    .create(ApiService::class.java)
                    .requestHomePage()
                    //指定上游发送事件线程
                    .subscribeOn(Schedulers.computation())
                    //指定下游接收事件线程
                    .observeOn(AndroidSchedulers.mainThread())
        }

        fun requestDetail(url: String): Observable<Detail> {
            return RetrofitManager.getInstance().getRetrofit()
                    //动态代理创建GithubAPI对象
                    .create(ApiService::class.java)
                    .requestDetail(url)
                    //指定上游发送事件线程
                    .subscribeOn(Schedulers.computation())
                    //指定下游接收事件线程
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

}