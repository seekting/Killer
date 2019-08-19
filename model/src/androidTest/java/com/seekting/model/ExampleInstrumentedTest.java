package com.seekting.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.seekting.model.wenzikong.ApiManager;
import com.seekting.model.wenzikong.Detail;
import com.seekting.model.wenzikong.HomeData;
import com.seekting.model.wenzikong.ModelActivity;
import com.seekting.model.wenzikong.RetrofitManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.util.LogUtil;
import androidx.test.platform.app.InstrumentationRegistry;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private HomeData mHomeData;

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getContext();

        assertEquals("com.seekting.model.test", appContext.getPackageName());
    }

    @Test
    public void request() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(appContext, ModelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        InstrumentationRegistry.getInstrumentation().startActivitySync(intent);
//        assertEquals("com.seekting.model.test", appContext.getPackageName());
        RetrofitManager.getInstance().init();

        Log.d("seekting", "ExampleInstrumentedTest.request()");
        Observable<HomeData> observable = ApiManager.Companion.requestHomePage();
        observable.subscribe(new Consumer<HomeData>() {
            @Override
            public void accept(HomeData s) throws Exception {
                Log.d("seekting", "ExampleInstrumentedTest.accept()" + s);
                mHomeData = s;

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("seekting", "ExampleInstrumentedTest.accept()", throwable);
            }
        })
        ;
//        Context appContext = InstrumentationRegistry.getInstrumentation().getContext();
//        assertEquals("com.seekting.model.1test", appContext.getPackageName());
    }

    @Test
    public void testDetail() {
        LogUtil.logDebug("xxx", mHomeData + "");
//        ModelsItem m = mHomeData.getTabs().get(0).getModels().get(0);
        String url = "http://cdn5.ziti2.com/wenzikong/models/021/data.json";
        Observable<Detail> observable = ApiManager.Companion.requestDetail(url);
        observable.subscribe(new Consumer<Detail>() {
            @Override
            public void accept(Detail detail) throws Exception {

                Log.d("seekting", "ExampleInstrumentedTest.testDetail()" + detail);
                assertNotNull(detail);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("seekting", "ExampleInstrumentedTest.testDetail()", throwable);
                assertNotNull(null);
            }
        });
    }

}
