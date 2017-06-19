package com.web.yg.festival;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by jink7 on 2017-05-24.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String msg;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // 푸시 메시지가 서버로 부터 왔을때 콜백 받는 메서드!!

        // 서버에서 데이터가 왔을 때 Log로 출력
        Map<String, String> data = remoteMessage.getData();

        if (data != null && data.size() > 0) {
            // json 파싱처리 한다.
            Gson gson = new Gson();
            // data -> json 문자열 변환
            String pushSendStr = gson.toJson(data);

            // json -> bean 변환
            PushMsgBean.Data pushMsgBean = gson.fromJson(pushSendStr, PushMsgBean.Data.class);
            noti(pushMsgBean.getTitle(), pushMsgBean.getMessage());
            return;
        }
        noti("Festival", msg);

    } // end of onMessage~

    public void noti(String title, String content) {
        // 노티 테스트
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Festival")
                .setSmallIcon(R.mipmap.notiimg)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notiimg))
                .setTicker(title)
                .setContentText(content)
                .setAutoCancel(true)

                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notiMng = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //클릭할 때까지 액티비티 실행을 보류하고 있는 PendingIntent 객체 생성

        PendingIntent pending= PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notiBuilder.setContentIntent(pending);   //PendingIntent 설정
        notiMng.notify((int) System.currentTimeMillis(), notiBuilder.build());

        notiBuilder.setAutoCancel(true);         //클릭하면 자동으로 알림 삭제
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, MainActivity.class), 0);
//        notiBuilder.setContentIntent(contentIntent);
    } // end of noti
}
