package com.cmcm.cmimexcemple.chatroom;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmcm.cmimexcemple.BaseFragment;
import com.cmcm.cmimexcemple.R;
import com.cmcm.cmimexcemple.conversation.ConversationAdapter;
import com.im.imlogic.utils.LvImLogs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2020/10/25.
 * desc:
 */
public class ChatRoomListFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private final static String TAG = "ChatRoomListFragment";
    private RoomListAdapter mRoomListAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.rv_chat_room_list);
        Context context = getContext();
        LvImLogs.d(TAG, "initView ");
        if (context != null) {
            mRoomListAdapter = new RoomListAdapter(context);
            LvImLogs.d(TAG, "initView RoomListAdapter");
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            mRecyclerView.setAdapter(mRoomListAdapter);
        }
    }

}
