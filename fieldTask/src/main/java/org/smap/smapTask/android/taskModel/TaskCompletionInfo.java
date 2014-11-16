package org.smap.smapTask.android.taskModel;

import java.util.Date;

/**
 * Created by neilpenman on 16/11/2014.
 */
public class TaskCompletionInfo {
    public double lat;
    public double lon;
    public long actFinish;	// When the task was finished
    public String ident;	// Survey ident
    public String uuid;		// Unique identifier for the results
}
