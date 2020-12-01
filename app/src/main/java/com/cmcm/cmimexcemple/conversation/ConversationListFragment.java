package com.cmcm.cmimexcemple.conversation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmcm.cmimexcemple.BaseFragment;
import com.cmcm.cmimexcemple.R;
import com.cmcm.cmimexcemple.event.LoginSucceedEvent;
import com.cmcm.cmimexcemple.event.MessageEvent;
import com.cmcm.cmimexcemple.event.RefreshConversationListEvent;
import com.im.imlogic.LVIMSDK;
import com.im.imlogic.LVIMSession;
import com.im.imlogic.utils.LvImLogs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by Xiaohong on 2020/10/25.
 * desc:
 */
public class ConversationListFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private ConversationAdapter conversationAdapter;
    private String TAG = "ConversationListFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.rv_conversations);
        Context context = getContext();
        LvImLogs.d(TAG, "initView ");
        if (context != null) {
            conversationAdapter = new ConversationAdapter(context);
            LvImLogs.d(TAG, "initView ConversationAdapter");
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            mRecyclerView.setAdapter(conversationAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateConversations();
    }


    // 刷新会话列表
    public void updateConversations() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<LVIMSession> list = new ArrayList<>();
                LVIMSDK.sharedInstance().querySessionList(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LvImLogs.d(TAG, "initView addConversations");
                        if (conversationAdapter != null) {
                            conversationAdapter.addConversations(list);
                        }
                    }
                });
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event instanceof RefreshConversationListEvent || event instanceof LoginSucceedEvent) {
            // 登录成功，刷新会话列表。
            updateConversations();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
