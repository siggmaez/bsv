package dev.bsv.integration;

import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/** Initializes only our in-process UI; does not intercept game or network code. */
public final class BsvProvider extends ContentProvider implements Application.ActivityLifecycleCallbacks {
    @Override public boolean onCreate() {
        ((Application) getContext().getApplicationContext()).registerActivityLifecycleCallbacks(this);
        Log.i("BSV", "In-process menu registered");
        return true;
    }
    @Override public void onActivityResumed(Activity activity) {
        if (activity.getClass().getName().equals("com.supercell.brawlstars.GameApp")) {
            activity.getWindow().getDecorView().post(() -> BsvMenu.attach(activity));
        }
    }
    @Override public void onActivityCreated(Activity a, Bundle b) {}
    @Override public void onActivityStarted(Activity a) {}
    @Override public void onActivityPaused(Activity a) {}
    @Override public void onActivityStopped(Activity a) {}
    @Override public void onActivitySaveInstanceState(Activity a, Bundle b) {}
    @Override public void onActivityDestroyed(Activity a) {}
    @Override public Cursor query(Uri u, String[] p, String s, String[] a, String o) { return null; }
    @Override public String getType(Uri u) { return null; }
    @Override public Uri insert(Uri u, ContentValues v) { return null; }
    @Override public int delete(Uri u, String s, String[] a) { return 0; }
    @Override public int update(Uri u, ContentValues v, String s, String[] a) { return 0; }
}
