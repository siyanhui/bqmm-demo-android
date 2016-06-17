package com.example.bqmm_demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ListView;

import com.melink.baseframe.utils.DensityUtils;
import com.melink.bqmmsdk.bean.Emoji;
import com.melink.bqmmsdk.sdk.BQMM;
import com.melink.bqmmsdk.sdk.IBqmmSendMessageListener;
import com.melink.bqmmsdk.ui.keyboard.BQMMKeyboard;
import com.melink.bqmmsdk.widget.BQMMEditView;
import com.melink.bqmmsdk.widget.BQMMSendButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyActivity extends FragmentActivity {
    public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0x1;
    private BQMMKeyboard bqmmKeyboard;
    private ListView mRealListView;
    List<Message> datas = new ArrayList<>();
    private ChatAdapter adapter;
    private View inputbox;
    /*
     * BQMM集成
     * 相关变量
     */
    private BQMMSendButton bqmmSend;
    private CheckBox bqmmKeyboardOpen;
    private BQMMEditView bqmmEditView;
    private BQMM bqmmsdk;

    /**
     * BQMM集成
     * 键盘切换相关
     */
    private Rect tmp = new Rect();
    private int mScreenHeight;
    private View mMainContainer;
    private final int DISTANCE_SLOP = 180;
    private final String LAST_KEYBOARD_HEIGHT = "last_keyboard_height";
    private boolean mPendingShowPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bqmm_myactivity_chat);
        initView();
    }

    private void initView() {
        inputbox = findViewById(R.id.messageToolBox);
        mMainContainer = findViewById(R.id.main_container);
        bqmmKeyboard = (BQMMKeyboard) findViewById(R.id.chat_msg_input_box);
        mRealListView = (ListView) findViewById(R.id.chat_listview);
        mRealListView.setSelector(android.R.color.transparent);
        bqmmSend = (BQMMSendButton) findViewById(R.id.chatbox_send);
        bqmmKeyboardOpen = (CheckBox) findViewById(R.id.chatbox_open);
        bqmmEditView = (BQMMEditView) findViewById(R.id.chatbox_message);
        bqmmEditView.requestFocus();
        /**
         * BQMM集成
         * 加载SDK
		 */
        bqmmsdk = BQMM.getInstance();
        // 初始化表情MM键盘，需要传入关联的EditView,SendBtn
        bqmmsdk.setEditView(bqmmEditView);
        bqmmsdk.setKeyboard(bqmmKeyboard);
        bqmmsdk.setSendButton(bqmmSend);
        bqmmsdk.load();
        /*
         * 默认方式打开软键盘时切换表情符号的状态
		 */
        bqmmEditView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bqmmKeyboardOpen.setChecked(false);
                return false;
            }
        });
        /**
         * BQMM集成
         * 实现输入联想
         */
        bqmmEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BQMM.getInstance().startShortcutPopupWindow(MyActivity.this, s.toString(), bqmmKeyboardOpen);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        /**
         * BQMM集成
         * 设置发送消息的回调
		 */
        bqmmsdk.setBqmmSendMsgListener(new IBqmmSendMessageListener() {
            /**
             * 单个大表情消息
             */
            @Override
            public void onSendFace(final Emoji face) {
                Message message = new Message(Message.MSG_TYPE_FACE,
                        Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry",
                        "avatar", face.getEmoCode(), true, true, new Date());
                datas.add(message);
                adapter.refresh(datas);

                /**
                 * 1秒后增加一条和发出的这条相同的消息，模拟对话
                 */
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Message getmessage = new Message(Message.MSG_TYPE_FACE,
                                Message.MSG_STATE_SUCCESS, "Jerry", "avatar", "Tom",
                                "avatar", face.getEmoCode(), false, true, new Date());
                        datas.add(getmessage);
                        adapter.refresh(datas);
                    }
                }, 1000);
            }

            /**
             * 图文混排消息
             */
            @Override
            public void onSendMixedMessage(List<Object> emojis, boolean isMixedMessage) {
                final String msgString = getMixedMessageString(emojis);
                Message message = new Message(Message.MSG_TYPE_TEXT,
                        Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry",
                        "avatar", msgString, true, true, new Date());
                datas.add(message);
                adapter.refresh(datas);

                /**
                 * 1秒后增加一条和发出的这条相同的消息，模拟对话
                 */
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Message getmessage = new Message(Message.MSG_TYPE_TEXT,
                                Message.MSG_STATE_SUCCESS, "Jerry", "avatar", "Tom",
                                "avatar", msgString, false, true, new Date());
                        datas.add(getmessage);
                        adapter.refresh(datas);
                    }
                }, 1000);
            }
        });
        initMessageInputToolBox();
        initListView();

        /**
         * 设置键盘的默认高度
         */
        int defaultHeight = DensityUtils.dip2px(this, 250);
        int height = getPreferences(MODE_PRIVATE).getInt(LAST_KEYBOARD_HEIGHT, defaultHeight);
        ViewGroup.LayoutParams params = bqmmKeyboard.getLayoutParams();
        if (params != null) {
            params.height = height;
            bqmmKeyboard.setLayoutParams(params);
        }
        /**
         * 表情键盘切换监听
         */
        bqmmEditView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Keyboard -> BQMM
                if (mPendingShowPlaceHolder) {
                    // 在设置mPendingShowPlaceHolder时已经调用了隐藏Keyboard的方法，直到Keyboard隐藏前都取消重绘
                    if (isKeyboardVisible()) {
                        ViewGroup.LayoutParams params = bqmmKeyboard.getLayoutParams();
                        int distance = getDistanceFromInputToBottom();
                        // 调整PlaceHolder高度
                        if (distance > DISTANCE_SLOP && distance != params.height) {
                            params.height = distance;
                            bqmmKeyboard.setLayoutParams(params);
                            getPreferences(MODE_PRIVATE).edit().putInt(LAST_KEYBOARD_HEIGHT, distance).apply();
                        }
                        return false;
                    } else {
                        mRealListView.setSelection(mRealListView.getAdapter().getCount() - 1);
                        showBqmmKeyboard();
                        mPendingShowPlaceHolder = false;
                        return false;
                    }
                } else {//BQMM -> Keyboard
                    if (isBqmmKeyboardVisible() && isKeyboardVisible()) {
                        mRealListView.setSelection(mRealListView.getAdapter().getCount() - 1);
                        hideBqmmKeyboard();
                        return false;
                    }
                }
                return true;
            }
        });
        //切换开关
        bqmmKeyboardOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 除非软键盘和PlaceHolder全隐藏时直接显示PlaceHolder，其他情况此处处理软键盘，onPreDrawListener处理PlaceHolder
                if (isBqmmKeyboardVisible()) { // PlaceHolder -> Keyboard
                    showSoftInput(bqmmEditView);
                } else if (isKeyboardVisible()) { // Keyboard -> PlaceHolder
                    mPendingShowPlaceHolder = true;
                    hideSoftInput(bqmmEditView);
                } else { // Just show PlaceHolder
                    mRealListView.setSelection(mRealListView.getAdapter().getCount() - 1);
                    showBqmmKeyboard();
                }
            }
        });
    }

    private String getMixedMessageString(List<Object> emojis) {
        // 获得bqmm_edit中的数据，做页面上的处理
        StringBuilder sendMsg = new StringBuilder();
        for (int i = 0; i < emojis.size(); i++) {
            if (emojis.get(i) instanceof Emoji) {
                sendMsg.append("[").append(((Emoji) emojis.get(i)).getEmoCode()).append("]");
            } else {
                sendMsg.append(emojis.get(i).toString());
            }
        }
        return sendMsg.toString();
    }

    /**************************
     * 表情键盘软键盘切换相关 start
     **************************************/
    private void closebroad() {
        if (isBqmmKeyboardVisible()) {
            hideBqmmKeyboard();
        } else if (isKeyboardVisible()) {
            hideSoftInput(bqmmEditView);
        }
    }

    private boolean isKeyboardVisible() {
        return (getDistanceFromInputToBottom() > DISTANCE_SLOP && !isBqmmKeyboardVisible())
                || (getDistanceFromInputToBottom() > (bqmmKeyboard.getHeight() + DISTANCE_SLOP) && isBqmmKeyboardVisible());
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    private void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Activity在此方法中测量根布局的高度
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && mScreenHeight <= 0) {
            mMainContainer.getGlobalVisibleRect(tmp);
            mScreenHeight = tmp.bottom;
        }
    }

    private void showBqmmKeyboard() {
        bqmmKeyboard.showKeyboard();
    }

    private void hideBqmmKeyboard() {
        bqmmKeyboard.hideKeyboard();
    }

    private boolean isBqmmKeyboardVisible() {
        return bqmmKeyboard.isKeyboardVisible();
    }

    /**
     * 输入框的下边距离屏幕的距离
     */
    private int getDistanceFromInputToBottom() {
        return mScreenHeight - getInputBottom();
    }

    /**
     * 输入框下边的位置
     */
    private int getInputBottom() {
        inputbox.getGlobalVisibleRect(tmp);
        return tmp.bottom;
    }

    /**************************
     * 表情键盘软键盘切换相关 end
     **************************************/

    @Override
    protected void onDestroy() {
        // 关闭SDK
        bqmmsdk.destory();
        super.onDestroy();
    }

    /*
     * 初始化列表信息以及表情键盘的监听
     */
    private void initMessageInputToolBox() {
        mRealListView.setOnTouchListener(getOnTouchListener());
    }

    private void initListView() {
        byte[] emoji = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98,
                (byte) 0x81};
        Message message = new Message(Message.MSG_TYPE_TEXT,
                Message.MSG_STATE_SUCCESS, "\ue415", "avatar", "Jerry",
                "avatar", new String(emoji), false, true, new Date(
                System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

        datas.add(message);
        adapter = new ChatAdapter(this, datas);
        mRealListView.setAdapter(adapter);
    }

    /**
     * 软键盘或者表情键盘打开时，按返回则关闭
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && (isBqmmKeyboardVisible() || isKeyboardVisible())) {
            closebroad();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 若软键盘或表情键盘弹起，点击上端空白处应该隐藏输入法键盘
     */
    private View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 关闭键盘
                bqmmKeyboardOpen.setChecked(false);
                closebroad();
                return false;
            }
        };
    }
}
