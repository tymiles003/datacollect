/*
 * Copyright (C) 2011 Smap Consulting Pty Ltd
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
 * Responsible for displaying tasks in a list view
 * 
 * @author Neil Penman (neilpenman@gmail.com)
 */
package org.smap.smapTask.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.smap.smapTask.android.R;
import org.smap.smapTask.android.utilities.KeyValueJsonFns;

import java.util.List;

import loaders.TaskEntry;

public class TaskListArrayAdapter extends ArrayAdapter<TaskEntry> {
    
    private int mLayout;
    LayoutInflater mInflater;
	
    public TaskListArrayAdapter(Context context) {
		super(context, R.layout.main_list);
		mLayout = R.layout.task_row;
		mInflater = LayoutInflater.from(context);	
	}
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	View view;
    	
    	if (convertView == null) {
    		view = mInflater.inflate(mLayout, parent, false);
    	} else {
    		view = convertView;
    	}
    
    	TaskEntry item = getItem(position);

    	ImageView icon = (ImageView) view.findViewById(R.id.icon);
    	if(item.type.equals("form")) {
    		icon.setImageResource(R.drawable.form);
    	} else if (item.taskStatus != null) {
    		if(item.taskStatus.equals("accepted")) {
    			icon.setImageResource(R.drawable.task_open);
    		} else if(item.taskStatus.equals("done")) {
    			icon.setImageResource(R.drawable.task_done);
    		} else if(item.taskStatus.equals("rejected") || item.taskStatus.equals("missed") || item.taskStatus.equals("cancelled")) {
    			icon.setImageResource(R.drawable.task_reject);
    		} else if(item.taskStatus.equals("submitted")) {
    			icon.setImageResource(R.drawable.task_submitted);
    		}
    	}
    	
    	
    	TextView taskNameText = (TextView) view.findViewById(R.id.toptext);
    	if (taskNameText != null) {
    		taskNameText.setText(item.name);
    	}

    	TextView taskStartText = (TextView) view.findViewById(R.id.middletext);
    	if(taskStartText != null) {
	    	if(item.type.equals("form")) {
		    	taskStartText.setText(getContext().getString(R.string.version) + ": " + item.formVersion);
	    	} else {    	
		    	taskStartText.setText(item.taskStart);
	    	}
    	}
    	
    	TextView taskAddressText = (TextView) view.findViewById(R.id.bottomtext);
    	if (taskAddressText != null) {
    		if(item.type.equals("form")) {
    			taskAddressText.setText(getContext().getString(R.string.smap_project) + ": " + item.project);
    		} else {
    			taskAddressText.setText(KeyValueJsonFns.getValues(item.taskAddress));
    		}
    	}
    	 
    	return view;
    }
    
    public void setData(List<TaskEntry> data) {
        clear();
        if (data != null) {
          for (int i = 0; i < data.size(); i++) {
            add(data.get(i));
          }
        }
      }
    

}
