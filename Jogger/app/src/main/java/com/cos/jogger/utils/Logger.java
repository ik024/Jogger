package com.cos.jogger.utils;

import android.util.Log;

import com.cos.jogger.BuildConfig;

/**
 * Created by admin1 on 10/16/2015.
 */
public class Logger {

    public static void d(String key, String value){//debug
        if (BuildConfig.DEBUG) {
            Log.d(key, value);
        }
    }

    public static void i(String key, String value){//information
        if (BuildConfig.DEBUG) {
            Log.d(key, value);
        }
    }

    public static void e(String key, String value){//error
        if (BuildConfig.DEBUG) {
            Log.d(key, value);
        }
    }
}
