package loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class SmapPointObserver extends BroadcastReceiver {

	  private SmapPointLoader mLoader;

public SmapPointObserver(SmapPointLoader loader) {
    mLoader = loader;
	
    LocalBroadcastManager.getInstance(mLoader.getContext()).registerReceiver(this,
  	      new IntentFilter("refreshPoints"));
  }

  @Override
  public void onReceive(Context context, Intent intent) {
	  Log.i("SmapPointObserver: ", "++++++++received refresh");
	  mLoader.onContentChanged();
  }
}
