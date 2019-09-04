package com.seekting;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaUtils {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static File createImageFile() throws IOException {
        // 唯一的文件名称
        String timeStamp = SIMPLE_DATE_FORMAT.format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File publicDirectory = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* 文件名前缀 */
                ".jpg",         /* 文件名后缀 */
                publicDirectory      /* 路径 */
        );

        return image;
    }

    public static File createVideoFile() {
        // 唯一的文件名称
        String timeStamp = SIMPLE_DATE_FORMAT.format(new Date());
        String imageFileName = "Video_" + timeStamp;
        File publicDirectory = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DCIM);
        File file = new File(publicDirectory, imageFileName + ".mp4");
//        File image = File.createTempFile(
//                imageFileName,  /* 文件名前缀 */
//                ".mp4",         /* 文件名后缀 */
//                publicDirectory      /* 路径 */
//        );

        return file;
    }
}
