package com.siyanhui.mojif.demo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.melink.baseframe.utils.DensityUtils;
import com.melink.baseframe.utils.StringUtils;
import com.melink.bqmmsdk.sdk.BQMMMessageHelper;
import com.melink.bqmmsdk.widget.BQMMMessageText;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private List<Message> datas = null;

    public ChatAdapter(Context context, List<Message> datas) {
        this.context = context;
        if (datas == null) {
            datas = new ArrayList<>(0);
        }
        this.datas = datas;
    }

    public void refresh(List<Message> datas) {
        if (datas == null) {
            datas = new ArrayList<>(0);
        }
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getIsSend() ? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final ViewHolder holder;
        final Message data = datas.get(position);
        if (v == null) {
            holder = new ViewHolder();
            if (data.getIsSend()) {
                v = View.inflate(context, R.layout.bqmm_chat_item_list_right, null);
            } else {
                v = View.inflate(context, R.layout.bqmm_chat_item_list_left, null);
            }
            holder.img_sendfail = (ImageView) v
                    .findViewById(R.id.chat_item_fail);
            holder.progress = (ProgressBar) v
                    .findViewById(R.id.chat_item_progress);
            holder.tv_date = (TextView) v.findViewById(R.id.chat_item_date);
            holder.message = (BQMMMessageText) v.findViewById(R.id.chat_item_content);
            holder.message.setStickerSize(DensityUtils.dip2px(100));
            holder.message.setEmojiSize(DensityUtils.dip2px(20));
            holder.message.setUnicodeEmojiSpanSizeRatio(1.5f);//让emoji显示得比一般字符大一点
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tv_date.setText(StringUtils.friendlyTime(StringUtils
                .getDateTime("yyyy-MM-dd " + "HH:mm:ss")));
        holder.tv_date.setVisibility(View.VISIBLE);

        if (data.getType() == Message.MSG_TYPE_FACE) {//大表情
            holder.message.showMessage(BQMMMessageHelper.getMsgCodeString(data.getContentArray()), BQMMMessageText.FACETYPE, data.getContentArray());
            holder.message.getBackground().setAlpha(0);
        } else if (data.getType() == Message.MSG_TYPE_WEBSTICKER) {
            holder.message.showBQMMGif(data.getBqssWebSticker().getSticker_id(), data.getBqssWebSticker().getSticker_url(), data.getBqssWebSticker().getSticker_width(), data.getBqssWebSticker().getSticker_height(), data.getBqssWebSticker().getIs_gif());
            holder.message.getBackground().setAlpha(0);
        } else {//小表情或文字或图文混排
            holder.message.showMessage(BQMMMessageHelper.getMsgCodeString(data.getContentArray()), BQMMMessageText.EMOJITYPE, data.getContentArray());
            holder.message.getBackground().setAlpha(255);
        }
        // 消息发送的状态
        switch (data.getState()) {
            case Message.MSG_STATE_FAIL:
                holder.progress.setVisibility(View.GONE);
                holder.img_sendfail.setVisibility(View.VISIBLE);
                break;
            case Message.MSG_STATE_SUCCESS:
                holder.progress.setVisibility(View.GONE);
                holder.img_sendfail.setVisibility(View.GONE);
                break;
            case Message.MSG_STATE_SENDING:
                holder.progress.setVisibility(View.VISIBLE);
                holder.img_sendfail.setVisibility(View.GONE);
                break;
        }
        return v;
    }


    class ViewHolder {
        TextView tv_date;
        ImageView img_sendfail;
        ProgressBar progress;
        BQMMMessageText message;
    }
}
