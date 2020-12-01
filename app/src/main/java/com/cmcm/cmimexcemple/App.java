package com.cmcm.cmimexcemple;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import com.cmcm.cmimexcemple.event.RefreshConversationListEvent;
import com.cmcm.cmimexcemple.utils.LogUtils;
import com.im.imcore.IMBridger;
import com.im.imlogic.IMMsg;
import com.im.imlogic.LVIMSDK;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;

public class App extends Application {
    static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        initImSDK();
    }

    Handler mHandler = new Handler();

    /**
     * 初始化IM SDK
     */
    private void initImSDK() {

        // 设置进入调试状态，使用测试环境。
        LVIMSDK.sharedInstance().setDebugEnableState(true);
        // 打开日志
        LVIMSDK.sharedInstance().setLogVisibleState(true);
        // 初始化sdk，请在初始化之前调用设置appID和appKey、进入调试环境、打开日志、设置主机域名（setHost）等配置方法，否则不保证生效。
        // 请在官网申请appId和appSecret
        LVIMSDK.sharedInstance().initWithAppId(this, LvImConstants.APP_ID, LvImConstants.APP_SECRET);
        // 接收消息监听,必须设置,否则进入房间后设置监听房间消息无效。
        LVIMSDK.sharedInstance().setGlobalReceiveMessageListener(receiveMessageListener);
        // 设置更新token的回调
        LVIMSDK.sharedInstance().setEventListener(moduleEventListener);
    }

    IMBridger.IMReceiveMessageListener receiveMessageListener = new IMBridger.IMReceiveMessageListener() {
        @Override
        public boolean onIMReceiveMessageFilter(int cmdtype, int subtype, int diytype, int dataid, String fromid, String toid, String msgType, byte[] msgContent, int waitings, int packetSize, int waitLength, int bufferSize) {
            LogUtils.d(TAG, "onIMReceiveMessageFilter toid = " + toid + " fromid = " + fromid + "   msgContent = " + Arrays.toString(msgContent));
            ArrayList<IMBridger.IMReceiveMessageListener> messageListeners = Model.getInstance().getMessageListeners();
            for (IMBridger.IMReceiveMessageListener listener : messageListeners) {
                return listener.onIMReceiveMessageFilter(cmdtype, subtype, diytype, dataid, fromid, toid, msgType, msgContent, waitings, packetSize, waitLength, bufferSize);
            }
            return false;
        }

        @Override
        public int onIMReceiveMessageHandler(String owner, IMMsg msg, int waitings, int packetSize, int waitLength, int bufferSize) {
            LogUtils.d(TAG, "onIMReceiveMessageHandler ---------  owner = " + owner);
            // 收到消息刷新会话列表页。
            EventBus.getDefault().post(new RefreshConversationListEvent());
            ArrayList<IMBridger.IMReceiveMessageListener> messageListeners = Model.getInstance().getMessageListeners();
            for (IMBridger.IMReceiveMessageListener listener : messageListeners) {
                return listener.onIMReceiveMessageHandler(owner, msg, waitings, packetSize, waitLength, bufferSize);
            }
            return 0;
        }
    };

    boolean isTokenRequesting;
    IMBridger.IMModuleEventListener moduleEventListener = new IMBridger.IMModuleEventListener() {
        @Override
        public void onQueryIMToken() { // 请在此回调中请求并设置IM token。此回调会频繁调用，请注意加逻辑控制避免频繁请求。示例如下。

            LVIMSDK.sharedInstance().setIMToken(GlobalParams.loginUserId, "hamxxxxxx");

            // 当前如果在请求token状态，return;
//            if (isTokenRequesting) {
//                return;
//            }
//            LogUtils.d(TAG, "requestDebugToken");
//            isTokenRequesting = true;
//            /**
//             * 此方法仅在调试模式下有效，demo演示用。
//             * IM token请通过自己应用的服务端向IM SDK服务器请求，再转发给客户端，然后调用setIMToken方法。
//             */
//            LVIMSDK.sharedInstance().requestDebugToken(new LVIMSDK.RequestDebugTokenListener() {
//                @Override
//                public void onSucceed(String imToken) {
//                    LogUtils.d(TAG, "onResponse imToken = " + imToken);
//                    LVIMSDK.sharedInstance().setIMToken(GlobalParams.loginUserId, imToken);
//                    isTokenRequesting = false;
//                }
//
//                @Override
//                public void onFailed(Exception e) {
//                    isTokenRequesting = false;
//                    LogUtils.d(TAG, "获取IM token失败， onFailure: " + e);
//                }
//            });

        }

        @Override
        public void onIMAuthFailed(String uid, String token, int ecode, int rcode, boolean isTokenExpired) {
            // IM token校验失败会回调此方法。同时也回调onQueryIMToken().
            LogUtils.d(TAG, "IM token校验失败 错误码 = " + ecode);
        }

        @Override
        public void onIMAuthSucceed(String uid, String token, long unReadMsgSize) {
            // IM校验token通过
            LogUtils.d(TAG, "IM token校验通过 uid = " + uid + "    token =" + token);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(App.this, "IM token校验通过", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onIMTokenExpired(String uid, String token) {
            // IM token过期会回调此方法。同时也回调onQueryIMToken().
            LogUtils.d(TAG, "IM token过期 uid = " + uid + "    token =" + token);
        }
    };

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }


}
