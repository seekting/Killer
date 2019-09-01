package com.seekting.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//通过“addFormDataPart”可以添加多个上传的文件。
public class OkHttpCallBackWrap {

    private static OkHttpClient sOkHttpClient;

    public static void post(String url, File file, Callback callback) throws IOException {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("application/octet-stream", file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        if (sOkHttpClient == null) {
            final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
            sOkHttpClient = httpBuilder
                    //设置超时
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(150, TimeUnit.SECONDS)
                    .build();
        }
        sOkHttpClient.newCall(request).enqueue(callback);
    }

    public static void post(String url, File file) throws IOException {
        Callback callback = new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }

            @Override
            public void onFailure(Call arg0, IOException e) {
                // TODO Auto-generated method stub
                System.out.println(e.toString());

            }

        };
        post(url, file, callback);

    }
}