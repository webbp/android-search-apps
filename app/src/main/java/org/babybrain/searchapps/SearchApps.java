package org.babybrain.searchapps;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by webb on 9/12/13.
 */
public class SearchApps extends Activity {
    private RelativeLayout main;
    private Apps apps;
    private GridViewNoTopFade appsView;
    private SearchText searchText;
    private IconAdapter iconAdapter;
//    private LinearLayout spacer;
    private SharedPreferences appLaunchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("webb","onCreate");
        setContentView(R.layout.main);
        main = (RelativeLayout) findViewById(R.id.main);
        apps = new Apps(this);
//        if (savedInstanceState != null) {
//            apps.restoreAppsLaunchData(savedInstanceState);
//        }
        appLaunchData = getSharedPreferences("appLaunchData", 0);
        apps.restoreAppLaunchData(appLaunchData);

        appsView = (GridViewNoTopFade) findViewById(R.id.appsView);
        iconAdapter = new IconAdapter(this, apps);
        appsView.setAdapter(iconAdapter);
        appsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                apps.launch(position);
            }
        });
        apps.iconAdapter = iconAdapter;
        searchText = (SearchText) findViewById(R.id.appSearchView);
        searchText.main = (View)main;
        searchText.apps = apps;
//        searchText.searchbarSpacer = (LinearLayout) findViewById(R.id.searchbarSpacer);
//        searchText.statusbarSpacer = (LinearLayout) findViewById(R.id.statusbarSpacer);
//        searchText.keyboardSpacer = (LinearLayout) findViewById(R.id.keyboardSpacer);
//        searchText.keyboardSpacerBottom = (LinearLayout) findViewById(R.id.keyboardSpacerBottom);
        searchText.appsView = appsView;
        searchText.w = getWindow();
        searchText.activity = this;
        searchText.x = (ImageView) findViewById(R.id.close);
        searchText.x.setOnTouchListener(searchText.closeTouchListener);
        apps.appSearchView = searchText;
//        appsView.clearFocus();
//        searchText.x.setOnClickListener(searchText.closeClickListener);  // slower

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask(){
//            @Override
//            public void run(){
//                apps.sort();
//                SearchApps.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        apps.updateView();
//                    }
//                });
//            }
//        }, 2000, 2000);

//        final Runnable sortAppsUpdateView = new Runnable() {
//            public void run() {
//                apps.sort();
//                apps.updateView();
//            }
//        };
//
//        TimerTask task = new TimerTask(){
//            public void run() {
//                SearchApps.getActivity().runOnUiThread(setImageRunnable);
//            }
//        };
//
//        Timer timer = new Timer();
//        timer.schedule(task, splashTime);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("webb","onResume");
//        appsView.requestFocus();
//        appsView.requestFocusFromTouch();
//        searchText.keyboardSpacerBottom.getLayoutParams().height = searchText.keyboardHeight;
//        searchText.requestFocusFromTouch();
//        searchText.requestFocus();
        searchText.clearFocus();
        apps.resetView.run();
        if(searchText.hadFocus) searchText.requestFocus();
//        searchText.setSpacersSlow();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("webb","onPause");
//        Log.d("webb", String.valueOf(searchText.hasFocus()));
        searchText.hadFocus = searchText.hasFocus();
        searchText.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d("webb","onSaveInstanceState");
//        apps.saveAppsLaunchData(savedInstanceState);
//        savedInstanceState.putInt("MyInt", 1);
        apps.saveAppLaunchData(appLaunchData);
        super.onSaveInstanceState(savedInstanceState);
    }
}
