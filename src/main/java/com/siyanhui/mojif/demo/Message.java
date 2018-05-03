package com.siyanhui.mojif.demo;

import com.melink.bqmmsdk.bean.Emoji;
import com.melink.bqmmsdk.bean.BQMMGif;

import org.json.JSONArray;

import java.util.Date;
import java.util.List;

/**
 * 聊天消息javabean
 */
public class Message {
    public final static int MSG_TYPE_FACE = 1;
    public final static int MSG_TYPE_MIXTURE = 2;
    public final static int MSG_TYPE_WEBSTICKER = 3;

    public final static int MSG_STATE_SENDING = 3;
    public final static int MSG_STATE_SUCCESS = 1;
    public final static int MSG_STATE_FAIL = 2;

    private Long id;
    private int type; // 1-大表情 | 2-图文混排 ...
    private int state; // 0-sending | 1-success | 2-fail
    private String fromUserName;
    private String fromUserAvatar;
    private String toUserName;
    private String toUserAvatar;

    private Boolean isSend;
    private Boolean sendSucces;
    private Date time;
    private Emoji emoji;
    private List<Object> emojis;//默认表情图文混排消息
    private BQMMGif mBQMMGif;

    private JSONArray contentArray;

    public Message(int type, int state, String fromUserName,
                   String fromUserAvatar, String toUserName, String toUserAvatar, JSONArray jsonArray, Boolean isSend, Boolean sendSucces, Date time) {
        super();
        this.type = type;
        this.state = state;
        this.fromUserName = fromUserName;
        this.fromUserAvatar = fromUserAvatar;
        this.toUserName = toUserName;
        this.toUserAvatar = toUserAvatar;
        this.isSend = isSend;
        this.sendSucces = sendSucces;
        this.time = time;
        this.contentArray = jsonArray;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserAvatar() {
        return fromUserAvatar;
    }

    public void setFromUserAvatar(String fromUserAvatar) {
        this.fromUserAvatar = fromUserAvatar;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getToUserAvatar() {
        return toUserAvatar;
    }

    public void setToUserAvatar(String toUserAvatar) {
        this.toUserAvatar = toUserAvatar;
    }


    public Boolean getIsSend() {
        return isSend;
    }

    public void setIsSend(Boolean isSend) {
        this.isSend = isSend;
    }

    public Boolean getSendSucces() {
        return sendSucces;
    }

    public void setSendSucces(Boolean sendSucces) {
        this.sendSucces = sendSucces;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public void setEmoji(Emoji emoji) {
        this.emoji = emoji;
    }

    public void setEmojis(List<Object> emojis) {
        this.emojis = emojis;
    }

    public List<Object> getEmojis() {
        return emojis;
    }

    public JSONArray getContentArray() {
        return contentArray;
    }

    public void setContentArray(JSONArray contentArray) {
        this.contentArray = contentArray;
    }
    public BQMMGif getBqssWebSticker() {
        return mBQMMGif;
    }

    public void setBqssWebSticker(BQMMGif bqmmGif) {
        this.mBQMMGif = bqmmGif;
    }

}
