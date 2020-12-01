package com.cmcm.cmimexcemple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmcm.cmimexcemple.conversation.ConversationAdapter;
import com.cmcm.cmimexcemple.utils.LogUtils;
import com.im.imcore.IMBridger;
import com.im.imlogic.IMMsg;
import com.im.imlogic.LVIMSDK;
import com.im.imlogic.LVIMSession;
import com.im.imlogic.utils.LvImLogs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2020-09-02.
 * desc: 会话列表
 */
public class ConversationListActivity extends BaseActivity {
    private EditText mEtMsg;
    private EditText mEtTargetUserId;
    private RecyclerView mRecyclerView;

    private ConversationAdapter conversationAdapter;

    private static final String TAG = "SessionListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rv_conversations);
        mEtMsg = findViewById(R.id.et_msg);
        mEtTargetUserId = findViewById(R.id.et_target_user_id);
        conversationAdapter = new ConversationAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(conversationAdapter);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 设置标题栏为房间号。
            actionBar.setTitle(String.format(getString(R.string.login_succeed_uid), GlobalParams.loginUserId));
        }
    }


    // 获取远程会话列表
    public void queryRemoteConversations() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                final ArrayList<LVIMSession> list = new ArrayList<>();
                int result = LVIMSDK.sharedInstance().queryRemoteSessionList(1, 10, new IMBridger.IMQueryRemoteSessionListListener() {
                    @Override
                    public void onIMQueryRemoteSessionListCallback(int ecode, int rcode, int rstatus, List<IMMsg> sessions) {
                        for (IMMsg msg :sessions){
                            LogUtils.d(TAG,"remote sessions msg = " + msg);
                        }
                    }
                });
            }
        }).start();
    }

    // 获取远程历史消息
    public void queryRemoteSessionMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = LVIMSDK.sharedInstance().queryRemoteSessionMessage(mEtTargetUserId.getText().toString(), 0,20, new IMBridger.IMQueryRemoteSessionMessageListener(){
                    @Override
                    public void onIMQueryRemoteSessionListCallback(int ecode, int rcode, int rstatus, List<IMMsg> messages) {
                        LvImLogs.d(TAG,"ecode = " + ecode + " rstatus = " + rstatus);
                        if (messages != null && messages.size() > 0){
                            for (IMMsg imMsg : messages){
                                LvImLogs.d(TAG, "queryRemoteSessionMessage imMsg = " + imMsg);
                            }
                        }
                    }
                });
            }
        }).start();
    }




    // 获取列表
    public void queryConversations() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<LVIMSession> list = new ArrayList<>();
                LVIMSDK.sharedInstance().querySessionList(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        conversationAdapter.addConversations(list);
                    }
                });
            }
        }).start();
    }


    /**
     * 开启activity
     *
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ConversationListActivity.class);
        context.startActivity(intent);
    }


    //
    public void onClick(View view) {
        String targetId;
        switch (view.getId()) {
            case R.id.btn_query_conversation:
//                queryRemoteConversations();
//                queryConversations();
//                queryLocalPrivateHistoryMessage();
                queryRemoteSessionMessage();
                break;
            case R.id.btn_all_unread:
                setAllUnread();
//                setPrivateDBStorageMax(5);
                break;
            case R.id.btn_conversation_unread:
                targetId = mEtTargetUserId.getText().toString();
                setConversationUnread(targetId);
                break;
            case R.id.btn_unread_before_time:
                targetId = mEtTargetUserId.getText().toString();
                setUnReadBeforeTime(targetId);
                break;

        }
    }

    // 设置数据库最大私信消息数
    private void setPrivateDBStorageMax(int max) {
        LVIMSDK.sharedInstance().setPrivateDBStorageMax(max);
    }

    // 查询本地私信消息。
    private void queryLocalPrivateHistoryMessage() {
        List<IMMsg> list = new ArrayList<>();
        LVIMSDK.sharedInstance().queryLocalPrivateHistoryMessage(mEtTargetUserId.getText().toString(),99,20,true,list);
        LogUtils.d(TAG,"list.size  = "+list.size());
    }

    // 把某个会话中当前时间之前的所有未读消息标记为已读
    private void setUnReadBeforeTime(final String targetId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = LVIMSDK.sharedInstance().updatePrivateMsgAsReadByStime(targetId, System.currentTimeMillis());
                LogUtils.d(TAG, "result = " + result);
            }
        }).start();
    }

    // 把某个会话中所有未读消息标记为已读
    private void setConversationUnread(final String targetId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = LVIMSDK.sharedInstance().clearPrivateSessionUnreadMsg(targetId);
                LogUtils.d(TAG, "result = " + result);
            }
        }).start();
    }

    // 把所有私信未读消息标记为已读
    private void setAllUnread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = LVIMSDK.sharedInstance().clearPrivateAllUnreadMsg();
                LogUtils.d(TAG, "result = " + result);
            }
        }).start();
    }


}
