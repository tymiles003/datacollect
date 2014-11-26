package org.smap.smapTask.android.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.odk.collect.android.application.Collect;
import org.smap.smapTask.android.fragments.MapFragment;


public class MapLocationObserver extends BroadcastReceiver {

    private MapFragment mMap = null;

public MapLocationObserver(Context context, MapFragment map) {
    mMap = map;

    LocalBroadcastManager.getInstance(context).registerReceiver(this,
            new IntentFilter("locationChanged"));
  }

  @Override
  public void onReceive(Context context, Intent intent) {
      Log.i("Maps Activity: ", "++++++++received refresh");
      boolean updatePath = Collect.getInstance().isRecordLocation();
      mMap.setUserLocation(Collect.getInstance().getLocation(), updatePath);
  }
}
