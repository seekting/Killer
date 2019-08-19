package com.seekting.common;

import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.io.InputStream;

public class ExecUtils {

    public static Pair<String, String> exec(String cmd) {

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream normal = process.getInputStream();
            InputStream error = process.getErrorStream();

            String normalString = IOUtil.readAndClose(normal);
            String errorString = IOUtil.readAndClose(error);

            Log.d("seekting", cmd + "-normal:" + normalString);
            Log.d("seekting", cmd + "-error:" + errorString);

            return new Pair<String, String>(normalString, errorString);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
