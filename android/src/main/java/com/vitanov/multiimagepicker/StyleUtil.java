package com.vitanov.multiimagepicker;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xia-weiyang on 18-12-13.
 */
public class StyleUtil {

    private static final String SHARED_PREFERENCES_NAME = "FlutterSharedPreferences";

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static int getCurrentStyle(Context context){
        String themeKey = getSharedPreferences(context).getString("flutter.themeKey","default");

        switch (themeKey){
            case "default": return R.style.Matisse_Default;
            case "black": return R.style.Matisse_Black;
        }

        return R.style.Matisse_Customize;
    }
}
