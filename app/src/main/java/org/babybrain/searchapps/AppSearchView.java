package org.babybrain.searchapps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AppSearchView extends EditText {
//    private OnQueryTextListener mOnQueryChangeListener;
//    private boolean mIconified;
//    private View mSearchButton;
//    private View mSubmitButton;
//    private View mSearchPlate;
//    private View mSubmitArea;
//    private SearchAutoComplete mQueryTextView;

    private Context c;
    private LinearLayout mAdjustResizeSpacer;
    public View main;
    public View v;
//    , search, spacer;
    private Window window;
    public Apps apps;
    public String query = "";
    public String lastQuery = "";
    public LinearLayout pad;
    public ImageView x;
    private ViewTreeObserver viewTreeObserver;
    public Window w;
    public Activity activity;
    public int lastHeightDiff = 0;
    public int statusbarHeight = 0;
    public int keyboardHeight = 0;
    public GridViewNoTopFade appsView;
    public LinearLayout searchbarSpacer;
    public LinearLayout statusbarSpacer;
    public LinearLayout keyboardSpacer;
    public LinearLayout keyboardSpacerBottom;

//    private ImageView mCloseButton = (ImageView) findViewById(R.id.search_close_btn);

    public AppSearchView(Context context) {
        super(context);
        c = context;
        init();
//        LayoutInflater.from(context).inflate(R.layout.appSearchView, this, true);

    }

    public AppSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
        init();
//        LayoutInflater.from(context).inflate(R.layout.appSearchView, this, true);
    }

    public AppSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        c = context;
        init();
//        LayoutInflater.from(context).inflate(R.layout.appSearchView, this, true);
//        imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        window = (Window)this.getParent();
        // get a reference of the activity
//        Activity parent = (Activity)getContext();
        // using the activity, get Window reference
//        window = parent.getWindow();
        // using the reference of the window, do whatever you want :D
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        Resources r = mContext.getResources();
//        main = (View) r.getLayout(webb.test2.R.id.main);
//        search = (View) r.getLayout(webb.test2.R.id.search);
//        spacer = (View) r.getLayout(webb.test2.R.id.spacer);
//        Log.d("webb.appsearch", "main h" + main.getHeight() + " scrollY " + main.getScrollY() + " scaleY" + main.getScaleY());
//        Log.d("webb.appsearch", "main h" + search.getHeight() + " scrollY " + search.getScrollY() + " scaleY" + search.getScaleY());
//        Log.d("webb.appsearch", "main h" + spacer.getHeight() + " scrollY " + spacer.getScrollY() + " scaleY" + spacer.getScaleY());
    }

    public void init(){
        v = this;
//        close = (Drawable) findViewById(R.drawable.dialog_ic_close_normal_holo_dark);
//        close.setBounds(5, 5, 35, 35);
//        setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); // supposedly xml textNoSuggestions could fail
    }

//    private void updateViewsVisibility(final boolean collapsed) {
//        mIconified = collapsed;
//        // Visibility of views that are visible when collapsed
//        final int visCollapsed = collapsed ? VISIBLE : GONE;
//        // Is there text in the query
////        final boolean hasText = !TextUtils.isEmpty(mQueryTextView.getText());
//        final boolean hasText = false;
//
//        mSearchButton.setVisibility(visCollapsed);
//        updateSubmitButton(hasText);
//        mSearchEditFrame.setVisibility(collapsed ? GONE : VISIBLE);
//        mSearchHintIcon.setVisibility(mIconifiedByDefault ? GONE : VISIBLE);

