package com.seekting.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOUtil {
    private static final boolean DEBUG = false;
    private static final String TAG = "IOUtil";

    public static String readLine(File file, int count) {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {

            if (DEBUG) {
                System.out.println("read: " + file.getAbsolutePath());
            }
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            int readed = 0;
            String line = bufferedReader.readLine();
            readed++;
            stringBuilder.append(line);
            stringBuilder.append("\n");

            while (readed < count && line != null) {
                line = bufferedReader.readLine();
                readed++;
                stringBuilder.append(line);
                stringBuilder.append("\n");

            }
        } catch (IOException e) {
            System.out.println("read " + file.getAbsolutePath() + " fail !!");
        } finally {
            close(bufferedReader);

        }
        return stringBuilder.toString();

    }

    public static void write(File file, String toString) {
        File parent = new File(file.getParent());

        if (!parent.exists()) {
            parent.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            write(fileOutputStream, toString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            flushAndClose(fileOutputStream);
        }
    }

    public interface Callback {
        boolean onRead(String line);

        default void onReadEnd() {

        }

    }

    public static void eachLine(InputStream in, Callback callback) {
        BufferedReader bufferedReader = null;

        String line = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            line = bufferedReader.readLine();

            while (true) {

                if (line != null) {
                    boolean readON = callback.onRead(line);
                    if (!readON) {
                        break;
                    }
                } else {
                    break;
                }
                line = bufferedReader.readLine();
            }
            callback.onReadEnd();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void eachLine(File file, Callback callback) {

        BufferedReader bufferedReader = null;
        try {
            if (DEBUG) {
                System.out.println("read: " + file.getAbsolutePath());
            }
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = bufferedReader.readLine();
            while (true) {

                if (line != null) {
                    boolean readON = callback.onRead(line);
                    if (!readON) {
                        break;
                    }
                } else {
                    break;
                }
                line = bufferedReader.readLine();
            }
            callback.onReadEnd();
        } catch (IOException e) {
            System.out.println("read " + file.getAbsolutePath() + " fail !!");
        } finally {
            close(bufferedReader);

        }
    }

    public static boolean copy(InputStream inputStream, File outFile, CopyCallBack onCopyCallBack) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outFile);
            return IOUtil.copy(inputStream, fileOutputStream, onCopyCallBack);
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public interface CopyCallBack {

        void onCopy(long length);

        void onStartCopy();

        void onCopyEnd(long length);
    }

    public static boolean copy(InputStream inputStream, OutputStream outputStream, CopyCallBack onCopycallback) {

        if (inputStream == null || outputStream == null) {
            return false;
        }
        byte[] bytes = new byte[1024 * 1024 * 8];
        long copy = 0;
        try {
            if (onCopycallback != null) {
                onCopycallback.onStartCopy();
            }
            int length = 0;
            while (true) {
                length = inputStream.read(bytes);
                if (onCopycallback != null) {
                    copy += length;
                    onCopycallback.onCopy(copy);
                }
                if (length <= 0) {
                    break;
                }
                outputStream.write(bytes, 0, length);
            }
            outputStream.flush();
            if (onCopycallback != null) {
                onCopycallback.onCopyEnd(copy);
            }
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            close(inputStream);
            close(outputStream);
        }

    }

    public static String readAndClose(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


        StringBuilder sb = new StringBuilder();
        try {
            String line = bufferedReader.readLine();
            while (true) {

                if (line != null) {
                    sb.append(line);
                    sb.append("\n");
                } else {
                    break;
                }
                line = bufferedReader.readLine();
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

    public static void flushAndClose(Closeable cloneable) {
        if (cloneable != null) {
            try {
                if (cloneable instanceof Flushable) {
                    Flushable flushable = (Flushable) cloneable;
                    flushable.flush();

                }
                cloneable.close();
            } catch (IOException e) {
            }
        }
    }

    public static void close(Closeable cloneable) {
        if (cloneable != null) {
            try {
                cloneable.close();
            } catch (IOException e) {
            }
        }
    }

    public static void readPrintAndClose(InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


        try {
            String line = bufferedReader.readLine();
            while (true) {

                if (line != null) {
                    System.out.println(line);
                } else {
                    break;
                }
                line = bufferedReader.readLine();
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


    }

    public static String request(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();

            urlConnection.connect();

            int code = urlConnection.getResponseCode();
            System.out.println("code=" + code);

            InputStream inputStream = urlConnection.getInputStream();
            return readAndClose(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void download(String url, File outFile) {
        try {
            URL u = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();

            urlConnection.connect();

            int code = urlConnection.getResponseCode();
            System.out.println("code=" + code);

            InputStream inputStream = urlConnection.getInputStream();
            copy(inputStream, outFile, null);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void replace(File file, Pattern mPattern, String replaceStr) {
        try {
            File out = new File(file.getParent(), file.getName() + ".temp");
            final FileWriter fileWriter = new FileWriter(out);

            IOUtil.eachLine(file, new IOUtil.Callback() {
                @Override
                public boolean onRead(String line) {
                    Matcher matcher = mPattern.matcher(line);
                    if (matcher.find()) {
                        String version = matcher.group(1);
                        System.out.println("begion replace:" + version + " to " + replaceStr);
                        line = line.replace(version, replaceStr);
                    }
                    try {
                        fileWriter.write(line + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return true;

                }
            });
            fileWriter.flush();
            fileWriter.close();
            file.delete();
            out.renameTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(OutputStream dataOutputStream, String str) {
        try {
            dataOutputStream.write((str + " \n").getBytes());
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void write(Writer writer, String msg) {
        try {
            writer.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeLine(Writer writer, String msg) {
        try {
            writer.write(msg);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String read(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] bytes = new byte[1024 * 8];

            while (true) {
                int length = fileInputStream.read(bytes);
                if (length > 0) {
                    byteArrayOutputStream.write(bytes, 0, length);
                } else {
                    break;
                }
            }
            return byteArrayOutputStream.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(fileInputStream);
        }

        return null;
    }
}
