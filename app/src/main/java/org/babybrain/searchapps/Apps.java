package org.babybrain.searchapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Vibrator;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class Apps {
    private HashSet<String> allAppsHashes;
    private ArrayList<App> allApps;
    private ArrayList<App> matchedApps;
//    private HashSet<String> allAppsHashSet;
    public IconAdapter iconAdapter;
    private Context context;
    public SearchView.OnQueryTextListener queryListener;
    private Vibrator vibrator;
    private BroadcastReceiver br;
//    private IntentFilter intentFilter;
    public SearchTextView searchTextView;
    protected PackageManager pm;
    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER);

    public Apps(Context c) {
        context = c;
        pm = context.getPackageManager();
        initializeAllAppsList();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        keyboardAnchor = k;
//        initializeAppsSearch();
//        initializePackageChangeReceiver();
    }

/*
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
*/

    protected void add(ResolveInfo ri, PackageManager pm, Context c, String u){
        allApps.add(new App(ri, pm, c, u));
        allAppsHashes.add(u);
    }

    protected void remove(App app) {
        allAppsHashes.remove(app.uniqueName);
        allApps.remove(app); // slow... could try another method
    }

    protected void remove(String u, int i) {
        allAppsHashes.remove(u);
        allApps.remove(i);
    }

    // make list of all apps using sparser data structure for faster parsing and launching
    public void initializeAllAppsList(){
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(mainIntent, 0);
        int size = resolveInfoList.size();
        allApps = new ArrayList<App>(size);
        allAppsHashes = new HashSet<String>(size);
        for (ResolveInfo ri : resolveInfoList) {
            String uniqueName = ri.activityInfo.packageName + ":" + ri.activityInfo.name;
            add(ri, pm, context, uniqueName);
        }
    }

    public void updateAllAppsList(){
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(mainIntent, 0);
        HashSet<String> missingAppsHashes = new HashSet<String>(allAppsHashes);
        boolean changes = false;
        for (ResolveInfo ri : resolveInfoList) {
            String uniqueName = ri.activityInfo.packageName + ":" + ri.activityInfo.name;
            missingAppsHashes.remove(uniqueName);
            if(allAppsHashes.contains(uniqueName)) continue;
            // new app!
            add(ri, pm, context, uniqueName);
            changes = true;
        }
        for(int i=0; i < allApps.size(); i++) {
            String uniqueName = allApps.get(i).uniqueName;
            if(!missingAppsHashes.contains(uniqueName)) continue;
            // missing (uninstalled) app!
            remove(uniqueName, i);
            changes = true;
        }
        if(changes){
            resetView.run();
        }
    }

    public App get(int i){
        return matchedApps.get(i);
    }

    public void launch(int i){
        App app = matchedApps.get(i);
        //@todo handle uninstalling apps more betterer
        if(app.launch()) return;

        // missing app!
        remove(app);
        matchedApps.remove(i);
        updateView();
//        Log.d("webb", String.valueOf(i) + " " + app.lcLabel);
    }

    public void info(int i) {
        matchedApps.get(i).startAppInfo();
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
        searchTextView.clear();
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

    public int query(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (lengthAfter == 0) {
            resetMatchedApps();
            updateView();
            return size();
        }
        if (lengthAfter < lengthBefore) resetMatchedApps();
        return filter(text.toString().toLowerCase(), start, lengthBefore,lengthAfter);
    }

    public int filter(String lclabel, int start, int lengthBefore, final int lengthAfter){
        int nMatchedApps = 0;
        ArrayList<App> newMatchedApps = new ArrayList<App>(matchedApps.size()/2); // decent guess
        for(int i=0; i < matchedApps.size(); i++){
            App app = matchedApps.get(i);
            if(app.matches(lclabel)){
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
                vibrator.vibrate(100);
                resetQuery();
                return 0;
            default:
//                Log.d("webb.appsearch", ">1 matches");
                matchedApps = newMatchedApps;
                Collections.sort(matchedApps, new InitialStringComparator(lclabel));
                updateView();
                return nMatchedApps;
        }
    }

    private class InitialStringComparator implements Comparator<App>{
        private String lclabel;
        public InitialStringComparator(String l){
            lclabel = l;
        }
        @Override
        public int compare(App a1, App a2) {
            String l1 = a1.lcLabel;
            String l2 = a2.lcLabel;
            int maxComparisons = Math.min(Math.min(l1.length(), l2.length()), lclabel.length());
            for (int i = 0; i < maxComparisons; i++) {
                char c = lclabel.charAt(i);
                char c1 = l1.charAt(i);
                char c2 = l2.charAt(i);
                if(c == c1 && c != c2) return 1;
                if(c == c2 && c != c1) return -1;
            }
            return 0;
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
//    private boolean isSystemPackage(PackageInfo pkgInfo) {
//        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
//                : false;
//    }
}