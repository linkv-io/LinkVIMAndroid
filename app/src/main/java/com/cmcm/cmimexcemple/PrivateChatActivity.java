package com.cmcm.cmimexcemple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmcm.cmimexcemple.adapter.MsgAdapter;
import com.cmcm.cmimexcemple.utils.LogUtils;
import com.cmcm.cmimexcemple.utils.SystemUtil;
import com.im.imcore.IMBridger;
import com.im.imlogic.IMMsg;
import com.im.imlogic.LVIMSDK;
import com.im.imlogic.LVPushContent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Xiaohong on 2020-05-22.
 * desc: 私信聊天界面
 */
public class PrivateChatActivity extends BaseActivity {
    private EditText mEtMsg;
    private RecyclerView mRecyclerView;

    private MsgAdapter mMsgAdapter;

    private static final String TAG = "PrivateChatActivity";
    private static final String KEY_OTHER_ID = "KEY_OTHER_ID";
    private String mOtherId;
    private TextView mTvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_private);

        initView();
        mOtherId = getIntent().getStringExtra(KEY_OTHER_ID);
        mTvTitle.setText(String.format(getString(R.string.chat_with_user), mOtherId));

        // 注册全局消息监听。
        Model.getInstance().addMessageListener(messageListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 反注册全局消息监听。
        Model.getInstance().removeMessageListener(messageListener);
    }

    IMBridger.IMReceiveMessageListener messageListener = new IMBridger.IMReceiveMessageListener() {
        @Override
        public boolean onIMReceiveMessageFilter(int cmdtype, int subtype, int diytype, int dataid, String fromid, String toid, String msgType, byte[] msgContent, int waitings, int packetSize, int waitLength, int bufferSize) {
            LogUtils.d(TAG, "onIMReceiveMessageFilter cmdtype = " + cmdtype + " fromid = " + fromid + "   msgContent = " + Arrays.toString(msgContent));
            // 返回true表示拦截，拦截后则不调用下面的onIMReceiveMessageHandler方法。
            return false;
        }

        @Override
        public int onIMReceiveMessageHandler(String owner, final IMMsg msg, int waitings, int packetSize, int waitLength, int bufferSize) {
            LogUtils.d(TAG, "消息的发送者ID：" + owner + " msg = " + msg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 刷新消息展示
                    mMsgAdapter.addMsg(msg);
                    mRecyclerView.smoothScrollToPosition(mMsgAdapter.getItemCount() - 1);
                }
            });
            return 0;
        }
    };

    private void initView() {
        mRecyclerView = findViewById(R.id.rv_msgs);
        mEtMsg = findViewById(R.id.et_msg);
        mTvTitle = findViewById(R.id.tv_title);
        mMsgAdapter = new MsgAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMsgAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 查出本地数据库所有消息
        ArrayList<IMMsg> localMsg = new ArrayList<>();
        mLVIMSDK.queryLocalPrivateHistoryMessage(mOtherId, 0, 50, false, localMsg);
        mMsgAdapter.setMsgList(localMsg);
        // 滚动到最近的消息
        mRecyclerView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
    }


    // 点击发送私信消息。
    public void send(View view) {
        String msgContent = mEtMsg.getText().toString();
        if (TextUtils.isEmpty(msgContent)) {
            // 提示输入消息内容和对方用户ID
            Toast.makeText(this, getString(R.string.tip_input_msg_or_target_id), Toast.LENGTH_LONG).show();
            return;
        }
        sendPrivateMsg(mOtherId, msgContent);

        mEtMsg.setText("");
        SystemUtil.hideKeyboard(this, mEtMsg);
    }


    private void sendEventMsg(String targetId, String msgContent) {
        String msgType = "can_not_be_null";
        LVPushContent lvPushContent = new LVPushContent();
        lvPushContent.setTitle("push title");
        lvPushContent.setBody("push body");
        lvPushContent.setExtra("push extra");

        IMMsg msg = IMMsg.buildEventMessage(targetId, msgType, msgContent, lvPushContent, null, null, null, null);
        mLVIMSDK.sendMessage(msg, this, new IMBridger.IMSendMessageListener() {
            @Override
            public void onIMSendMessageCallback(int ecode, String tid, final IMMsg msg, Object context) {
                LogUtils.d(TAG, "ecode = " + ecode + " tid = " + tid);
                if (ecode == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMsgAdapter.addMsg(msg);
                            mRecyclerView.smoothScrollToPosition(mMsgAdapter.getItemCount() - 1);
                        }
                    });
                }
            }
        });

    }


    private void sendPrivateMsg(String targetId, String msgContent) {
        String msgType = "can_not_be_null";
        LVPushContent lvPushContent = new LVPushContent();
        lvPushContent.setTitle("push title");
        lvPushContent.setBody("push body");
        lvPushContent.setExtra("push extra");
        IMMsg msg = IMMsg.buildTextPrivateMessage(targetId, msgType, msgContent, lvPushContent, null, null, null, null);
        mLVIMSDK.sendMessage(msg, this, new IMBridger.IMSendMessageListener() {
            @Override
            public void onIMSendMessageCallback(int ecode, String tid, final IMMsg msg, Object context) {
                LogUtils.d(TAG, "ecode = " + ecode + " tid = " + tid);
                if (ecode == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMsgAdapter.addMsg(msg);
                            mRecyclerView.smoothScrollToPosition(mMsgAdapter.getItemCount() - 1);
                        }
                    });
                }
            }
        });
    }


    /**
     * 开启activity
     *
     * @param context 上下文
     * @param userId  对方的用户ID
     */
    public static void actionStart(Context context, String userId) {
        Intent intent = new Intent(context, PrivateChatActivity.class);
        intent.putExtra(KEY_OTHER_ID, userId);
        context.startActivity(intent);
    }

    public void sendEvent(View view) {
        String msgContent = mEtMsg.getText().toString();
        if (TextUtils.isEmpty(msgContent)) {
            // 提示输入消息内容和对方用户ID
            Toast.makeText(this, getString(R.string.tip_input_msg_or_target_id), Toast.LENGTH_LONG).show();
            return;
        }
        sendEventMsg(mOtherId, msgContent);
        mEtMsg.setText("");
        SystemUtil.hideKeyboard(this, mEtMsg);
    }
}
