package org.babybrain.searchapps;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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
//        Log.d("webb","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        main = (RelativeLayout) findViewById(R.id.main);
        apps = new Apps(this, main);
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
        searchTextView.mainActivity = this;
        searchTextView.x = (ImageView) findViewById(R.id.close);
        searchTextView.x.setOnClickListener(searchTextView.xClickListener); // slower than setOnTouchListener; don't use
        apps.searchTextView = searchTextView;
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
        searchTextView.addTextChangedListener(apps.queryListener);
        searchTextView.requestFocus();
    }

    @Override
    protected void onRestart(){
//        Log.d("webb", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume(){
//        Log.d("webb", "onResume, keyboardWasVisible:"+String.valueOf(searchTextView.keyboardWasVisible));
        super.onResume();
        searchTextView.onResume();
        apps.updateAllAppsList();
    }

    @Override
    public void onPause() {
//        Log.d("webb", "onPause");
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        Log.d("webb", "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
        apps.saveAppLaunchData(appLaunchData);
    }
}
