package com.siyanhui.mojif.demo;

/**
 * Created by fantasy on 16/6/23.
 */
public class Emoji {

    private int code;
    private int res;

    public Emoji(int code, int res) {
        this.code = code;
        this.res = res;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
