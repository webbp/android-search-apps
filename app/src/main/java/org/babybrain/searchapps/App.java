package org.babybrain.searchapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class App implements Comparable<App>{
//    private String packageName;
//    private String className;
    private Intent intent;
    public Drawable icon;
    public CharSequence label;
    public String lcLabel;
    public int launchCount = 0;
    public int lastLaunch = 0;
    public boolean broken = false;
    private Context context;

    public App(ResolveInfo ri, PackageManager pm, Context c){
        context = c;
        intent = new Intent();
        intent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name); // required to differentiate e.g., Camera from Gallery
        intent.setAction("android.intent.action.MAIN"); // possibly useless
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required to launch e.g., Textra
        icon = ri.loadIcon(pm);
        label = ri.loadLabel(pm).toString();
        lcLabel = label.toString().toLowerCase();
    }

    public boolean launch(){
        try {
            context.startActivity(intent);
            launchCount++;
            lastLaunch = (int) (System.currentTimeMillis() / 1000);
            return true;
        } catch (Exception e){
            broken = true;
            return false;
        }
    }

    public boolean matches(String target){
        return lcLabel.contains(target);
    }

    @Override
    public int compareTo(App otherApp){
        // @todo maker this betters
        //if(launchCount>0) Log.d("webb.log", lcLabel + " " + launchCount);
        if(launchCount != otherApp.launchCount){
            return launchCount - otherApp.launchCount;
        } else {
            return lastLaunch - otherApp.lastLaunch;
        }
    }
}
