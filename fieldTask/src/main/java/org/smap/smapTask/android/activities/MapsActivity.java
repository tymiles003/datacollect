/*
 * Copyright (C) 2011 Cloudtec Pty Ltd
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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;


import org.smap.smapTask.android.fragments.MapFragment;
import org.smap.smapTask.android.R;

/**
 * Responsible for displaying maps of tasks.
 * 
 * @author Neil Penman 
 */
public class MapsActivity extends FragmentActivity  {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Insert the fragment by replacing any existing fragment
        Fragment fragment = new MapFragment();
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.map_content_frame) == null) {
            MapFragment map = new MapFragment();
            fm.beginTransaction().add(android.R.id.content, map).commit();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
	}

	
}