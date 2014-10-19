package org.babybrain.searchapps;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

public class SearchTextView extends EditText {
    private Context context;
    public ImageView x;
    public boolean hadFocus=false;
    private InputMethodManager imm;

    public SearchTextView(Context c) {
        super(c);
        init(c);
    }

    public SearchTextView(Context c, AttributeSet attrs) {
        super(c, attrs);
        init(c);
    }

    public SearchTextView(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
        init(c);
    }

    public void init(Context c) {
        context = c;
        imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
//        x = (ImageView) findViewById(R.id.close);
//        x.setOnTouchListener(closeTouchListener);
    }

//    @Override
//    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
//        super.onFocusChanged(focused, direction, previouslyFocusedRect);
//        if (!focused) {
//            hideKeyboard();
//        }
//    }

//    @Override
//    protected void onTextChanged(java.lang.CharSequence text, int start, int lengthBefore, int lengthAfter) {
//
//    }

    public void hideKeyboard(){
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public void clear(){
        setText("");
    }

    public void close(){
        clear();
        clearFocus();
        hideKeyboard();
    }

    public View.OnClickListener closeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            if(getText().length() > 0){
                clear();
            } else {
//                clearFocus();
                ((View)context).requestFocus();
//                hideKeyboard();
            }
        }
    };

    public View.OnTouchListener closeTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event){
            if(getText().length() > 0){
                clear();
                return false;
            } else {
                clearFocus();
//                hideKeyboard();
                return true;
            }
        }
    };
}
