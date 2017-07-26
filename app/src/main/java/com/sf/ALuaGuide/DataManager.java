package com.sf.ALuaGuide;

import android.content.Context;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 2017/7/22.
 */

public class DataManager {

    public static String[] fileArray = {"Lua教程.txt", "AndroLua帮助.txt", "实用代码.txt", "网络操作.txt", "文件操作.txt", "用户界面.txt", "基础代码.txt", "Intent类.txt", "笔记.txt"};

    public static List<Data> getData(Context context, String[] fileArray) {
        StringBuffer sb = new StringBuffer();

        for (String name : fileArray) {
            String str = "";
            try {
                InputStream in = context.getResources().getAssets().open(name);
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                str = new String(buffer, "utf8");
            } catch (Exception e) {
                ExceptionsHandle.show(context, e.toString());
                continue;
            }
            sb.append(str);
        }

        List<Data> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("《《(.*?)》》", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sb.toString());
        while (matcher.find()) {
            try {
                String title = matcher.group(1);
                matcher.find();
                String content = matcher.group(1);
                Data data = new Data(title, content);
                list.add(data);
            } catch (Exception e) {
                ExceptionsHandle.show(context, e.toString());
                continue;
            }
        }
        return list;
    }

    public static List<Data> getAll(Context context) {
        return getData(context, fileArray);
    }
}

class Data {
    String title;
    String content;

    public Data(String title, String content) {
        this.title = title;
        this.content = content;
    }
}