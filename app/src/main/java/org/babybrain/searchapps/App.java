package org.babybrain.searchapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

public class App {
    private Intent launchIntent = new Intent();
    private Intent appInfoIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    public Drawable icon;
    public CharSequence label;
    public String lcLabel;
    public int nLaunches = 0;
    public int lastLaunch = 0;
    public double meanInterval = 0;
//    public boolean broken = false;
    private Context context;
    public String uniqueName;

    public App(ResolveInfo ri, PackageManager pm, Context c, String u){
        context = c;
        launchIntent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name); // required to differentiate e.g., Camera from Gallery
        launchIntent.setAction("android.intent.action.MAIN"); // possibly useless
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required to launch e.g., Textra
        icon = ri.loadIcon(pm);
        label = ri.loadLabel(pm);
        lcLabel = label.toString().toLowerCase();
        appInfoIntent.setData(Uri.parse("package:" + ri.activityInfo.packageName));
        appInfoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required to start as new, separate task
        uniqueName = u;
//        appInfoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    public boolean launch(){
        if(!startApp()) return false;
        int now = (int)(System.currentTimeMillis() / 1000);
        nLaunches++;
        if(nLaunches >1) {
            int interval = now - lastLaunch;
            meanInterval += (interval - meanInterval)/nLaunches;
//            Log.d("webb", String.valueOf(meanInterval));
        }
//        Log.d("webb", String.valueOf(nLaunches));
        lastLaunch = now;
        return true;
    }

    public boolean matches(String target){
        return lcLabel.contains(target);
    }

    private boolean startApp() {
        try {
            context.startActivity(launchIntent);
        } catch (Exception e){
//            broken = true;
//            Log.d("webb",lcLabel + " is broken");
            return false;
        }
        return true;
    }

    public boolean startAppInfo(){
        try {
            context.startActivity(appInfoIntent);
        } catch (Exception e){
//            broken = true;
//            Log.d("webb",lcLabel + " is broken");
            return false;
        }
        return true;
    }

}
