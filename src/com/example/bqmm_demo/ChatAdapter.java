package com.example.bqmm_demo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.melink.baseframe.utils.StringUtils;
import com.melink.bqmmsdk.bean.Emoji;
import com.melink.bqmmsdk.sdk.BQMM;
import com.melink.bqmmsdk.sdk.BQMMMessageHelper;
import com.melink.bqmmsdk.sdk.IFetchEmojisByCodeListCallback;
import com.melink.bqmmsdk.widget.BQMMMessageView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            holder.message = (BQMMMessageView) v.findViewById(R.id.chat_item_content);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tv_date.setText(StringUtils.friendlyTime(StringUtils
                .getDataTime("yyyy-MM-dd " + "HH:mm:ss")));
        holder.tv_date.setVisibility(View.VISIBLE);
        // 如果是文本类型，则隐藏图片，如果是图片则隐藏文本
        if (data.getType() == Message.MSG_TYPE_TEXT) {
            showTextInfoFromStr(holder.message, data.getContent());
        } else {
            holder.message.loadDefaultFaceView();
            List<String> codes = new ArrayList<>();
            codes.add(data.getContent());
            BQMM.getInstance().fetchBigEmojiByCodeList(context, codes, new IFetchEmojisByCodeListCallback() {
                @Override
                public void onSuccess(List<Emoji> emojis) {
                    holder.message.showFaceMessage(emojis);
                }

                @Override
                public void onError(Throwable arg0) {
                    Log.d(context.getClass().getName(), "fetchEmojiByCode fail");
                }
            });


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

    private List<String> findEmojiByMsgStr(String messageStr) {
        List<String> emoji_list = new ArrayList<>();
        Pattern pattern1 = Pattern.compile("\\[([^\\[\\]]+)\\]", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(messageStr);
        while (matcher1.find()) {
            emoji_list.add(matcher1.group(1));
        }
        return emoji_list;
    }

    private void showTextInfoFromStr(final BQMMMessageView messageView, final String messagecontent) {
        if (!(findEmojiByMsgStr(messagecontent).size() > 0)) {
            messageView.getmTextView().setText(messagecontent);
            return;
        }
        BQMM.getInstance().fetchSmallEmojiByCodeList(context, findEmojiByMsgStr(messagecontent), new IFetchEmojisByCodeListCallback() {
            @Override
            public void onSuccess(final List<Emoji> emojis) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageView.showMixedMessage(BQMMMessageHelper.parseMixedMsg(messagecontent, emojis));
                    }
                });
            }

            @Override
            public void onError(Throwable arg0) {
                arg0.printStackTrace();
            }
        });
    }

    class ViewHolder {
        TextView tv_date;
        ImageView img_sendfail;
        ProgressBar progress;
        BQMMMessageView message;
    }
}
