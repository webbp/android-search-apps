package org.babybrain.searchapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchAppsActivity extends Activity {
    private RelativeLayout main;
    private Apps apps;
    private GridView appsView;
    private SearchTextView searchTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d("webb","onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        main = (RelativeLayout) findViewById(R.id.main);
        apps = new Apps(this);
        appsView = (GridView) findViewById(R.id.appsView);
        appsView.setAdapter(apps);
        appsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                apps.launch(position);
            }
        });
        appsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                apps.info(position);
                return true;
            }
        });
        searchTextView = (SearchTextView) findViewById(R.id.appSearchView);
        searchTextView.mainActivity = this;
        searchTextView.x = (ImageView) findViewById(R.id.close);
        apps.searchTextView = searchTextView;
        searchTextView.x.setOnClickListener(searchTextView.xClickListener); // slower than setOnTouchListener; don't use
        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // if there's a query, launch the first app, else close the field
                    if (searchTextView.length() > 0) {
                        apps.launchBestGuess();
                    } else {
                        searchTextView.close();
                    }
                }
                return false;
            }
        });
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
                if(lengthAfter == 0 && lengthBefore == 0) return;
//                Log.d("webb", "onTextChanged");
                apps.query(text, lengthBefore, lengthAfter);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    protected void onRestart(){
//        Log.d("webb", "onRestart");
//        Log.d("webb", "onRestart, keyboardWasVisible:"+String.valueOf(searchTextView.keyboardWasVisible));
        super.onRestart();
        apps.resumeTime = (int)(System.currentTimeMillis() / 1000);
        apps.updateAllAppsList();
    }

    @Override
    protected void onStop() {
//        Log.d("webb", "onStop");
        super.onStop();
        apps.onStop();
    }
}
