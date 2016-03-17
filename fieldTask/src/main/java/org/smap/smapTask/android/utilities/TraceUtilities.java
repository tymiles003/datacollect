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

package org.smap.smapTask.android.utilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;

import org.smap.smapTask.android.loaders.PointEntry;
import org.smap.smapTask.android.provider.TraceProviderAPI.TraceColumns;

import org.odk.collect.android.application.Collect;

import java.util.ArrayList;

public class TraceUtilities {


    public static void insertPoint(Location location) {

        Uri dbUri =  TraceColumns.CONTENT_URI;


        ContentValues values = new ContentValues();
        values.put(TraceColumns.LAT, location.getLatitude());
        values.put(TraceColumns.LON, location.getLongitude());
        values.put(TraceColumns.TIME, Long.valueOf(System.currentTimeMillis()));
        values.put(TraceColumns.SOURCE, Utilities.getSource());

        Collect.getInstance().getContentResolver().insert(dbUri, values);

    }

    /*
     * Get the trail of points
     */
    public static void getPoints(ArrayList<PointEntry> entries) {

        String [] proj = {
                TraceColumns._ID,
                TraceColumns.LAT,
                TraceColumns.LON,
                TraceColumns.TIME,
        };

        String [] selectArgs = {""};
        selectArgs[0] = Utilities.getSource();
        String selectClause = TraceColumns.SOURCE + " = ?";

        String sortOrder = TraceColumns._ID + " ASC; ";

        final ContentResolver resolver = Collect.getInstance().getContentResolver();
        Cursor pointListCursor = resolver.query(TraceColumns.CONTENT_URI, proj, selectClause, selectArgs, sortOrder);


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

    public static void deleteSource() {

        Uri dbUri =  TraceColumns.CONTENT_URI;

        String [] selectArgs = {""};
        selectArgs[0] = Utilities.getSource();
        String selectClause = TraceColumns.SOURCE + " = ?";

        Collect.getInstance().getContentResolver().delete(dbUri, selectClause, selectArgs);

    }

}
