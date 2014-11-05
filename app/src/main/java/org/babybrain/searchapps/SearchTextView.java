package org.babybrain.searchapps;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

public class SearchTextView extends EditText {
    private InputMethodManager imm;
    public ImageView x;
    public Activity mainActivity;
//    public boolean keyboardWasVisible = true;

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

    public void hideKeyboard(){
//        Log.d("webb", "hideKeyboard");
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void clear(){
        setText("");
    }

    public void close(){
        clear();
        clearFocus();
        hideKeyboard();
    }

    public View.OnClickListener xClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            if (getText().length() > 0) {
                clear();
            } else {
                clearFocus();
                hideKeyboard();
            }
        }
    };

}
