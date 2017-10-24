package com.my.oo.utils;

import android.text.TextUtils;

/**
 * Author：mengyuan
 * Date  : 2017/2/8下午6:53
 * E-Mail:mengyuanzz@126.com
 * Desc  :正则工具类
 */

public class MatcheUtils {


    public static boolean isCreditNumber(String idCard) {
        return !TextUtils.isEmpty(idCard) && idCard.matches("^\\d{16}$");
    }

    public static boolean isBankNumber(String bankNumber) {
        return !TextUtils.isEmpty(bankNumber) && (bankNumber.matches("^\\d{16}$") || bankNumber.matches("^\\d{19}$"));
    }


}
