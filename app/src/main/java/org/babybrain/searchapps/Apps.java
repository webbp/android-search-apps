package org.babybrain.searchapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Vibrator;
import android.util.Log;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Apps {
    private ArrayList<App> allApps;
    private ArrayList<App> matchedApps;
    private GridView gridView;
    private SearchView searchView;
    public IconAdapter iconAdapter;
    private Context context;
    private String query = "";
    private String lastQuery = "";
    public SearchView.OnQueryTextListener queryListener;
    private Vibrator vibrator;
    private BroadcastReceiver br;
    private IntentFilter intentFilter;
    public SearchTextView appSearchView;

    public Apps(Context c) {
        context = c;
//        keyboardAnchor = k;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        initializeAllAppsList();
//        initializeAppsSearch();
//        initializePackageChangeReceiver();
    }

    public void initializePackageChangeReceiver(){
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                Log.d("webb.appsearch.receiver", "br: " + intent.toString());
                initializeAllAppsList();
                resetMatchedApps();
            }
        };
        context.registerReceiver(br, intentFilter);
    }

    // make list of all apps using sparser data structure for faster parsing and launching
    private void initializeAllAppsList(){
        PackageManager pm = context.getPackageManager();
        allApps = new ArrayList<App>();
        for (ResolveInfo ri : getResolveInfoList(pm)) {
            allApps.add(new App(ri, pm, context));
        }
    }

    // get list of all apps' ResolveInfo
    private List<ResolveInfo> getResolveInfoList(PackageManager pm){
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pm.queryIntentActivities(mainIntent, 0);
    }

    public App get(int i){
        return matchedApps.get(i);
    }

    public void launch(int i){
        App app = matchedApps.get(i);
        if(!app.launch()){
            //@todo remove(Object) is inefficient
            allApps.remove(app);
            matchedApps.remove(app);
            updateView();
        }
//        Log.d("webb", String.valueOf(i) + " " + app.lcLabel);
    }

    public void info(int i) {
        App app = matchedApps.get(i);
        app.startAppInfo();
    }

    public void launchLast(){
        launch(size()-1);
    }

    public int size(){
        return matchedApps.size();
    }

