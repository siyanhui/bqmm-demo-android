package com.siyanhui.mojif.demo.bqmmgif;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.melink.baseframe.utils.DensityUtils;

import java.lang.ref.WeakReference;
import java.util.Collection;

/**
 * Created by syh on 07/12/2017.
 */

public class BQMMSearchPopupWindow extends PopupWindow {
    private RecyclerView mRecyclerView;
    private BQMMSearchContentAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int mTotalItemCount;
    private int mLastVisibleItemPosition;
    private LoadMoreListener mLoadMoreListener;
    private int[] mParentLocation = new int[]{0, 0};
    private WeakReference<View> mParentViewWeakReference;

    BQMMSearchPopupWindow(Context context, int height) {
        super();
        mRecyclerView = new RecyclerView(context);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new BQMMSearchContentAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = mLinearLayoutManager.getItemCount();
                mLastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                if (mTotalItemCount <= (mLastVisibleItemPosition + 2)) {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.loadMore();
                    }
                }
            }
        });
        LinearLayout layout = new LinearLayout(context);
        layout.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        layout.addView(mRecyclerView);
        setContentView(layout);
        setHeight(height);
        setFocusable(false);
    }

    public void setParentView(View parent) {
        parent.getLocationOnScreen(mParentLocation);
        mParentViewWeakReference = new WeakReference<>(parent);
    }

    public void show(final Collection stickers) {
        mRecyclerView.scrollToPosition(0);
        if (mParentViewWeakReference != null) {
            final View parent = mParentViewWeakReference.get();
            if (parent != null) {
                if (isShowing()) dismiss();
                mAdapter.setBQMMGifList(stickers);
                int screenWidth = DensityUtils.getScreenW();
                setWidth(screenWidth);
                showAtLocation(parent, Gravity.NO_GRAVITY, 0, mParentLocation[1] - getHeight());
            }
        }
    }

    public void showMore(final Collection stickers) {
        mAdapter.addBQMMGifList(stickers);
    }

    public void setLoadMoreListener(LoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    public BQMMSearchContentAdapter getAdapter() {
        return mAdapter;
    }

    interface LoadMoreListener {
        void loadMore();
    }
}
