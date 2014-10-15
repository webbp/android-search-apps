package org.babybrain.searchapps;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by webb on 9/12/13.
 */
public class SearchAppsActivity extends Activity {
    private RelativeLayout main;
    private Apps apps;
    private GridView appsView;
    private SearchTextView searchTextView;
    private IconAdapter iconAdapter;
    private SharedPreferences appLaunchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d("webb","onCreate");
        setContentView(R.layout.main);
        main = (RelativeLayout) findViewById(R.id.main);
        apps = new Apps(this);
//        if (savedInstanceState != null) {
//            apps.restoreAppsLaunchData(savedInstanceState);
//        }
        appLaunchData = getSharedPreferences("appLaunchData", 0);
        apps.restoreAppLaunchData(appLaunchData);

        appsView = (GridView) findViewById(R.id.appsView);
        iconAdapter = new IconAdapter(this, apps);
        appsView.setAdapter(iconAdapter);
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
        apps.iconAdapter = iconAdapter;
        searchTextView = (SearchTextView) findViewById(R.id.appSearchView);
        searchTextView.main = main;
        searchTextView.apps = apps;
        searchTextView.appsView = appsView;
        searchTextView.w = getWindow();
        searchTextView.activity = this;
        searchTextView.x = (ImageView) findViewById(R.id.close);
        searchTextView.x.setOnTouchListener(searchTextView.closeTouchListener);
//        searchText.x.setOnClickListener(searchText.closeClickListener); // slower than setOnTouchListener; don't use
        apps.appSearchView = searchTextView;
        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchTextView.onDoneClick();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
//        Log.d("webb","onResume");
        searchTextView.clearFocus();
        apps.resetView.run();
        if(searchTextView.hadFocus) searchTextView.requestFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.d("webb","onPause");
//        Log.d("webb", String.valueOf(searchText.hasFocus()));
        searchTextView.hadFocus = searchTextView.hasFocus();
        searchTextView.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        Log.d("webb","onSaveInstanceState");
        apps.saveAppLaunchData(appLaunchData);
        super.onSaveInstanceState(savedInstanceState);
    }
}
