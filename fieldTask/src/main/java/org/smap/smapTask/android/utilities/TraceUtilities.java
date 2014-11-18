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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import org.smap.smapTask.android.provider.TraceProviderAPI.TraceColumns;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.provider.InstanceProviderAPI.InstanceColumns;
import org.odk.collect.android.utilities.STFileUtils;

import java.text.DateFormat;
import java.util.ArrayList;

import loaders.TaskEntry;

public class TraceUtilities {


    public static void insertPoint(Location location) {

        Uri dbUri =  TraceColumns.CONTENT_URI;


        ContentValues values = new ContentValues();
        values.put(TraceColumns.LAT, location.getLatitude());
        values.put(TraceColumns.LON, location.getLongitude());
        values.put(TraceColumns.TIME, Long.valueOf(System.currentTimeMillis()));

        Collect.getInstance().getContentResolver().insert(dbUri, values);

    }



}