//    private void initializeAppsSearch(){
//        queryListener = new SearchView.OnQueryTextListener(){
//            @Override
//            public boolean onQueryTextChange(String query){
//                Log.d("webb.appsearch", "onQuery: " + lastQuery + " -> " + query);
//                if(query.length() == 0){
//                    Log.d("webb.appsearch", "empty: " + lastQuery + " -> " + query);
//                    if(lastQuery.length() > 0) resetView();
//                }
//                else if(lastQuery.length() == 0 || query.contains(lastQuery)){
//                    Log.d("webb.appsearch", "add: " + lastQuery + " -> " + query);
//                    onQueryTextAdd(query);
//                }
////                else if(query.equals(lastQuery)){
////                    Log.d("webb.appsearch", "3 " + lastQuery + " " + query);
////                    return true;
////                }
//                else {
//                    Log.d("webb.appsearch", "delete/replace: " + lastQuery + " -> " + query);
//                    resetMatchedApps();
//                    onQueryTextAdd(query);
//                }
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String query){
//                if(query.length() == 0){
//                    // hide keyboard
////            Context c = getApplicationContext)(;)
////            InputMethodManager imm = (InputMethodManager)getSystemService(
////                    Context.INPUT_METHOD_SERVICE);
////            imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
//                }
//                else {
//                    // launch highest-sorted app
//                    launch(size()-1);
//                }
//                return true;
//            }
//        };
//
//        searchView.setOnCloseListener(new SearchView.OnCloseListener(){
//            @Override
//            public boolean onClose() {
//                Log.d("webb.appsearch", "searchView.closeListener");
//                if(searchView.getQuery().length() == 0){
////                    Log.d("webb.appsearch", "clearFocus");
//                    searchView.clearFocus();
////                    bottomSpacer.setVisibility(LinearLayout.GONE);
//                    return true;
//                }
//                else {
//                    resetQuery();
//                    return true;
//                }
//            }
//        });
//
//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.d("webb.appsearch", "searchview.focusChange");
//                if (hasFocus) {
//                    Log.d("webb.appsearch", "searchView.hasFocus");
////                    bottomSpacer.setVisibility(LinearLayout.VISIBLE);
////                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
////                    imm.showSoftInputFromInputMethod(searchView.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
//                }
//            }
//        });
//
//    }

    public void resetQuery(){
        appSearchView.clear();
    }

    public void resetQuery(String newQuery){
//        searchView.setQuery((CharSequence) newQuery, false);
    }

    public void resetMatchedApps(){
        matchedApps = (ArrayList<App>) allApps.clone();
    }

    public void updateView(){
        iconAdapter.notifyDataSetChanged();
    }

    public Runnable resetView = new Runnable() {
        public void run() {
            sort();
            resetMatchedApps();
            updateView();
        }
    };

    public class TemporalDiscountingAppComparator implements Comparator<App> {
        private final int now = (int)(System.currentTimeMillis() / 1000);
        private static final double discountFactor = 0.1;
        @Override
        public int compare(App x, App y) {            
            if(x.nLaunches ==0 || y.nLaunches ==0) return x.lastLaunch - y.lastLaunch;
            // ...so safe to assume both have >0 nLaunches
            int xInterval = now - x.lastLaunch;
            int yInterval = now - y.lastLaunch;
//            double xHypMeanInterval = x.meanInterval + (xInterval - x.meanInterval) / x.nLaunches;
//            double yHypMeanInterval = y.meanInterval + (yInterval - y.meanInterval) / y.nLaunches;
//            double xValue = 1/xHypMeanInterval;
//            double yValue = 1/yHypMeanInterval;
//            double xValueDiscounted = xValue/(1+discountFactor*xInterval);
//            double yValueDiscounted = yValue/(1+discountFactor*yInterval);
            double xValueDiscounted = x.nLaunches/(1+discountFactor*xInterval);
            double yValueDiscounted = y.nLaunches/(1+discountFactor*yInterval);
//            Log.d("webb", TextUtils.join(" ", new String[]{
//                    x.lcLabel,
//                    String.valueOf(x.nLaunches),
//                    String.valueOf(xInterval),
//                    String.valueOf(x.meanInterval),
//                    String.valueOf(xHypMeanInterval),
//                    String.valueOf(xValue),
//                    String.valueOf(xValueDiscounted),
//                    y.lcLabel,
//                    String.valueOf(y.nLaunches),
//                    String.valueOf(yInterval),
//                    String.valueOf(y.meanInterval),
//                    String.valueOf(yHypMeanInterval),
//                    String.valueOf(yValue),
//                    String.valueOf(yValueDiscounted),
//            }));
            if (xValueDiscounted > yValueDiscounted) return 1;
            if (xValueDiscounted < yValueDiscounted) return -1;
            return x.lastLaunch - y.lastLaunch; // rare
        }
    }

    public void sort(){
        Collections.sort(allApps, new TemporalDiscountingAppComparator());
    }

    public void saveAppLaunchData(SharedPreferences appLaunchData){
        App app;
        SharedPreferences.Editor editor = appLaunchData.edit();
        for(int i=0; i < allApps.size(); i++){
            app = allApps.get(i);
            if(app.nLaunches >0){
                editor.putInt(app.lcLabel+":nLaunches", app.nLaunches);
            }
            if(app.lastLaunch >0){
                editor.putInt(app.lcLabel+":lastLaunch", app.lastLaunch);
            }
            if(app.meanInterval >0){
                editor.putFloat(app.lcLabel + ":meanInterval", (float) app.meanInterval);
            }
        }
        editor.commit();
    }

//    public void restoreAppsLaunchData(Bundle savedInstanceState){
    public void restoreAppLaunchData(SharedPreferences appLaunchData){
        if (appLaunchData != null) {
            App app;
            int result;
            double result2;
            boolean restored = false;
            for(int i=0; i < allApps.size(); i++){
                app = allApps.get(i);
                result = appLaunchData.getInt(app.lcLabel+":nLaunches",0);
                if(result>0){
                    app.nLaunches = result;
                    restored = true;
                }
                result = appLaunchData.getInt(app.lcLabel+":lastLaunch",0);
                if(result>0){
                    app.lastLaunch = result;
                    restored = true;
                }
                result2 = (double)appLaunchData.getFloat(app.lcLabel+":meanInterval",0);
                if(result2>0){
                    app.meanInterval = result;
                    restored = true;
                }
            }
            if(restored){
                sort();
            }
        }
        resetMatchedApps();
    }

    public int onQueryTextAdd(String q){
        App app;
        int nMatchedApps = 0;
        ArrayList<App> newMatchedApps = new ArrayList<App>(matchedApps.size());
        query = q.toLowerCase();

        for(int i=0; i < matchedApps.size(); i++){
            app = matchedApps.get(i);
            if(app.matches(query)){
                newMatchedApps.add(app);
                nMatchedApps++;
            }
        }

        switch(nMatchedApps){
            case 1:
//                Log.d("webb.appsearch", "1 match");
                matchedApps = newMatchedApps;
                updateView();
                launch(0);
                resetQuery();
                return 1;
            case 0:
//                Log.d("webb.appsearch", "0 matches");
//                resetQuery(lastQuery); // doesn't work (e.g., try gz, then z)
                resetQuery();
                vibrator.vibrate(100);
                return 0;
            default:
//                Log.d("webb.appsearch", ">1 matches");
                matchedApps = newMatchedApps;
                Collections.sort(matchedApps, new Comparator<App>() {
                    public int compare(App a1, App a2) {
                        String l1 = a1.lcLabel;
                        String l2 = a2.lcLabel;
                        int maxComparisons = Math.min(query.length(), Math.min(l1.length(), l2.length()));
                        for (int i = 0; i < maxComparisons; i++) {
                            if (l1.charAt(i) == query.charAt(i) && l2.charAt(i) != query.charAt(i))
                                return 1;
                            else if (l1.charAt(i) != query.charAt(i) && l2.charAt(i) == query.charAt(i))
                                return -1;
                        }
                        return 0;
                    }
                });
                updateView();
                return newMatchedApps.size();
        }
    }

    /**
     * Return whether the given PackgeInfo represents a system package or not.
     * User-installed packages (Market or otherwise) should not be denoted as
     * system packages.
     * (Kenneth Evans, 2011, in http://stackoverflow.com/questions/2695746/how-to-get-a-list-of-installed-android-applications-and-pick-one-to-run)
     *
     * @param pkgInfo
     * @return
     */
    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }
}