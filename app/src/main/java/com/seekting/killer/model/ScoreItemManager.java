package com.seekting.killer.model;


import android.content.Context;
import android.util.Log;

import com.seekting.killer.App;
import com.seekting.utils.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

public class ScoreItemManager {

    private CountDownLatch mCountDownLatch = new CountDownLatch(1);
    private ScoreItem mScoreItem;

    private static class Holder {
        private static final ScoreItemManager instance = new ScoreItemManager();
    }

    public static ScoreItemManager getInstance() {
        return Holder.instance;
    }

    private ScoreItemManager() {


    }

    public void init(Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Stack<ScoreItem> scoreItems = new Stack<>();

//                    String[] array = new String[]{"", "\\t", "\\t\\t", "\\t\\t\\t"};
                    String[] array = new String[]{"", "    ", "        ", "            "};
                    InputStream inputStream = context.getAssets().open("score.txt");
                    IOUtil.eachLine(inputStream, new IOUtil.Callback() {
                        @Override
                        public boolean onRead(String line) {
                            if (line.startsWith("all")) {
                                scoreItems.push(new ScoreItem("0", "all", 0));


                            } else {
                                out:
                                for (int i = 3; i > 0; i--) {
                                    if (line.startsWith(array[i])) {
                                        int index = 0;
                                        while (!scoreItems.isEmpty()) {
                                            ScoreItem scoreItem = scoreItems.pop();
                                            index++;
                                            if (scoreItem.level == i - 1) {//它的上一级
                                                scoreItems.push(scoreItem);
                                                ScoreItem child = new ScoreItem(String.valueOf(index), line.trim(), i);
                                                scoreItems.push(child);
                                                scoreItem.mItems.add(child);
                                                break out;
                                            }

                                        }
                                    }

                                }
                            }


                            return true;
                        }
                    });
                    mScoreItem = scoreItems.firstElement();
                    print(mScoreItem);

                    Log.d("seekting", "ScoreItemManager.run()" + mScoreItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCountDownLatch.countDown();
            }
        }).start();
    }

    private static void print(ScoreItem scoreItem) {
        Stack<ScoreItem> scoreItems = new Stack<>();
        scoreItems.add(scoreItem);
        StringBuilder sb = new StringBuilder();

        StringBuilder stringBuilder = new StringBuilder();
        while (!scoreItems.isEmpty()) {
            ScoreItem scoreItem1 = scoreItems.pop();

            stringBuilder.setLength(0);
            for (int i = 0; i < scoreItem1.level; i++) {
                stringBuilder.append("    ");

            }
            stringBuilder.append(scoreItem1.name);
            stringBuilder.append("\n");
            sb.append(stringBuilder.toString());
            if (!scoreItem1.mItems.isEmpty()) {
                for (int i = scoreItem1.mItems.size() - 1; i >= 0; i--) {

                    scoreItems.push(scoreItem1.mItems.get(i));
                }
            }

        }
        Log.d("seekting", "ScoreItemManager.print()\n" + sb.toString());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            File file = new File(App.context.getDataDir(), "test_score.txt");
            IOUtil.write(file, sb.toString());
            Log.d("seekting", "ScoreItemManager.print()" + file.getAbsolutePath());
        }
    }

}

