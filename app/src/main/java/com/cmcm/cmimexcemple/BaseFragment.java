package com.cmcm.cmimexcemple;

import android.os.Handler;

import androidx.fragment.app.Fragment;

/**
 * Created by Xiaohong on 2020/10/25.
 * desc:
 */
public class BaseFragment extends Fragment {
    private Handler mHandler = new Handler();

    /**
     * 将任务放到主线程执行
     * @param runnable 待执行任务
     */
    public void runOnUiThread(Runnable runnable) {
        runOnUiThread(runnable, 0);
    }

    /**
     * 将任务延迟delay毫秒放到主线程执行
     * @param runnable 待执行任务
     * @param delay 延迟执行的毫秒数
     */
    public void runOnUiThread(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);

    }


}