//        updateCloseButton();
//        updateVoiceButton(!hasText);
//        updateSubmitArea();
//    }
/*
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
//        adjustPanResize();

        if (focused) {
//
//            setImeVisibility(true);
//            adjustPanResize();
//adjustPanResize();
//            Log.d("webb.appsearch", "hasFocus");
//            mHeight = main.getHeight();
//            Log.d("webb.appsearch", "height " + mHeight);
//            Log.d("webb.appsearch", "scrollY " + main.getScrollY());

//            mContext.getApplicationContext().
//            mAdjustResizeSpacer
//            super.onFocusChanged(focused, direction, previouslyFocusedRect);
//            new android.os.Handler().postDelayed(new Runnable() {
//                public void run() {
//                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                    Log.i("tag", "This'll run 300 milliseconds later");
//                }}, 1000);
//            pad.setVisibility(VISIBLE);
        }
        else{
//            setImeVisibility(false);
//            pad.setVisibility(GONE);
//            new android.os.Handler().postDelayed(new Runnable() {
//                public void run() {
//                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//                }}, 1000);
//            super.onFocusChanged(focused, direction, previouslyFocusedRect);
//            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//            Log.d("webb.log", "adjustPan");
//            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//            imm.hideSoftInputFromWindow(main.getWindowToken(), 0);

//            InputMethodManager imm = (InputMethodManager) w.getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(mUserNameEdit, InputMethodManager.HIDE_IMPLICIT_ONLY);

        }
        Log.d("webb.log", " " + hasFocus());
    }
*/
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if(text == null || text.length() == 0){
            if(lastQuery == null || lastQuery.length() == 0) return;
            else post(resetView);
        }
        else{
            query = text.toString().toLowerCase();

            if(lastQuery != null && lastQuery.length() == 0 || query.contains(lastQuery)){
                Log.d("webb.appsearch", "add: " + lastQuery + " -> " + query);
                int result = apps.onQueryTextAdd(query);
                if(result == 0){
                    clear();
                }
            }
            else if(query.equals(lastQuery)){
                Log.d("webb.appsearch", "3 " + lastQuery + " " + query);
                return;
            }
            else {
                Log.d("webb.appsearch", "delete/replace: " + lastQuery + " -> " + query);
                apps.resetMatchedApps();
                int result = apps.onQueryTextAdd(query);
                if(result == 0){
                    clear();
                }
            }
        }
        lastQuery = query;
    }

//        @Override
//        public boolean onQueryTextSubmit(String query){
//            if(query.length() == 0){
//                // hide keyboard
////            Context c = getApplicationContext)(;)
////            InputMethodManager imm = (InputMethodManager)getSystemService(
////                    Context.INPUT_METHOD_SERVICE);
////            imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
//            }
//            else {
//                // launch highest-sorted app
//                launch(size()-1);
//            }
//            return true;
//        }
//    };

//    searchView.setOnCloseListener(new SearchView.OnCloseListener(){
//        @Override
//        public boolean onClose() {
//            Log.d("webb.appsearch", "searchView.closeListener");
//            if(searchView.getQuery().length() == 0){
////                    Log.d("webb.appsearch", "clearFocus");
//                searchView.clearFocus();
////                    bottomSpacer.setVisibility(LinearLayout.GONE);
//                return true;
//            }
//            else {
//                resetQuery();
//                return true;
//            }
//        }
//    });

//    searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            Log.d("webb.appsearch", "searchview.focusChange");
//            if (hasFocus) {
//                Log.d("webb.appsearch", "searchView.hasFocus");
////                    bottomSpacer.setVisibility(LinearLayout.VISIBLE);
////                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
////                    imm.showSoftInputFromInputMethod(searchView.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
//            }
//        }
//    });

    public void setSpacersSlow(){
        if(main == null) return;
        viewTreeObserver = main.getViewTreeObserver();
        if (!viewTreeObserver.isAlive()) return;
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                main.getWindowVisibleDisplayFrame(r);
                int heightDiff = main.getRootView().getHeight() - (r.bottom - r.top);
                if(statusbarHeight > 0 && heightDiff > statusbarHeight){
                    main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    keyboardHeight = heightDiff - statusbarHeight;
                    keyboardSpacer.getLayoutParams().height = heightDiff;
                    keyboardSpacer.setVisibility(VISIBLE);
                    statusbarSpacer.setVisibility(VISIBLE);
                    searchbarSpacer.setVisibility(GONE);
                    appsView.setVisibility(VISIBLE);
                }
                else if(statusbarHeight == 0 && heightDiff > 0){
                    statusbarHeight = heightDiff;
                    statusbarSpacer.getLayoutParams().height = statusbarHeight/2;
                }
            }
        });
    }

    public void clear(){
        setText("");
    }

    public void close(){
        if(getText().length() > 0){
            clear();
        } else {
            clearFocus();
        }
//            setImeVisibility(false);
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
                requestFocus();
                imm.showSoftInput(main,0);
                if(keyboardHeight > 0){
                    keyboardSpacer.setVisibility(VISIBLE);
                    statusbarSpacer.setVisibility(VISIBLE);
                    searchbarSpacer.setVisibility(GONE);
                }
            }
        }
    };

    private Runnable mHideImeRunnable = new Runnable() {
        public void run() {
            InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null){
                clearFocus();
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
                keyboardSpacer.setVisibility(GONE);
                statusbarSpacer.setVisibility(GONE);
                searchbarSpacer.setVisibility(VISIBLE);
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

    private Runnable resetView = new Runnable() {
        public void run() {
            post(apps.resetView);
        }
    };

    public void log(String t){
        Log.d("webb.log", t);
    }
}
