package org.babybrain.searchapps;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by webb on 9/12/13.
 */
public class SearchApps extends Activity {
    private RelativeLayout main;
    private Apps apps;
    private GridViewNoTopFade appsView;
    private AppSearchView appSearchView;
    private IconAdapter iconAdapter;
    private LinearLayout spacer;
    private SharedPreferences appLaunchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        appSearchView = (AppSearchView) findViewById(R.id.appSearchView);
        appSearchView.main = (View)main;
        appSearchView.apps = apps;
//        appSearchView.searchbarSpacer = (LinearLayout) findViewById(R.id.searchbarSpacer);
//        appSearchView.statusbarSpacer = (LinearLayout) findViewById(R.id.statusbarSpacer);
//        appSearchView.keyboardSpacer = (LinearLayout) findViewById(R.id.keyboardSpacer);
//        appSearchView.keyboardSpacerBottom = (LinearLayout) findViewById(R.id.keyboardSpacerBottom);
        appSearchView.appsView = appsView;
        appSearchView.w = getWindow();
        appSearchView.activity = this;
        appSearchView.x = (ImageView) findViewById(R.id.close);
        appSearchView.x.setOnTouchListener(appSearchView.closeTouchListener);
        apps.appSearchView = appSearchView;
//        appSearchView.x.setOnClickListener(appSearchView.closeClickListener);  // slower
    }

    @Override
    protected void onResume(){
        super.onResume();
//        appSearchView.keyboardSpacerBottom.getLayoutParams().height = appSearchView.keyboardHeight;
        appSearchView.requestFocusFromTouch();
//        appSearchView.requestFocus();
        appSearchView.requestFocus();
        apps.resetView.run();
//        appSearchView.setSpacersSlow();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        apps.saveAppsLaunchData(savedInstanceState);
//        savedInstanceState.putInt("MyInt", 1);
        apps.saveAppLaunchData(appLaunchData);
        super.onSaveInstanceState(savedInstanceState);
    }
}
