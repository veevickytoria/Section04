/*******************************************************************************
 * Copyright (C) 2014 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.meetingninja.csse.notes;

import java.util.ArrayList;
import java.util.List;

import objects.Version;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;

public class VersionControlActivity extends Activity {
	private SessionManager session;
	private List<Version> versions;
	private VersionItemAdapter versionAdpt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version_control);

		getVersions("name");

		ListView lv = (ListView) findViewById(R.id.versionControlList);

		versionAdpt = new VersionItemAdapter(this,
				R.layout.version_control_item, versions);

		lv.setAdapter(versionAdpt);

		registerForContextMenu(lv);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_version_control, menu);
		return true;
	}

	private void getVersions(String noteName) {
		// TODO: make this a back end call
		versions = new ArrayList<Version>();
		Version v1 = new Version("10/1/13   4:45", "Matthew"), v2 = new Version(
				"10/1/13   5:55", "William"), v3 = new Version(
				"10/3/13   12:10", "Cricket"), v4 = new Version(
				"10/4/13   5:00", "Kevin");
		versions.add(v1);
		versions.add(v2);
		versions.add(v3);
		versions.add(v4);

	}

}

class VersionItemAdapter extends ArrayAdapter<Version> {
	private List<Version> versions;

	public VersionItemAdapter(Context context, int textViewResourceID,
			List<Version> versions) {
		super(context, textViewResourceID, versions);
		this.versions = versions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.version_control_item, null);
		}

		Version version = versions.get(position);
		if (version != null) {
			TextView versionDate = (TextView) v.findViewById(R.id.versionDate);
			TextView versionEditor = (TextView) v
					.findViewById(R.id.versionEditor);

			if (versionDate != null) {
				versionDate.setText(version.getDate());
			}
			if (versionEditor != null) {
				versionEditor.setText(version.getEditor());
			}
		}

		return v;

	}

}
