package com.web.yg.festival;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.iid.FirebaseInstanceId;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView; // 안드로이드 화면에서 웹사이트를 볼 웹뷰선언
    private JSInterface mJSInterface; // 자바스크립트 인터페이스 선언
    private BackPressCloseHandler backPressCloseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(intent);
         /* 뒤로가기 종료 버튼 */
        backPressCloseHandler = new BackPressCloseHandler(this);
        ////////////////////////////////////////////////////////////////////////////////////////////
        // webView 셋팅
        mWebView = (WebView) findViewById(R.id.webView);
        // 자바스크립트 사용 셋팅
        mWebView.getSettings().setLoadWithOverviewMode(true); // 웹뷰에서 페이지가 확대되는 문제해결
        /* 웹뷰에서 인터넷으로 켜지는 기능 안되게*/
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setInitialScale(1); // 기기별 화면사이트에 맞게 조절
        mWebView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 사용하겠다

        mJSInterface = new JSInterface();
        // 키값이 html에서 window.mJSInterface 함수로 사용된다. 웹에서 안드로이드 함수 호출하는것.
        mWebView.addJavascriptInterface(mJSInterface, "mJSInterface"); // 여기서 Object, String 파라미터 값 중, String이 중요한 것이여~!


        // test용
//        mWebView.loadUrl("file:///android_asset/www/sample.html"); // assets 폴더에 있는 메인 페이지 로딩
        // 웹 연결
//        mWebView.loadUrl("http://kostafesta.cafe24.com/"); // 안드로이드 실행될때, 웹사이트 주소를 호출
        mWebView.loadUrl("http://192.168.0.102:8082/"); // 안드로이드 실행될때, 웹사이트 주소를 호출

        // Get updated InstanceID token.
        // 기기의 토큰정보를 담는다.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("TEST", "Refreshed token: " + refreshedToken);
        if (refreshedToken != null && refreshedToken.length() > 0) {
            PrefUtil.setPreference(this, PrefUtil.KEY_PUSH_TOKEN, refreshedToken);
        }
    } // end of onCreate

    /* 앱 종료 */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    // 자바스크립트 인터페이스 클래스
    class JSInterface {
        // 스크립트로 아래 메서드를 호출합니다. @JavascriptInterface가 중요하다.

        @JavascriptInterface
        public void updateAndToken(String memberId) {
            String token = PrefUtil.getPreference(MainActivity.this, PrefUtil.KEY_PUSH_TOKEN);
            Log.d("LOGIN",memberId +"," + token);
            new TokenSendTask(memberId, token).execute(); // 서버로 전송하는 스레드 시작
        }
    } // end of 자바스크립트 인터페이스

    //    AsyncTask< doInBackground()의 파라미터, update~관련 오버라이드 메서드의 파라미터 값, return 값>
    private class TokenSendTask extends AsyncTask<String, Void, String> {

        private String memberId, token;

        public TokenSendTask(String memberId, String token) {
            this.memberId = memberId;
            this.token = token;
        }

        @Override
        protected String doInBackground(String... params) {
//            String sendUrl = "http://kostafesta.cafe24.com/updatePushToken.do";
            String sendUrl = "http://192.168.0.102:8082/updatePushToken.do";
//
            try {
                RestTemplate restT = new RestTemplate();
                restT.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restT.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
                map.add("memberId", memberId);
                map.add("token", token);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

                return restT.postForObject(sendUrl, request, String.class);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    } // end of async태스크
} // end of class
