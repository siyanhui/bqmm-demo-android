package com.siyanhui.mojif.demo.bqmmgif;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.melink.baseframe.utils.DensityUtils;
import com.melink.bqmmsdk.bean.BQMMGif;
import com.melink.bqmmsdk.widget.BQMMGifView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by syh on 07/12/2017.
 */

public class BQMMSearchContentAdapter extends RecyclerView.Adapter<BQMMSearchContentAdapter.ViewHolder> {
    private final List<BQMMGif> mBQMMGifList = new ArrayList<>();
    private OnSearchContentClickListener mSearchContentClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = new LinearLayout(parent.getContext());
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setBackgroundColor(Color.WHITE);
        layout.setPadding(DensityUtils.dip2px(5), DensityUtils.dip2px(7.5f), DensityUtils.dip2px(5), DensityUtils.dip2px(7.5f));
        BQMMGifView bqmmMessageText = new BQMMGifView(parent.getContext());
        layout.addView(bqmmMessageText);
        return new ViewHolder(layout, bqmmMessageText);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BQMMGif item = mBQMMGifList.get(position);
        int pixels = DensityUtils.dip2px(80);
        if (!TextUtils.isEmpty(item.getGif_thumb())) {
            holder.bqmmMessageTextView.showThumbnail(item.getSticker_id(), item.getGif_thumb(), pixels, pixels, item.getIs_gif() == 1);
        } else if (!TextUtils.isEmpty(item.getThumb())) {
            holder.bqmmMessageTextView.showThumbnail(item.getSticker_id(), item.getThumb(), pixels, pixels, false);
        } else {
            holder.bqmmMessageTextView.showGif(item.getSticker_id(), item.getSticker_url(), pixels, pixels, item.getIs_gif() == 1);
        }
        holder.bqmmMessageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchContentClickListener != null) {
                    mSearchContentClickListener.onSearchContentClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBQMMGifList.size();
    }

    public void setSearchContentClickListener(OnSearchContentClickListener listener) {
        this.mSearchContentClickListener = listener;
    }

    public void setBQMMGifList(@Nullable Collection bqmmGifList) {
        mBQMMGifList.clear();
        addBQMMGifList(bqmmGifList);
    }

    public void addBQMMGifList(@Nullable Collection bqmmGifList) {
        if (bqmmGifList != null) for (Object object : bqmmGifList) {
            if (object instanceof BQMMGif) {
                mBQMMGifList.add((BQMMGif) object);
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        BQMMGifView bqmmMessageTextView;

        ViewHolder(LinearLayout view, BQMMGifView text) {
            super(view);
            linearLayout = view;
            bqmmMessageTextView = text;
        }
    }

    interface OnSearchContentClickListener {
        void onSearchContentClick(BQMMGif webSticker);
    }
}
