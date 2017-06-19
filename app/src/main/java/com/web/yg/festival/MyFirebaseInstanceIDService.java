package com.web.yg.festival;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by jink7 on 2017-05-24.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // 단말기 고유ID = 토큰 을 발급 받는다.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken(); // 토큰을 발급받는다.
        Log.d("TEST", refreshedToken);

        // 토큰값 저장
        if (refreshedToken != null && refreshedToken.length() > 0) {
            PrefUtil.setPreference(this, PrefUtil.KEY_PUSH_TOKEN, refreshedToken);
        }
    } // end of onTokenRefresh
} // end of class
