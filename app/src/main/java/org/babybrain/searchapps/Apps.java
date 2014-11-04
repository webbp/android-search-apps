package org.babybrain.searchapps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class Apps extends BaseAdapter {
    private HashSet<String> allAppsHashes = new HashSet<String>(0);
    private ArrayList<App> allApps = new ArrayList<App>(0);
    private ArrayList<App> matchedApps = new ArrayList<App>(0);;
    private Context context;
    private Vibrator vibrator;
    public SearchTextView searchTextView;
    private PackageManager pm;
    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER);
    SharedPreferences launchData;
    SharedPreferences.Editor launchDataEditor;
    public int resumeTime;
    private boolean needsSort = true;
    private LayoutInflater inflater;
    private ImageView imageView;
    private TextView textView;
    private BroadcastReceiver br;
    private IntentFilter appsChangedIntentFilter;
    private boolean isAppsChangedReceiverRegistered = false;

    public Apps(Context c) {
        context = c;
        pm = context.getPackageManager();
        launchData = context.getSharedPreferences("launchData", 0);
        launchDataEditor = launchData.edit();
        inflater = LayoutInflater.from(context);
        initAllAppsList();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        initAppsChangedReceiver();
    }

    private void initAppsChangedReceiver() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateAllAppsList();
            }
        };
        appsChangedIntentFilter = new IntentFilter();
        appsChangedIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appsChangedIntentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        appsChangedIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appsChangedIntentFilter.addDataScheme("package");
        registerAppsChangedReceiver();
    }

    public void registerAppsChangedReceiver(){
        if(!isAppsChangedReceiverRegistered){
            isAppsChangedReceiverRegistered = true;
            context.registerReceiver(br, appsChangedIntentFilter);
//            Log.d("webb", "registerAppsChangedReceiver");
        }
    }

    private void unregisterAppsChangedReceiver(){
        if(isAppsChangedReceiverRegistered){
            isAppsChangedReceiverRegistered = false;
            context.unregisterReceiver(br);
//            Log.d("webb", "unregisterAppsChangedReceiver");
        }
    }

    public boolean hasStableIds() {
        return false;
    }

    public int getCount() {
        return size();
    }

    public Object getItem(int position) {
        return get(position);
    }

    public long getItemId(int position) {
//        Log.d("webb", "getItemId "+String.valueOf(apps.size())+": "+String.valueOf(position)+": "+apps.get(position).lcLabel);
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
//        Log.d("webb", "getView "+String.valueOf(apps.size())+": "+String.valueOf(position)+": "+apps.get(position).lcLabel);
        if(view == null){
            view = inflater.inflate(R.layout.icon, null); //, parent, false);
        }
        App app = get(position);
        imageView = (ImageView)view.findViewById(R.id.icon_image);
        imageView.setImageDrawable(app.icon);
        textView = (TextView)view.findViewById(R.id.icon_text);
        textView.setText(app.label);
        return view;
    }

    // main app add function
    private void add(ResolveInfo ri, String uniqueName) {
        App app = new App(ri, pm, context, uniqueName);
        restoreLaunchData(app);
        allApps.add(app);
        allAppsHashes.add(uniqueName);
    }

    // convenience wrapper
    private void add(ResolveInfo ri) {
        String uniqueName = ri.activityInfo.packageName + ":" + ri.activityInfo.name;
        add(ri, uniqueName);
    }

    protected void remove(int i, String uniqueName) {
        allApps.remove(i);
        allAppsHashes.remove(uniqueName);
    }

    protected void removeMatchedApp(int i, App app) {
        matchedApps.remove(i);
        updateView();
        allApps.remove(app); // slow, but infrequent
        allAppsHashes.remove(app.uniqueName);
    }

    // make list of all apps using sparser data structure for faster parsing and launching
    private void initAllAppsList() {
//        Log.d("webb", "initAllAppsList");
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(mainIntent, 0);
        int size = resolveInfoList.size();
        allApps = new ArrayList<App>(size);
        allAppsHashes = new HashSet<String>(size);
        for (ResolveInfo ri : resolveInfoList) {
//            Log.d("webb", "init: new app " + ri.activityInfo.packageName + ":" + ri.activityInfo.name);
            add(ri);
        }
        sort();
        resetMatchedApps();
        updateView();
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
//                Log.d("webb", "update: new app " + uniqueName);
                // new app!
                add(ri, uniqueName);
                changes = true;
            }
            for (int i = 0; i < allApps.size() && missingAppsHashes.size() > 0; i++) {
                String uniqueName = allApps.get(i).uniqueName;
                if (!missingAppsHashes.contains(uniqueName)) continue;
                // missing (uninstalled) app!
//                Log.d("webb", "update: missing app " + uniqueName);
                remove(i, uniqueName);
                changes = true;
            }
            if(changes) {
                needsSort = true;
                sort();
                resetMatchedApps();
                updateViewOnUIThread();
            }
            registerAppsChangedReceiver();
        }
    };
    public void updateAllAppsList() {
//        Log.d("webb", "updateAllAppsList");
        new Thread(updateAllAppsListRunnable).start();
    }

    private Runnable updateViewRunnable = new Runnable() {
        @Override
        public void run() {
            updateView();
        }
    };

    // http://stackoverflow.com/a/5162096/1563960 + http://stackoverflow.com/a/13086422/1563960
    private void updateViewOnUIThread(){
        ((Activity)context).runOnUiThread(updateViewRunnable);
    }

    public App get(int i){
        return matchedApps.get(i);
    }

    public void launch(int i){
//        Log.d("webb", String.valueOf(i) + " " + app.lcLabel);
        App app = matchedApps.get(i);
        boolean launched = app.launch();
        needsSort = true;
        saveLaunchData(app);
        // missing app!
        if(!launched) removeMatchedApp(i, app);
    }

    private void saveLaunchData(App app){
        launchDataEditor.putInt(app.lcLabel+":nLaunches", app.nLaunches);
        launchDataEditor.putInt(app.lcLabel+":lastLaunch", app.lastLaunch);
        launchDataEditor.putFloat(app.lcLabel + ":meanInterval", (float) app.meanInterval);
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

    public void resetQuery(){
        searchTextView.clear();
    }

    public void onStop(){
        unregisterAppsChangedReceiver();
        sort();
        updateView();
        searchTextView.close();
    }

    public void resetMatchedApps(){
        matchedApps = allApps;
    }

    public void updateView(){
//        adapter.notifyDataSetChanged();
        notifyDataSetChanged();
    }

    public class TemporalDiscountingAppComparator implements Comparator<App> {
        private int now = resumeTime;
        private double discountFactor = 0.1;
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
        if(needsSort){
            Collections.sort(allApps, new TemporalDiscountingAppComparator());
            needsSort = false;
        }
    }

    private void restoreLaunchData(App app){
        boolean restored = false;
        int nLaunches = launchData.getInt(app.lcLabel+":nLaunches",0);
        if(nLaunches>0){
            app.nLaunches = nLaunches;
            restored = true;
        }
        int lastLaunch = launchData.getInt(app.lcLabel+":lastLaunch",0);
        if(lastLaunch>0){
            app.lastLaunch = lastLaunch;
            restored = true;
        }
        double meanInterval = (double) launchData.getFloat(app.lcLabel+":meanInterval",0);
        if(meanInterval>0){
            app.meanInterval = meanInterval;
            restored = true;
        }
        if(restored) needsSort = true;
    }

    public void query(CharSequence query, int lengthBefore, int lengthAfter) {
        if(lengthAfter == 0) {
            resetMatchedApps();
            updateView();
            return;
        }
        if(lengthAfter < lengthBefore) resetMatchedApps();
        filter(query.toString().toLowerCase());
    }

    public void filter(String query){
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
        // if no change in set of matches, return
        if(nMatches==nPrevMatches) return;
        // if there are 0 matches, reset the query and return
        if(nMatches==0){
//            Log.d("webb.appsearch", "0 matches");
            vibrator.vibrate(100);
            resetQuery();
            return;
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
        } else { // else if multiple matches, sor
//        Log.d("webb.appsearch", ">1 matches");
            appLabelInitialStringToQueryComparator.setQuery(query);
            Collections.sort(matchedApps, appLabelInitialStringToQueryComparator);
        }
        updateView();
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