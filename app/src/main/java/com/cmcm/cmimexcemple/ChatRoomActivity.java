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
import com.cmcm.cmimexcemple.chatroom.RoomListAdapter;
import com.cmcm.cmimexcemple.utils.LogUtils;
import com.cmcm.cmimexcemple.utils.SystemUtil;
import com.im.imcore.IMBridger;
import com.im.imlogic.IMMsg;

import java.util.Arrays;

/**
 * Created by Xiaohong on 2020-05-22.
 * desc:创建和加入房间，发送房间消息。
 */
public class ChatRoomActivity extends BaseActivity {
    private EditText mEtRoomMsg;
    private RecyclerView mRecyclerView;
    private MsgAdapter mMsgAdapter;
    String mRoomId = "";
    private static final String TAG = "ChatRoomActivity";
    private final int FLAG_JOIN_ROOM = 1001;
    private final int FLAG_SEND_ROOM_MSG = 1002;
    private TextView mTvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        initView();
        mRoomId = getIntent().getStringExtra(KEY_ROOM_ID);
        String roomName;
        if (RoomListAdapter.DEFAULT_ROOM_ID.equals(mRoomId)) {
            roomName = getString(R.string.default_room);
        } else {
            roomName = String.format(getString(R.string.title_chat_room), mRoomId);
        }
        mTvTitle.setText(roomName);

        enterRoom();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rv_msgs);
        mEtRoomMsg = findViewById(R.id.et_room_msg);
        mTvTitle = findViewById(R.id.tv_title);
        mMsgAdapter = new MsgAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMsgAdapter);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 设置标题栏为房间号。
            actionBar.setTitle("房间: " + mRoomId + "" + String.format(getString(R.string.login_succeed_uid), GlobalParams.loginUserId));
        }
    }


    /**
     * 进入房间
     */
    private void enterRoom() {
        // 加入房间
        mLVIMSDK.joinChatRoom(mRoomId, FLAG_JOIN_ROOM, roomEventListener);
        // 接收房间消息监听
        mLVIMSDK.setChatroomReceiveMessageListener(receiveMessageListener);
    }

    IMBridger.IMReceiveMessageListener receiveMessageListener = new IMBridger.IMReceiveMessageListener() {
        @Override
        public boolean onIMReceiveMessageFilter(int cmdtype, int subtype, int diytype, int dataid, String fromid, String toid, String msgType, byte[] msgContent, int waitings, int packetSize, int waitLength, int bufferSize) {
            LogUtils.d(TAG, "onIMReceiveMessageFilter toid = " + toid + " fromid = " + fromid + "   msgContent = " + Arrays.toString(msgContent));
            // 返回true表示拦截，拦截后则不调用下面的onIMReceiveMessageHandler方法。
            return false;
        }

        @Override
        public int onIMReceiveMessageHandler(String owner, final IMMsg msg, int waitings, int packetSize, int waitLength, int bufferSize) {
            LogUtils.d(TAG, "消息的发送者ID：" + owner + " msg content = " + msg.getMsgContent());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 添加到消息列表展示。
                    mMsgAdapter.addMsg(msg);
                    mRecyclerView.smoothScrollToPosition(mMsgAdapter.getItemCount() - 1);
                }
            });
            return 0;
        }
    };

    IMBridger.IMSendMessageListener roomEventListener = new IMBridger.IMSendMessageListener() {
        @Override
        public void onIMSendMessageCallback(int ecode, String tid, IMMsg msg, Object context) {
            LogUtils.d(TAG, "加入或者房间指令返回码：" + ecode + " 房间号 = " + tid + " 指令标记  =" + context);
            if (context instanceof Integer && ecode == 0) {
                int flag = (int) context;
                if (flag == FLAG_JOIN_ROOM) {
                    Toast.makeText(ChatRoomActivity.this.getApplicationContext(), "加入房间成功", Toast.LENGTH_SHORT).show();
                } else if (flag == FLAG_SEND_ROOM_MSG) {
                    mMsgAdapter.addMsg(msg);
                    mRecyclerView.smoothScrollToPosition(mMsgAdapter.getItemCount() - 1);
                }
            }
        }
    };

    // 点击发送房间消息。
    public void send(View view) {
        String targetId = mRoomId;
        String msgContent = mEtRoomMsg.getText().toString();
        if (targetId == null || TextUtils.isEmpty(msgContent)) {
            Toast.makeText(this, getString(R.string.pls_input_msg), Toast.LENGTH_SHORT).show();
            return;
        }
        String msgType = "msgType";
        IMMsg msg = IMMsg.buildChatRoomMessage(targetId, msgType, msgContent);
        sendMsg(msg);
        mEtRoomMsg.setText("");
        SystemUtil.hideKeyboard(this, mEtRoomMsg);
    }

    private void sendMsg(IMMsg msg) {
        mLVIMSDK.sendMessage(msg, FLAG_SEND_ROOM_MSG, roomEventListener);
    }

    static final String KEY_ROOM_ID = "KEY_ROOM_ID";

    public static void actionStart(Context context, String rid) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(KEY_ROOM_ID, rid);
        context.startActivity(intent);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLVIMSDK.leaveChatRoom(mRoomId, null, null);
    }
}
