package com.cmcm.cmimexcemple.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.cmimexcemple.R;

/**
 * 主页的按钮
 */
public class MainBottomTabItem extends RelativeLayout {
    private ImageView tabImage;
    private TextView tabText;
    private ImageView redIv;
    private TextView tvNum;

    public MainBottomTabItem(Context context) {
        super(context);
        initView();
    }

    public MainBottomTabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MainBottomTabItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.item_tab, this);
        tabImage = view.findViewById(R.id.iv_tab_img);
        tabText = view.findViewById(R.id.tv_tab_text);
        redIv = view.findViewById(R.id.iv_red);
        tvNum = view.findViewById(R.id.tv_num);
    }

    public void setName(String name) {
        tabText.setText(name);
    }

    public void setDrawable(int drawable) {
        tabImage.setBackgroundResource(drawable);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        tabImage.setSelected(selected);
        tabText.setSelected(selected);
    }

    /**
     * 红点
     * @param visibility
     */
    public void setRedVisibility(int visibility) {
        redIv.setVisibility(visibility);
    }

    /**
     * 数量
     * @param visibility
     */
    public void setNumVisibility(int visibility) {
        tvNum.setVisibility(visibility);
    }

    /**
     * 消息数
     * @param num
     */
    public void setNum(String num) {
        tvNum.setText(num);
    }

//    /**
//     * 设置未读书多拽监听
//     * @param listener
//     */
//    public void setTabUnReadNumDragListener(DragPointView.OnDragListencer listener) {
//        tvNum.setDragListencer(listener);
//    }

}
