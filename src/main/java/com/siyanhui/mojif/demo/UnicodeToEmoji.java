package com.siyanhui.mojif.demo;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fantasy on 16/6/23.
 */
public class UnicodeToEmoji {


    private static Resources sResources;
    private static float density;

    private static Map<Integer, Emoji> sEmojiMap;
    private static List<Emoji> sEmojiList;


    /**
     * 图片替换emoji初始化
     *
     * @param context
     */
    public static void initPhotos(Context context) {
        sEmojiMap = new HashMap<>();
        sEmojiList = new ArrayList<>();
        sResources = context.getResources();

        int[] codes = sResources.getIntArray(R.array.emoji_code);
        TypedArray array = sResources.obtainTypedArray(R.array.emoji_res);

        if (codes.length != array.length()) {
            throw new RuntimeException("Emoji resource init fail.");
        }

        int i = -1;
        while (++i < codes.length) {
            Emoji emoji = new Emoji(codes[i], array.getResourceId(i, -1));

            sEmojiMap.put(codes[i], emoji);
            sEmojiList.add(emoji);
        }

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        density = dm.density;

        Log.d("SystemUtils", "density:" + density);

    }

    public static class EmojiImageSpan extends ReplacementSpan {
        Drawable mDrawable;

        /**
         * BQMM集成
         * 返回codePoint对应的表情图片
         *
         * @param codePoint Unicode code point
         * @return 该code point对应的drawable
         */
        public static Drawable getEmojiDrawable(int codePoint) {
            if (sEmojiMap.containsKey(codePoint)) {
                return sResources.getDrawable(sEmojiMap.get(codePoint).getRes());
            } else {
                return null;
            }
        }

        private EmojiImageSpan(Resources resources, int codePoint) {

            if (sEmojiMap.containsKey(codePoint)) {
                mDrawable = resources.getDrawable(sEmojiMap.get(codePoint).getRes());

                int width = mDrawable.getIntrinsicWidth() - (int) (4 * density);
                int height = mDrawable.getIntrinsicHeight() - (int) (4 * density);
                mDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);

            }
        }

        private static final String TAG = "DynamicDrawableSpan";

        /**
         * A constant indicating that the bottom of this span should be aligned
         * with the bottom of the surrounding text, i.e., at the same level as the
         * lowest descender in the text.
         */
        public static final int ALIGN_BOTTOM = 0;


        /**
         * Your subclass must implement this method to provide the bitmap
         * to be drawn.  The dimensions of the bitmap must be the same
         * from each call to the next.
         */
        public Drawable getDrawable() {
            return mDrawable;
        }

        @Override
        public int getSize(Paint paint, CharSequence text,
                           int start, int end,
                           Paint.FontMetricsInt fm) {
            Drawable d = getCachedDrawable();
            Rect rect = d.getBounds();

            if (fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }

            return rect.right;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text,
                         int start, int end, float x,
                         int top, int y, int bottom, Paint paint) {
            Drawable b = getCachedDrawable();
            canvas.save();

            int transY = bottom - b.getBounds().bottom;

            transY -= density;


            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }

        private Drawable getCachedDrawable() {
            WeakReference<Drawable> wr = mDrawableRef;
            Drawable d = null;

            if (wr != null)
                d = wr.get();

            if (d == null) {
                d = getDrawable();
                mDrawableRef = new WeakReference<Drawable>(d);
            }

            return d;
        }

        private WeakReference<Drawable> mDrawableRef;
    }


}
