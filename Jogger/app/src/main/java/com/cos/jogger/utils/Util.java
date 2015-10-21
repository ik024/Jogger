package com.cos.jogger.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

/**
 * Created by admin1 on 10/21/2015.
 */
public class Util {

    private static final String TAG = Util.class.getSimpleName();

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Log.d(TAG, ""+TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics));
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

}
