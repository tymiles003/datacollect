/*
 * Copyright (C) 2014 Smap Consulting Pty Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.smap.smapTask.android.activities;

import java.util.List;

import org.smap.smapTask.android.loaders.TaskLoader;
import org.smap.smapTask.android.loaders.TaskEntry;

import org.smap.smapTask.android.adapters.TaskListArrayAdapter;
import org.odk.collect.android.provider.FormsProviderAPI.FormsColumns;
import org.odk.collect.android.provider.InstanceProviderAPI.InstanceColumns;
import org.odk.collect.android.activities.FormEntryActivity;
import org.odk.collect.android.application.Collect;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.smap.smapTask.android.R;
import org.smap.smapTask.android.receivers.LocationChangedReceiver;
import org.smap.smapTask.android.utilities.Constants;
import org.smap.smapTask.android.utilities.ManageForm;
import org.smap.smapTask.android.utilities.Utilities;

/**
 * Responsible for displaying buttons to launch the major activities. Launches some activities based
 * on returns of others.
 * 
 * @author Neil Penman 
 */
public class MainListActivity extends FragmentActivity  {
	
	private LoaderManager.LoaderCallbacks<List<TaskEntry>> mCallbacks;
	private AlertDialog mAlertDialog;

    private LocationManager locationManager;
    protected PendingIntent locationListenerPendingIntent;
	
	
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);

         // Create the ListFragment
         FragmentManager fm = getSupportFragmentManager();
         if (fm.findFragmentById(android.R.id.content) == null) {
             TaskListFragment list = new TaskListFragment();
             fm.beginTransaction().add(android.R.id.content, list).commit();
         }

         // Setup the location update Pending Intents
         locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
         Intent activeIntent = new Intent(this, LocationChangedReceiver.class);
         locationListenerPendingIntent = PendingIntent.getBroadcast(this, 1000, activeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
     }

	
	/*
	 * Fragment to display list of tasks
	 */
	 public static class TaskListFragment extends ListFragment implements
     		LoaderManager.LoaderCallbacks<List<TaskEntry>> { 
	
		private static final int TASK_LOADER_ID = 1;
		
		private TaskListArrayAdapter mAdapter;
	  	private MainListActivity mActivity;
		  	

	    public TaskListFragment() {
	    	super();    	
	    	mActivity = (MainListActivity) getActivity();
	    }
	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        mAdapter = new TaskListArrayAdapter(getActivity());
	        setListAdapter(mAdapter);
	        setListShown(false);
	        getLoaderManager().initLoader(TASK_LOADER_ID, null, this);
	        
	        registerForContextMenu(getListView());
	        
	        // Handle long item clicks
	        ListView lv = getListView();
	        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	            @Override
	            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
	                return onLongListItemClick(v,pos,id);
	            }
	        });
	
	    }
	    
	    @Override
	    public Loader<List<TaskEntry>> onCreateLoader(int id, Bundle args) {
	    	return new TaskLoader(getActivity());
	    }
	
	    @Override
	    public void onLoadFinished(Loader<List<TaskEntry>> loader, List<TaskEntry> data) {
	    	mAdapter.setData(data);

	    	if (isResumed()) {
	    		setListShown(true);
	    	} else {
	    		setListShownNoAnimation(true);
	    	}
	    }
	
	    @Override
	    public void onLoaderReset(Loader<List<TaskEntry>> loader) {
	      mAdapter.setData(null);
	    }
	    
	    
	    
	    
	    @Override
	    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    	inflater.inflate(R.menu.task_context, menu);
	        super.onCreateOptionsMenu(menu, inflater);
	    }
	    
		
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        return super.onOptionsItemSelected(item);
	    }

	    
	    /*
	     * Handle a long click on a list item
	     */
	    protected boolean onLongListItemClick(View v, int position, long id) {
	    	
	    	TaskEntry task = (TaskEntry) getListAdapter().getItem(position);
	    	
	    	if(task.type.equals("task")) {
		    	Intent i = new Intent(getActivity(), org.smap.smapTask.android.activities.TaskAddressActivity.class);
		        i.putExtra("id", task.id);
		        
		    	startActivity(i);
	    	}
	        return true;
	    }
		    
	    
	    /**
	     * Starts executing the selected task
	     */
	    @Override
	    public void onListItemClick(ListView listView, View view, int position, long id) {
	 	       
	    	TaskEntry entry = (TaskEntry) getListAdapter().getItem(position);
	
	    	
	    	if(entry.type.equals("task")) {
	    		String formPath = Collect.FORMS_PATH + entry.taskForm;
				if(entry.repeat) {
					entry.instancePath = duplicateInstance(formPath, entry.instancePath, entry);
				}
	    		completeTask(entry.instancePath, formPath, entry.id, entry.taskStatus);
	    	} else {
	    		Uri formUri = ContentUris.withAppendedId(FormsColumns.CONTENT_URI, entry.id);
	    		startActivity(new Intent(Intent.ACTION_EDIT, formUri));
	    	}

	    }

		/*
		 * Duplicate the instance
		 * Call this if the instance repeats
		 */
		public String duplicateInstance(String formPath, String originalPath, TaskEntry entry) {
			String newPath = null;

			// 1. Get a new instance path
			ManageForm mf = new ManageForm();
			newPath = mf.getInstancePath(formPath, 0);

			// 2. Duplicate the instance entry and get the new path
			Utilities.duplicateTask(originalPath, newPath, entry);

			// 3. Copy the instance files
			Utilities.copyInstanceFiles(originalPath, newPath);
			return newPath;
		}
	 
		/*
		 * The user has selected an option to edit / complete a task
		 */
		public void completeTask(String instancePath, String formPath, long taskId, String status) {
		
			// set the adhoc location
			boolean canComplete = false;
			try {
				canComplete = Utilities.canComplete(status);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Return if the user is not allowed to update this task
			if(!canComplete) {
				return;
			}
			
			// Get the provider URI of the instance 
	        String where = InstanceColumns.INSTANCE_FILE_PATH + "=?";
	        String[] whereArgs = {
	            instancePath
	        };
	       
			Cursor cInstanceProvider = Collect.getInstance().getContentResolver().query(InstanceColumns.CONTENT_URI, 
					null, where, whereArgs, null);
			
			if(cInstanceProvider.getCount() != 1) {
				Log.e("MainListActivity:completeTask", "Unique instance not found: count is:" + 
						cInstanceProvider.getCount());
			} else {
				cInstanceProvider.moveToFirst();
				Uri instanceUri = ContentUris.withAppendedId(InstanceColumns.CONTENT_URI,
		                cInstanceProvider.getLong(
		                cInstanceProvider.getColumnIndex(InstanceColumns._ID)));
				// Start activity to complete form
				Intent i = new Intent(Intent.ACTION_EDIT, instanceUri);
	
				i.putExtra(FormEntryActivity.KEY_FORMPATH, formPath);	// TODO Don't think this is needed
				i.putExtra(FormEntryActivity.KEY_TASK, taskId);			
				if(instancePath != null) {	// TODO Don't think this is needed
					i.putExtra(FormEntryActivity.KEY_INSTANCEPATH, instancePath);           
				}
				startActivity(i);
			} 
			cInstanceProvider.close();
			
		}

	 }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MainListActivity", "onStart============================");
        requestLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MainListActivity", "onStop============================");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        dismissDialogs();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MainListActivity", "onDestroy============================");
        disableLocationUpdates();
    }


    /**
     * Dismiss any dialogs that we manage.
     */
    private void dismissDialogs() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    /**
     * Start listening for location updates.
     */
    protected void requestLocationUpdates() {
        // Normal updates while activity is visible.
        // TODO manage multiple providers
        // TODO Manage provder being enabled / disabled

        /*
         * Only use GPS to get locations for tracking the user
         *  Using less accurate sources is not feasible to collect a gpx trail
         *  However it may be useful if we were just recording location of survey
         */
        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {     // Fix issue with errors on devices without GPS
            try {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.GPS_INTERVAL, Constants.GPS_DISTANCE, locationListenerPendingIntent);
			} catch (SecurityException e) {
				// Permission not granted
			}
        }
    }

    /**
     * Stop listening for location updates
     */
    protected void disableLocationUpdates() {


    try {
            locationManager.removeUpdates(locationListenerPendingIntent);
    } catch (Exception e) {
            // Ignore failures, we are exiting after all
    }


    }


}


