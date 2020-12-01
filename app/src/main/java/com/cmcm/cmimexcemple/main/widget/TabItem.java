package com.cmcm.cmimexcemple.main.widget;

/**
 * 按钮的信息
 */
public class TabItem {

    public int id;
    public int drawable;
    public int tag;
    public String text;
    public int width;
    public int height;
    public int top;
    public int left;
    public int bottom;
    public int right;

    public Type type;

    public enum Type{
        BUTTON,
        CHECKBOX,
        RADIOBUTTON
    }
}
