package com.cmcm.cmimexcemple;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.im.imlogic.LVIMSDK;

/**
 * Created by Xiaohong on 2020-05-22.
 * desc:
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    // 获取IM SDK的单例实例
    public LVIMSDK mLVIMSDK = LVIMSDK.sharedInstance();

}
