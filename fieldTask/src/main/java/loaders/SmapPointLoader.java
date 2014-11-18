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

/*
 * Responsible for displaying loading points
 * 
 * @author Neil Penman (neilpenman@gmail.com)
 */
package loaders;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.provider.FormsProviderAPI.FormsColumns;
import org.smap.smapTask.android.provider.TraceProviderAPI.TraceColumns;
import org.smap.smapTask.android.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of AsyncTaskLoader which loads a {@code List<PointEntry>}
 * containing all tasks on the device.
 */
public class SmapPointLoader extends AsyncTaskLoader<List<PointEntry>> {

	private List<PointEntry> mPoints = null;
	private SmapPointObserver mSmapPointObserver;	// Monitor changes to task data

	public SmapPointLoader(Context ctx) {
		super(ctx);
	}

	/**
	 * This method is called on a background thread and generates a List of
	 * {@link loaders.PointEntry} objects.
	 */
	@Override
	public List<PointEntry> loadInBackground() {

		// Create corresponding array of entries and load their labels.
		ArrayList<PointEntry> entries = new ArrayList<PointEntry>(100);
		getPoints(entries);

		return entries;
	}

	private void getPoints(ArrayList<PointEntry> entries) {

        String [] proj = {TraceColumns._ID,
                TraceColumns.LAT,
                TraceColumns.LON,
                TraceColumns.TIME,
                };

        String sortOrder = TraceColumns._ID + " ASC, ";

        final ContentResolver resolver = Collect.getInstance().getContentResolver();
        Cursor pointListCursor = resolver.query(TraceColumns.CONTENT_URI, proj, null, null, sortOrder);


		if(pointListCursor != null) {
    		 
			pointListCursor.moveToFirst();
			while (!pointListCursor.isAfterLast()) {
        		 
                PointEntry entry = new PointEntry();

        		entry.lat = pointListCursor.getDouble(pointListCursor.getColumnIndex(TraceColumns.LAT));
                entry.lon = pointListCursor.getDouble(pointListCursor.getColumnIndex(TraceColumns.LON));
                entry.time = pointListCursor.getLong(pointListCursor.getColumnIndex(TraceColumns.TIME));
	             
	            entries.add(entry);
	            pointListCursor.moveToNext();
        	 }
    	}
        if(pointListCursor != null) {
            pointListCursor.close();
        }
	}

	/**
	 * Called when there is new data to deliver to the client. The superclass
	 * will deliver it to the registered listener (i.e. the LoaderManager),
	 * which will forward the results to the client through a call to
	 * onLoadFinished.
	 */
	@Override
	public void deliverResult(List<PointEntry> points) {
		if (isReset()) {
			Log.w("taskloader",
					"+++ Warning! An async query came in while the Loader was reset! +++");

			if (points != null) {
				releaseResources(points);
				return;
			}
		}

		// Hold a reference to the old data so it doesn't get garbage collected.
		// We must protect it until the new data has been delivered.
		List<PointEntry> oldPoints = mPoints;
		mPoints = points;

		if (isStarted()) {
			super.deliverResult(points);
		}

		// Invalidate the old data as we don't need it any more.
		if (oldPoints != null && oldPoints != points) {
			releaseResources(oldPoints);
		}
	}

	@Override
	protected void onStartLoading() {

		if (mPoints != null) {
			deliverResult(mPoints);
		}

		// Register the observers that will notify the Loader when changes are
		// made.
		if (mSmapPointObserver == null) {
			mSmapPointObserver = new SmapPointObserver(this);
		}

		if (takeContentChanged()) {
			forceLoad();
		} else if (mPoints == null) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {

		cancelLoad();

	}

	@Override
	protected void onReset() {

		onStopLoading();

		// At this point we can release the resources associated with 'tasks'.
		if (mPoints != null) {
			releaseResources(mPoints);
			mPoints = null;
		}

		// The Loader is being reset, so we should stop monitoring for changes.
		if (mSmapPointObserver != null) {
			try {
				getContext().unregisterReceiver(mSmapPointObserver);
			} catch (Exception e) {
				
			}
			mSmapPointObserver = null;
		}

	}

	@Override
	public void onCanceled(List<PointEntry> points) {
	
		super.onCanceled(points);
		releaseResources(points);
	}

	@Override
	public void forceLoad() {
		Log.i("SmapPointLoader", "+++++++ forceLoad");
		super.forceLoad();
	}
	
	@Override
	protected void onForceLoad() {
		Log.i("SmapPointLoader", "+++++++ onForceLoad");
		super.onForceLoad();
		
	}

	/**
	 * Helper method to take care of releasing resources associated with an
	 * actively loaded data set.
	 */
	private void releaseResources(List<PointEntry> points) {

	}

}