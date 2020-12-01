package com.cmcm.cmimexcemple.main;

/**
 * Created by Xiaohong on 2020/10/25.
 * desc: 主页三种TAB的枚举类。
 */
public enum Tab {
    // 会话
    CHAT(0),
    // 房间消息
    ROOM(1),
    // 个人页
    ME(2);

    private int value;

    Tab(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    // 根据值获取枚举类型。
    public static Tab getType(int value){
        Tab[] values = Tab.values();
        for (Tab tab: values) {
            if (value == tab.value){
                return tab;
            }
        }
        return null;
    }

}
