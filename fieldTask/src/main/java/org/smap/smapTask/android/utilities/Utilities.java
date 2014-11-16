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

import java.text.DateFormat;
import java.util.ArrayList;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.provider.InstanceProviderAPI.InstanceColumns;
import org.odk.collect.android.utilities.STFileUtils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import loaders.TaskEntry;

public class Utilities {

    // Valid values for task status
    public static final String STATUS_T_ACCEPTED = "accepted";
    public static final String STATUS_T_REJECTED = "rejected";
    public static final String STATUS_T_COMPLETE = "complete";
    public static final String STATUS_T_SUBMITTED = "submitted";
    public static final String STATUS_T_CANCELLED = "cancelled";
    public static final String STATUS_T_CLOSED = "closed";

    // Valid values for is synced
    public static final String STATUS_SYNC_YES = "synchronized";
    public static final String STATUS_SYNC_NO = "not synchronized";
	
	// Get the task source
	public static String getSource() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(Collect.getInstance()
						.getBaseContext());
		String serverUrl = settings.getString(
				PreferencesActivity.KEY_SERVER_URL, null);
		String source = STFileUtils.getSource(serverUrl);
		
		

		return source;
	}

    public static TaskEntry getTaskForTaskId(long id) {

        TaskEntry entry = null;

        // Get cursor
        String [] proj = {
                InstanceColumns._ID,
                InstanceColumns.T_TITLE,
                InstanceColumns.T_TASK_STATUS,
                InstanceColumns.T_SCHED_START,
                InstanceColumns.T_ADDRESS,
                InstanceColumns.FORM_PATH,
                InstanceColumns.INSTANCE_FILE_PATH,
                InstanceColumns.SCHED_LON,
                InstanceColumns.SCHED_LAT,
                InstanceColumns.ACT_LON,
                InstanceColumns.ACT_LAT,
                InstanceColumns.T_ACT_FINISH,
                InstanceColumns.T_IS_SYNC,
                InstanceColumns.T_TASK_ID,
                InstanceColumns.UUID

        };

        String selectClause = InstanceColumns._ID + " = " + id;


        final ContentResolver resolver = Collect.getInstance().getContentResolver();
        Cursor c = resolver.query(InstanceColumns.CONTENT_URI, proj, selectClause, null, null);

        try {
            c.moveToFirst();
            DateFormat dFormat = DateFormat.getDateTimeInstance();


            entry = new TaskEntry();

            entry.type = "task";
            entry.name = c.getString(c.getColumnIndex(InstanceColumns.T_TITLE));
            entry.taskStatus = c.getString(c.getColumnIndex(InstanceColumns.T_TASK_STATUS));
            entry.taskStart = c.getLong(c.getColumnIndex(InstanceColumns.T_SCHED_START));
            entry.taskAddress = c.getString(c.getColumnIndex(InstanceColumns.T_ADDRESS));
            entry.taskForm = c.getString(c.getColumnIndex(InstanceColumns.FORM_PATH));
            entry.instancePath = c.getString(c.getColumnIndex(InstanceColumns.INSTANCE_FILE_PATH));
            entry.id = c.getLong(c.getColumnIndex(InstanceColumns._ID));
            entry.schedLon = c.getDouble(c.getColumnIndex(InstanceColumns.SCHED_LON));
            entry.schedLat = c.getDouble(c.getColumnIndex(InstanceColumns.SCHED_LAT));
            entry.actLon = c.getDouble(c.getColumnIndex(InstanceColumns.ACT_LON));
            entry.actLat = c.getDouble(c.getColumnIndex(InstanceColumns.ACT_LAT));
            entry.actFinish = c.getLong(c.getColumnIndex(InstanceColumns.T_ACT_FINISH));
            entry.isSynced = c.getString(c.getColumnIndex(InstanceColumns.T_IS_SYNC));
            entry.taskId = c.getLong(c.getColumnIndex(InstanceColumns.T_TASK_ID));
            entry.uuid = c.getString(c.getColumnIndex(InstanceColumns.UUID));

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                }
            }
        }

        return entry;
    }

    public static void getTasks(ArrayList<TaskEntry> tasks, boolean all_non_synchronised) {

        // Get cursor
        String [] proj = {
                InstanceColumns._ID,
                InstanceColumns.T_TITLE,
                InstanceColumns.T_TASK_STATUS,
                InstanceColumns.T_SCHED_START,
                InstanceColumns.T_ADDRESS,
                InstanceColumns.FORM_PATH,
                InstanceColumns.INSTANCE_FILE_PATH,
                InstanceColumns.SCHED_LON,
                InstanceColumns.SCHED_LAT,
                InstanceColumns.ACT_LON,
                InstanceColumns.ACT_LAT,
                InstanceColumns.T_ACT_FINISH,
                InstanceColumns.T_IS_SYNC,
                InstanceColumns.T_TASK_ID,
                InstanceColumns.UUID

        };

        String selectClause = null;
        if(all_non_synchronised) {
            selectClause = "(" + InstanceColumns.SOURCE + " = ?" +
                    " or " + InstanceColumns.SOURCE + " = 'local')" +
                    " and " + InstanceColumns.T_IS_SYNC + " = ? ";
        } else {
            selectClause = "(" + InstanceColumns.SOURCE + " = ?" +
                    " or " + InstanceColumns.SOURCE + " = 'local')" +
                    " and " + InstanceColumns.T_TASK_STATUS + " != ? ";
        }

        String [] selectArgs = {"",""};
        selectArgs[0] = Utilities.getSource();
        if(all_non_synchronised) {
            selectArgs[1] = Utilities.STATUS_SYNC_NO;
        } else {
            selectArgs[1] = Utilities.STATUS_T_CLOSED;
        }

        String sortOrder = InstanceColumns.T_SCHED_START + " DESC";

        Cursor c = Collect.getInstance().getContentResolver().query(InstanceColumns.CONTENT_URI, proj, selectClause, selectArgs, sortOrder);

        try {
            c.moveToFirst();
            while (!c.isAfterLast()) {

                TaskEntry entry = new TaskEntry();

                entry.type = "task";
                entry.name = c.getString(c.getColumnIndex(InstanceColumns.T_TITLE));
                entry.taskStatus = c.getString(c.getColumnIndex(InstanceColumns.T_TASK_STATUS));
                entry.taskStart = c.getLong(c.getColumnIndex(InstanceColumns.T_SCHED_START));
                entry.taskAddress = c.getString(c.getColumnIndex(InstanceColumns.T_ADDRESS));
                entry.taskForm = c.getString(c.getColumnIndex(InstanceColumns.FORM_PATH));
                entry.instancePath = c.getString(c.getColumnIndex(InstanceColumns.INSTANCE_FILE_PATH));
                entry.id = c.getLong(c.getColumnIndex(InstanceColumns._ID));
                entry.schedLon = c.getDouble(c.getColumnIndex(InstanceColumns.SCHED_LON));
                entry.schedLat = c.getDouble(c.getColumnIndex(InstanceColumns.SCHED_LAT));
                entry.actLon = c.getDouble(c.getColumnIndex(InstanceColumns.ACT_LON));
                entry.actLat = c.getDouble(c.getColumnIndex(InstanceColumns.ACT_LAT));
                entry.actFinish = c.getLong(c.getColumnIndex(InstanceColumns.T_ACT_FINISH));
                entry.isSynced = c.getString(c.getColumnIndex(InstanceColumns.T_IS_SYNC));
                entry.taskId = c.getLong(c.getColumnIndex(InstanceColumns.T_TASK_ID));
                entry.uuid = c.getString(c.getColumnIndex(InstanceColumns.UUID));

                tasks.add(entry);
                c.moveToNext();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                }
            }
        }

    }

    /*
     * Delete the task
     */
    public static void deleteTask(Long id) {

        Uri taskUri =  Uri.withAppendedPath(InstanceColumns.CONTENT_URI, id.toString());
        final ContentResolver cr = Collect.getInstance().getContentResolver();
        cr.delete(taskUri, null, null);
    }

    /*
     * Delete any tasks with the matching status
     * Only delete if the task status has been successfully synchronised with the server
     */
    public static int deleteTasksWithStatus(String status) {

        Uri dbUri =  InstanceColumns.CONTENT_URI;

        String selectClause = InstanceColumns.T_TASK_STATUS + " = ? and "
                + InstanceColumns.SOURCE + " = ? and "
                + InstanceColumns.T_IS_SYNC + " = ?";

        String [] selectArgs = {"","",""};
        selectArgs[0] = status;
        selectArgs[1] = Utilities.getSource();
        selectArgs[2] = Utilities.STATUS_SYNC_YES;

        return Collect.getInstance().getContentResolver().delete(dbUri, selectClause, selectArgs);
    }

    /*
 * Delete the task
 */
    public static void closeTasksWithStatus(String status) {

        Uri dbUri =  InstanceColumns.CONTENT_URI;

        ContentValues values = new ContentValues();
        values.put(InstanceColumns.T_TASK_STATUS, Utilities.STATUS_T_CLOSED);

        String selectClause = InstanceColumns.T_TASK_STATUS + " = ? and "
                + InstanceColumns.SOURCE + "= ? ";

        String [] selectArgs = {"",""};
        selectArgs[0] = status;
        selectArgs[1] = Utilities.getSource();

        Collect.getInstance().getContentResolver().update(dbUri, values, selectClause, selectArgs);

    }

    /*
     * Mark the task as synchronised
     */
    public static void setTaskSynchronized(Long id) {

        Uri taskUri =  Uri.withAppendedPath(InstanceColumns.CONTENT_URI, id.toString());

        ContentValues values = new ContentValues();
        values.put(InstanceColumns.T_IS_SYNC, STATUS_SYNC_YES);

        Collect.getInstance().getContentResolver().update(taskUri, values, null, null);

    }

    /*
     * Mark the task as synchronised
     */
    public static void setStatusForTask(Long id, String status) {

        Uri taskUri =  Uri.withAppendedPath(InstanceColumns.CONTENT_URI, id.toString());

        ContentValues values = new ContentValues();
        values.put(InstanceColumns.T_TASK_STATUS, status);

        Collect.getInstance().getContentResolver().update(taskUri, values, null, null);

    }

    /*
     * Set the task assignment
     */
    public static void setStatusForTask(long taskId, String status) {

        Uri dbUri =  InstanceColumns.CONTENT_URI;

        String selectClause = InstanceColumns.T_TASK_ID + " = " + taskId + " and "
                + InstanceColumns.SOURCE + " = ?";


        String [] selectArgs = {""};
        selectArgs[0] = Utilities.getSource();

        ContentValues values = new ContentValues();
        values.put(InstanceColumns.T_TASK_STATUS, status);

        Collect.getInstance().getContentResolver().update(dbUri, values, selectClause, selectArgs);

    }

    /*
     * Return true if the current task status allows it to be rejected
     */
    public static boolean canReject(String currentStatus) {
        boolean valid = false;
        if(currentStatus.equals(Utilities.STATUS_T_ACCEPTED)) {
            valid = true;
        }
        return valid;
    }

    /*
     * Return true if the current task status allows it to be completed
     */
    public static boolean canComplete(String currentStatus) {
        boolean valid = false;
        if(currentStatus.equals(STATUS_T_ACCEPTED)) {
            valid = true;
        }

        return valid;
    }

    /*
     * Return true if the current task status allows it to be completed
     */
    public static boolean canAccept(String currentStatus) {
        boolean valid = false;
        if(currentStatus.equals(STATUS_T_REJECTED)) {
            valid = true;
        }

        return valid;
    }
}
