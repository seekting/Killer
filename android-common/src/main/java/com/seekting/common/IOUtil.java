package com.seekting.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtil {

    public static String readAndClose(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


        StringBuilder sb = new StringBuilder();
        try {
            String line = bufferedReader.readLine();
            while (true) {
                sb.append(line);
                line = bufferedReader.readLine();
                if (line != null) {
                    sb.append("\n");
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return sb.toString();


    }
}
