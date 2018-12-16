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
            case "blue": return R.style.Matisse_Blue;
            case "grey": return R.style.Matisse_Grey;
            case "brown": return R.style.Matisse_Brown;
            case "blueLight": return R.style.Matisse_BlueLight;
            case "cyan": return R.style.Matisse_Cyan;
            case "green": return R.style.Matisse_Green;
            case "red": return R.style.Matisse_Red;
            case "pink": return R.style.Matisse_Pink;
            case "orange": return R.style.Matisse_Orange;
        }

        return R.style.Matisse_Customize;
    }
}
