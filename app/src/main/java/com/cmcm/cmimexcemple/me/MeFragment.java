package com.cmcm.cmimexcemple.me;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmcm.cmimexcemple.BaseFragment;
import com.cmcm.cmimexcemple.GlobalParams;
import com.cmcm.cmimexcemple.R;
import com.cmcm.cmimexcemple.event.LoginSucceedEvent;
import com.cmcm.cmimexcemple.event.MessageEvent;
import com.cmcm.cmimexcemple.event.RefreshConversationListEvent;
import com.im.imlogic.LVIMSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Xiaohong on 2020/10/27.
 * desc: 登录用户信息页
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {
    private TextView mTvUserId;
    private TextView mTvHead;
    private Button mBtnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mTvUserId = view.findViewById(R.id.tv_user_id);
        mTvHead = view.findViewById(R.id.tv_head);

        mBtnLogout = view.findViewById(R.id.btn_logout);
        mBtnLogout.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
    }

    private void updateUserInfo() {
        String loginUserId = GlobalParams.loginUserId;
        char userFirstLetter = '\n';
        if (loginUserId != null && loginUserId.length() > 0) {
            userFirstLetter = loginUserId.charAt(0);
        }
        mTvHead.setText(userFirstLetter+"");
        mTvUserId.setText(loginUserId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                LVIMSDK.sharedInstance().logout();
                GlobalParams.loginUserId = "";
                updateUserInfo();
                break;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event instanceof RefreshConversationListEvent || event instanceof LoginSucceedEvent) {
            // 登录成功，刷新用户信息。
            updateUserInfo();
        }
    }



}
