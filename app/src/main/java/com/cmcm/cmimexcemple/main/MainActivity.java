package com.cmcm.cmimexcemple.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cmcm.cmimexcemple.BaseActivity;
import com.cmcm.cmimexcemple.ChatRoomActivity;
import com.cmcm.cmimexcemple.GlobalParams;
import com.cmcm.cmimexcemple.PrivateChatActivity;
import com.cmcm.cmimexcemple.R;
import com.cmcm.cmimexcemple.chatroom.ChatRoomListFragment;
import com.cmcm.cmimexcemple.conversation.ConversationListFragment;
import com.cmcm.cmimexcemple.event.LoginSucceedEvent;
import com.cmcm.cmimexcemple.event.RefreshConversationListEvent;
import com.cmcm.cmimexcemple.me.MeFragment;
import com.cmcm.cmimexcemple.utils.LogUtils;
import com.cmcm.cmimexcemple.utils.SystemUtil;
import com.cmcm.cmimexcemple.main.widget.MainTabGroupView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.im.imcore.IMBridger;
import com.im.imlogic.LVIMSDK;
import com.im.imlogic.utils.LvImLogs;

import java.util.ArrayList;
import java.util.List;

import com.cmcm.cmimexcemple.main.widget.*;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends BaseActivity implements View.OnClickListener, MainPopWindow.OnPopWindowItemClickListener {
    private final String TAG = "MainActivity";
    private EditText mEtUserId;
    private EditText mEtOtherId;
    private ViewPager mVpFragmentContainer;
    private MainTabGroupView mTabGroupView;
    /**
     * tabs 的图片资源
     */
    private int[] tabImageRes = new int[]{
            R.drawable.selector_chat,
            R.drawable.selector_tab_room,
            R.drawable.selector_tab_me,
//            R.drawable.selector_room,
//            R.drawable.selector_me,
    };

    /**
     * 各个 Fragment 界面
     */
    private List<Fragment> fragments = new ArrayList<>();
    private ImageButton mBtnMore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                LvImLogs.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

    }

    // 初始化界面
    private void initView() {

        mTabGroupView = findViewById(R.id.tabGroupView);
        mBtnMore = findViewById(R.id.btn_more);
        mBtnMore.setOnClickListener(this);
        mVpFragmentContainer = findViewById(R.id.vpFragmentContainer);
        // 初始化底部 tabs
        initTabs();
        // 初始化 fragment 的 viewpager
        initFragmentViewPager();

        // 选中第一个界面
        mTabGroupView.setSelected(0);
    }

    /**
     * 初始化 Tabs
     */
    private void initTabs() {
        // 初始化 tab
        List<TabItem> items = new ArrayList<>();
        String[] stringArray = getResources().getStringArray(R.array.tab_names);

        for (Tab tab : Tab.values()) {
            TabItem tabItem = new TabItem();
            tabItem.id = tab.getValue();
            tabItem.text = stringArray[tab.getValue()];
            tabItem.drawable = tabImageRes[tab.getValue()];
            items.add(tabItem);
        }

        mTabGroupView.initView(items, new TabGroupView.OnTabSelectedListener() {
            @Override
            public void onSelected(View view, TabItem item) {
                // 检查是否登录，没有则弹窗登录
                if (!isLoginSucceed()) {
                    showLoginDialog();
                    return;
                }
                // 点击切换界面
                int currentItem = mVpFragmentContainer.getCurrentItem();
                if (currentItem != item.id) {
                    mVpFragmentContainer.setCurrentItem(item.id);
                    // 如果是我的页面， 则隐藏红点
                    if (item.id == Tab.ME.getValue()) {
                        ((MainBottomTabItem) mTabGroupView.getView(Tab.ME.getValue())).setRedVisibility(View.GONE);
                    }
                }
            }
        });


        ((MainBottomTabItem) mTabGroupView.getView(Tab.CHAT.getValue())).setNumVisibility(View.VISIBLE);
    }


    /**
     * 初始化 initFragmentViewPager
     */
    private void initFragmentViewPager() {
        fragments.add(new ConversationListFragment());
        fragments.add(new ChatRoomListFragment());
        fragments.add(new MeFragment());

        // ViewPager 的 Adpater
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };

        mVpFragmentContainer.setAdapter(fragmentPagerAdapter);
        mVpFragmentContainer.setOffscreenPageLimit(fragments.size());
        // 设置页面切换监听
        mVpFragmentContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 设置tab状态
                mTabGroupView.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 更新push token
     *
     * @param token push token
     */
    private void uploadToken(String token) {
        int result = mLVIMSDK.uploadPushToken(token, new IMBridger.IMUploadPushTokenListener() {
            @Override
            public void onIMUploadPushTokenCallback(int eCode, String msg) {
                LvImLogs.d(TAG, "eCode = " + eCode + "  msg = " + msg);
            }
        });
        if (result != 0) {
            LogUtils.e(this, " 更新push token失败！！");
        }
    }


    void retrieveAndUpdateToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful() || task.getResult() == null) {
                            LvImLogs.w(TAG, "getInstanceId failed" + task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        LvImLogs.d(TAG, "retrieveToken token = " + token + " tid = " + Thread.currentThread().getId());
                        if (!TextUtils.isEmpty(token)) {
                            uploadToken(token);
                        }
                    }
                });
    }


    /**
     * 登陆
     *
     * @param uid 用户id
     */
    private void login(String uid) {
        int result = mLVIMSDK.login(uid);
        if (result == 0) {
            onLoginSucceed(uid);
        } else {
            LogUtils.e(this, "登陆失败！！");
        }
    }


    // 登录成功后的界面更新
    private void onLoginSucceed(String uid) {
        Toast.makeText(MainActivity.this, getString(R.string.login_succeed), Toast.LENGTH_SHORT).show();
        GlobalParams.loginUserId = uid;
        EventBus.getDefault().post(new LoginSucceedEvent());
        retrieveAndUpdateToken();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // 显示登录弹窗
    public void showLoginDialog() {
        mEtUserId = new EditText(this);
        mEtUserId.setHint(getString(R.string.tip_input_user_id));
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.login))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(mEtUserId)
                .setPositiveButton(getString(R.string.login), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mEtUserId != null) {
                            String userId = mEtUserId.getText().toString().trim();
                            if (TextUtils.isEmpty(userId) || userId.length() < 4) {
                                // 提示用户ID不能小于4个字符
                                Toast.makeText(MainActivity.this, getString(R.string.tip_input_user_id), Toast.LENGTH_SHORT).show();
                                showLoginDialog();
                            } else {
                                login(userId);
                                SystemUtil.hideKeyboard(MainActivity.this, mEtUserId);
                            }
                        }
                    }
                })
                .setCancelable(false).show();
    }


    /**
     * 检测是否登录成功
     */
    private boolean isLoginSucceed() {
        return mLVIMSDK.isAppUserLoginSucceed();
    }

    // 退出登录
    public void logout(View view) {
        LVIMSDK.sharedInstance().logout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more:
                // 检查是否登录，没有则弹窗登录
                if (!isLoginSucceed()) {
                    showLoginDialog();
                } else {
                    MainPopWindow mainPopWindow = new MainPopWindow(this, this);
                    mainPopWindow.showPopupWindow(v);
                }
                break;
        }
    }

    @Override
    public void onStartChartClick() {
        // 发起单聊，先弹窗让用户输入对方用户ID
        startNewChatDialog();

    }


    // 发起单聊弹窗
    public void startNewChatDialog() {
        mEtOtherId = new EditText(this);
        mEtOtherId.setHint(getString(R.string.pls_input_user_id));
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.popup_item_start_chat))
                .setView(mEtOtherId)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mEtOtherId != null) {
                            String other = mEtOtherId.getText().toString().trim();
                            if (TextUtils.isEmpty(other)) {
                                // 提示用户ID不能为空
                                Toast.makeText(MainActivity.this, getString(R.string.pls_input_user_id), Toast.LENGTH_SHORT).show();
                                startNewChatDialog();
                            } else {
                                PrivateChatActivity.actionStart(MainActivity.this, other);
                                SystemUtil.hideKeyboard(MainActivity.this, mEtUserId);
                            }
                        }
                    }
                })
                .show();
    }

    @Override
    public void onCreateGroupClick() {
        // 创建群聊

    }

    @Override
    public void onCreateRoomClick() {
        // 创建房间。
        createRoomDialog();
    }

    // 创建聊天室弹窗
    public void createRoomDialog() {
        final EditText etRoomId = new EditText(this);
        etRoomId.setHint(getString(R.string.tip_input_room_id));
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.popup_item_create_room))
                .setView(etRoomId)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etRoomId != null) {
                            String roomId = etRoomId.getText().toString().trim();
                            if (TextUtils.isEmpty(roomId)) {
                                Toast.makeText(MainActivity.this, getString(R.string.tip_input_room_id), Toast.LENGTH_SHORT).show();
                                createRoomDialog();
                            } else {
                                ChatRoomActivity.actionStart(MainActivity.this, roomId);
                                SystemUtil.hideKeyboard(MainActivity.this, mEtUserId);
                            }
                        }
                    }
                })
                .show();
    }


}
