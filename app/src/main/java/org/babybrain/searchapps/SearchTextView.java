package org.babybrain.searchapps;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

public class SearchTextView extends EditText {
    private Context c;
    public View main;
    public View v;
    public Apps apps;
    public String query = "";
    public String lastQuery = "";
    public ImageView x;
    public Window w;
    public Activity activity;
    public GridView appsView;
    public boolean hadFocus=false;
    InputMethodManager imm;

    public SearchTextView(Context context) {
        super(context);
        init(context);
    }

    public SearchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        v = this;
        c = context;
        imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

//    @Override
//    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
//        super.onFocusChanged(focused, direction, previouslyFocusedRect);
//        if (!focused) {
//            hideKeyboard();
//        }
//    }

    public void hideKeyboard(){
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public void clear(){
        setText("");
    }

    public void close(){
        if(getText().length() > 0){
            clear();
        } else {
            clearFocus();
            hideKeyboard();
        }
    }

    // if there's a query, launch the first app, else close the field
    public void onDoneClick() {
        if(getText().length() > 0){
            apps.launchLast();
        } else {
            close();
        }
    }

    public View.OnClickListener closeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            close();
        }
    };

    public View.OnTouchListener closeTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event){
            close();
            return true;
        }
    };

/*
    public Runnable adjustPan = new Runnable(){
        public void run(){
            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    };

    public Runnable adjustResize = new Runnable(){
        public void run(){
            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    };

    public void setTimeout(Runnable r, long delayMillis){
        new android.os.Handler().postDelayed(r, delayMillis);
    }

    private Runnable mShowImeRunnable = new Runnable() {
        public void run() {
            InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null){
//                requestFocus();
                imm.showSoftInput(main,0);
//                if(keyboardHeight > 0){
//                    keyboardSpacer.setVisibility(VISIBLE);
//                    statusbarSpacer.setVisibility(VISIBLE);
//                    searchbarSpacer.setVisibility(GONE);
//                }
            }
        }
    };

    private Runnable mHideImeRunnable = new Runnable() {
        public void run() {
            InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null){
//                clearFocus();
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
//                keyboardSpacer.setVisibility(GONE);
//                statusbarSpacer.setVisibility(GONE);
//                searchbarSpacer.setVisibility(VISIBLE);
            }
        }
    };

    private void setImeVisibility(final boolean visible) {
        if (visible) {
            removeCallbacks(mHideImeRunnable);
            post(mShowImeRunnable);
            //setTimeout(adjustResize, 400);
        } else {
            removeCallbacks(mShowImeRunnable);
            //post(adjustPan);
            //setTimeout(mHideImeRunnable, 400);
            post(mHideImeRunnable);
//            if(keyboardHeight == 0) setSpacersSlow();
        }
    }
*/
    private Runnable resetView = new Runnable() {
        public void run() {
            post(apps.resetView);
        }
    };

    public void log(String t){
//        Log.d("webb.log", t);
    }
}
