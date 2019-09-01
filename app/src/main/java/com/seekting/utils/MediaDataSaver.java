package com.seekting.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class MediaDataSaver {

    public static final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     * 保存后用广播扫描，Android4.4以下使用这个方法
     *
     * @author YOLANDA
     */
    public static void saveBroadcast(Context context, Bitmap bitmap) {
        String filePath = saveImg(context, true, bitmap);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
        Toast.makeText(context, "保存成功：" + filePath, Toast.LENGTH_LONG).show();
    }

    /**
     * 保存后用MediaScanner扫描，通用的方法
     *
     * @author YOLANDA
     */
    public static void saveScan(Context context, Bitmap bitmap) {
        String filePath = saveImg(context, false, bitmap);
        MediaScanner mediaScanner = new MediaScanner(context);
        String[] filePaths = new String[]{filePath};
        String[] mimeTypes = new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension("png")};
        mediaScanner.scanFiles(filePaths, mimeTypes);
        Toast.makeText(context, "保存成功：" + filePath, Toast.LENGTH_LONG).show();
    }


    /**
     * 保存图片到SD卡
     *
     * @param isInsertGallery 是否保存到图库
     * @return
     * @author YOLANDA
     */
    public static String saveImg(Context context, boolean isInsertGallery, Bitmap bitmap) {
        File myappDir = new File(Environment.getExternalStorageDirectory(), "killer");
        if (myappDir.exists() && myappDir.isFile()) {
            myappDir.delete();
        }
        if (!myappDir.exists()) {
            myappDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(myappDir, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isInsertGallery) {
            try {
                android.provider.MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

}
