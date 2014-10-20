package org.babybrain.searchapps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

public class SearchTextView extends EditText {
    private InputMethodManager imm;
    public ImageView x;
    public Activity mainActivity;
    public boolean keyboardWasVisible = true;

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
        imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect)
    {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(gainFocus) {
            showKeyboard();
        }
        else {
            hideKeyboard();
        }
    }

    public void onResume(){
        if(keyboardWasVisible) {
            requestFocus();
            mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        else{
            mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    public void hideKeyboard(){
//        Log.d("webb", "hideKeyboard");
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        imm.hideSoftInputFromWindow(getWindowToken(), 0); // slower
        keyboardWasVisible = false;
    }

    public void showKeyboard(){
//        Log.d("webb", "showKeyboard");
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
//        imm.showSoftInput(main, InputMethodManager.SHOW_FORCED); // slower
        keyboardWasVisible = true;
    }

    public void clear(){
        setText("");
    }

    public void close(){
        clear();
        clearFocus();
    }

    public View.OnClickListener xClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            if (getText().length() > 0) {
                clear();
            } else {
                clearFocus();
            }
        }
    };

}
