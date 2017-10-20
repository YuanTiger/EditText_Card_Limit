package com.my.oo.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Author：mengyuan
 * Date  : 2017/5/19下午5:22
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class AppUtils {

    /**
     * 根据信用卡格式添加空格，每4位添加一个空格
     *
     * @param content
     * @return
     */
    public static String addSpeaceByCredit(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        content = content.replaceAll(" ", "");
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        if (content.length() > 16) {
            content = content.substring(0, 16);
        }
        StringBuilder newString = new StringBuilder();
        for (int i = 1; i <= content.length(); i++) {
            if (i % 4 == 0 && i != content.length()) {
                newString.append(content.charAt(i - 1) + " ");
            } else {
                newString.append(content.charAt(i - 1));
            }
        }
//        Log.i("mengyuan", "添加空格后："+newString.toString());
        return newString.toString();
    }
}
