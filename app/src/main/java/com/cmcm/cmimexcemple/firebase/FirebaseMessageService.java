package com.cmcm.cmimexcemple.firebase;

import androidx.annotation.NonNull;

import com.cmcm.cmimexcemple.utils.LogUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.im.imcore.IMBridger;
import com.im.imlogic.LVIMSDK;
import com.im.imlogic.utils.LvImLogs;

import java.util.Map;

public class FirebaseMessageService extends FirebaseMessagingService {
    final String TAG = "FirebaseMessageService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        LvImLogs.d(TAG, "onMessageReceived");

        Map<String, String> data = remoteMessage.getData();
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            LvImLogs.d(TAG, "title = " + notification.getTitle());
            LvImLogs.d(TAG, "body = " + notification.getBody());
            LvImLogs.d(TAG, "clickAction = " + notification.getClickAction());
            LvImLogs.d(TAG, "sound = " + notification.getSound());
        }

        for (String key : data.keySet()) {
            Object value = data.get(key);
            LvImLogs.d(TAG, "onMessageReceived data ---  key = " + key + " value = " + value);
        }

    }


    @Override
    public void onDeletedMessages() {
        LvImLogs.d(TAG, "onDeletedMessages ");
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        LvImLogs.d(TAG, "onNewToken token = " + token);

        int result = LVIMSDK.sharedInstance().uploadPushToken(token, new IMBridger.IMUploadPushTokenListener() {
            @Override
            public void onIMUploadPushTokenCallback(int eCode, String msg) {
                LvImLogs.d(TAG, "eCode = " + eCode + "  msg = " + msg);
            }
        });
//        if (result != 0) {
//            LogUtils.e(this, " 更新push token失败！！");
//        }
//        Looper.prepare();
//        Toast.makeText(this, "推送令牌更新", Toast.LENGTH_SHORT).show();
//        Looper.loop();
    }


}
