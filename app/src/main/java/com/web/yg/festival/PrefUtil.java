package com.web.yg.festival;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jink7 on 2017-05-25.
 */

public class PrefUtil {
    // 푸시 토큰값을 저장하는 키 정의
    public static final String KEY_PUSH_TOKEN = "keyPushToken";

    public static void setPreference (Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + "Festival", Activity.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getPreference(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + "Festival", Activity.MODE_PRIVATE);
        return prefs.getString(key, "");
    }
} // end of class
