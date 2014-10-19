package org.babybrain.searchapps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class Apps {
    private HashSet<String> allAppsHashes;
    private ArrayList<App> allApps;
    private ArrayList<App> matchedApps;
    public IconAdapter iconAdapter;
    private Context context;
    private Vibrator vibrator;
    //    private IntentFilter intentFilter;
    public SearchTextView searchTextView;
    private PackageManager pm;
    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER);

    public Apps(Context c) {
        context = c;
        pm = context.getPackageManager();
        initializeAllAppsList();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    protected void add(ResolveInfo ri, PackageManager pm, Context c, String u) {
        allApps.add(new App(ri, pm, c, u));
        allAppsHashes.add(u);
    }

    protected void remove(String u, int i) {
        allAppsHashes.remove(u);
        allApps.remove(i);
    }

    protected void remove(App app, int i) {
        allAppsHashes.remove(app.uniqueName);
        matchedApps.remove(i);
        allApps.remove(app); // slow... could try another method
    }

    // make list of all apps using sparser data structure for faster parsing and launching
    public void initializeAllAppsList() {
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(mainIntent, 0);
        int size = resolveInfoList.size();
        allApps = new ArrayList<App>(size);
        allAppsHashes = new HashSet<String>(size);
        for (ResolveInfo ri : resolveInfoList) {
            String uniqueName = ri.activityInfo.packageName + ":" + ri.activityInfo.name;
            Log.d("webb", "init: new app " + uniqueName);
            add(ri, pm, context, uniqueName);
        }
    }

    private Runnable updateAllAppsListRunnable = new Runnable() {
        @Override
        public void run() {
            List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(mainIntent, 0);
            HashSet<String> missingAppsHashes = new HashSet<String>(allAppsHashes);
            boolean changes = false;
            for (ResolveInfo ri : resolveInfoList) {
                String uniqueName = ri.activityInfo.packageName + ":" + ri.activityInfo.name;
                missingAppsHashes.remove(uniqueName);
                if (allAppsHashes.contains(uniqueName)) continue;
                Log.d("webb", "update: new app " + uniqueName);
                // new app!
                add(ri, pm, context, uniqueName);
                changes = true;
            }
            for (int i = 0; i < allApps.size() && missingAppsHashes.size() > 0; i++) {
                String uniqueName = allApps.get(i).uniqueName;
                if (!missingAppsHashes.contains(uniqueName)) continue;
                // missing (uninstalled) app!
                Log.d("webb", "update: missing app " + uniqueName);
                remove(uniqueName, i);
                changes = true;
            }
            if (changes) {
                sort();
                resetMatchedApps();
                ((Activity)context).runOnUiThread(new Runnable() { // http://stackoverflow.com/a/5162096/1563960 + http://stackoverflow.com/a/13086422/1563960
                    @Override
                    public void run() {
                        updateView();
                    }
                });
            }
        }
    };
    public void updateAllAppsList() {
        new Thread(updateAllAppsListRunnable).start();
    }

    public App get(int i){
        return matchedApps.get(i);
    }

    public void launch(int i){
        App app = matchedApps.get(i);
        //@todo handle uninstalling apps more betterer
        if(app.launch()) return;

        // missing app!
        remove(app, i);
        updateView();
//        Log.d("webb", String.valueOf(i) + " " + app.lcLabel);
    }

    public void info(int i) {
        matchedApps.get(i).info();
    }

    public void launchBestGuess(){
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
//        matchedApps = (ArrayList<App>) allApps.clone();
        matchedApps = allApps;
    }

    public void updateView(){
        iconAdapter.notifyDataSetChanged();
    }

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
        SharedPreferences.Editor editor = appLaunchData.edit();
        for(App app : allApps){
            if(app.nLaunches>0) editor.putInt(app.lcLabel+":nLaunches", app.nLaunches);
            if(app.lastLaunch>0) editor.putInt(app.lcLabel+":lastLaunch", app.lastLaunch);
            if(app.meanInterval>0) editor.putFloat(app.lcLabel + ":meanInterval", (float) app.meanInterval);
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

    public TextWatcher queryListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            if(query(text, start, lengthBefore, lengthAfter) == 0) searchTextView.clear();
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
        @Override
        public void afterTextChanged(Editable editable) {}
    };

    public int query(CharSequence query, int start, int lengthBefore, int lengthAfter) {
        if(lengthAfter == 0) {
            resetMatchedApps();
            updateView();
            return size();
        }
        if(lengthAfter < lengthBefore) resetMatchedApps();
        return filter(query.toString().toLowerCase(), start, lengthBefore, lengthAfter);
    }

    public int filter(String query, int start, int lengthBefore, final int lengthAfter){
        // find the array indices of apps with lowercase labels matching query
        int nMatches=0,
                nPrevMatches=size(),
                matchIndices[] = new int[nPrevMatches];
        for(int i=0; i<nPrevMatches; i++){
            App app = get(i);
            if(app.matches(query)){
                matchIndices[nMatches] = i;
                nMatches++;
            }
        }
        // if no change in set of matches and return
        if(nMatches==nPrevMatches) return nMatches;
        // if there are 0 matches, reset the query and return
        if(nMatches==0){
//            Log.d("webb.appsearch", "0 matches");
            vibrator.vibrate(100);
            resetQuery();
            return 0;
        }
        // use the matched app indices to create the new matched app data structure
        ArrayList<App> newMatchedApps = new ArrayList<App>(nMatches);
        for(int i=0; i < nMatches; i++){
            newMatchedApps.add(matchedApps.get(matchIndices[i]));
        }
        // replace the old matched app data with the new
        matchedApps = newMatchedApps;
        // if there's one match, launch it
        if(nMatches==1) {
//            Log.d("webb.appsearch", "1 match");
            launch(0);
            resetQuery();
        } else { // else if multiple matches, sor
//        Log.d("webb.appsearch", ">1 matches");
            appLabelInitialStringToQueryComparator.setQuery(query);
            Collections.sort(matchedApps, appLabelInitialStringToQueryComparator);
            updateView();
        }
        updateView();
        return nMatches;
    }

    private AppLabelInitialStringToQueryComparator appLabelInitialStringToQueryComparator = new AppLabelInitialStringToQueryComparator();
    private class AppLabelInitialStringToQueryComparator implements Comparator<App>{
        private String query;
        public void setQuery(String q){query = q;}
        @Override
        public int compare(App a1, App a2) {
            String l1 = a1.lcLabel;
            String l2 = a2.lcLabel;
            int maxComparisons = Math.min(Math.min(l1.length(), l2.length()), query.length());
            for (int i = 0; i < maxComparisons; i++) {
                char c = query.charAt(i);
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