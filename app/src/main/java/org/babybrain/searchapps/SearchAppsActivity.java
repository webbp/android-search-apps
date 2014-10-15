package org.babybrain.searchapps;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by webb on 9/12/13.
 */
public class SearchAppsActivity extends Activity {
    private RelativeLayout main;
    private Apps apps;
    private NoTopFadeGridView appsView;
    private SearchTextView searchTextView;
    private IconAdapter iconAdapter;
//    private LinearLayout spacer;
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

        appsView = (NoTopFadeGridView) findViewById(R.id.appsView);
        iconAdapter = new IconAdapter(this, apps);
        appsView.setAdapter(iconAdapter);
        appsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                apps.launch(position);
            }
        });
        apps.iconAdapter = iconAdapter;
        searchTextView = (SearchTextView) findViewById(R.id.appSearchView);
        searchTextView.main = (View)main;
        searchTextView.apps = apps;
//        searchText.searchbarSpacer = (LinearLayout) findViewById(R.id.searchbarSpacer);
//        searchText.statusbarSpacer = (LinearLayout) findViewById(R.id.statusbarSpacer);
//        searchText.keyboardSpacer = (LinearLayout) findViewById(R.id.keyboardSpacer);
//        searchText.keyboardSpacerBottom = (LinearLayout) findViewById(R.id.keyboardSpacerBottom);
        searchTextView.appsView = appsView;
        searchTextView.w = getWindow();
        searchTextView.activity = this;
        searchTextView.x = (ImageView) findViewById(R.id.close);
        searchTextView.x.setOnTouchListener(searchTextView.closeTouchListener);
        apps.appSearchView = searchTextView;
//        appsView.clearFocus();
//        searchText.x.setOnClickListener(searchText.closeClickListener);  // slower

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask(){
//            @Override
//            public void run(){
//                apps.sort();
//                SearchAppsActivity.this.runOnUiThread(new Runnable() {
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
//                SearchAppsActivity.getActivity().runOnUiThread(setImageRunnable);
//            }
//        };
//
//        Timer timer = new Timer();
//        timer.schedule(task, splashTime);
    }

    @Override
    protected void onResume(){
        super.onResume();
//        Log.d("webb","onResume");
//        appsView.requestFocus();
//        appsView.requestFocusFromTouch();
//        searchText.keyboardSpacerBottom.getLayoutParams().height = searchText.keyboardHeight;
//        searchText.requestFocusFromTouch();
//        searchText.requestFocus();
        searchTextView.clearFocus();
        apps.resetView.run();
        if(searchTextView.hadFocus) searchTextView.requestFocus();
//        searchText.setSpacersSlow();
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
//        apps.saveAppsLaunchData(savedInstanceState);
//        savedInstanceState.putInt("MyInt", 1);
        apps.saveAppLaunchData(appLaunchData);
        super.onSaveInstanceState(savedInstanceState);
    }
}
