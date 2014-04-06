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
package com.meetingninja.csse;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class NavDrawerItem {

	private String title;
	private int icon;
	private String count = "0";
	// boolean to set visiblity of the counter
	private boolean isCounterVisible = false;
	private Fragment page;

	public NavDrawerItem() {
	}

	public NavDrawerItem(String title, int icon) {
		this.title = title;
		this.icon = icon;
	}

	public NavDrawerItem(String title, int icon, Fragment page) {
		this(title, icon);
		this.page = page;
	}

	public NavDrawerItem(String title, int icon, boolean isCounterVisible,
			String count) {
		this(title, icon);
		this.isCounterVisible = isCounterVisible;
		this.count = count;
	}

	public NavDrawerItem(String title, int icon, boolean isCounterVisible,
			String count, Fragment page) {
		this(title, icon, isCounterVisible, count);
		this.page = page;
	}

	public String getTitle() {
		return this.title;
	}

	public int getIcon() {
		return this.icon;
	}

	public String getCount() {
		return this.count;
	}

	public boolean getCounterVisibility() {
		return this.isCounterVisible;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public void setCounterVisibility(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}

	public boolean select(FragmentManager fragManager) {
		if (page == null)
			return false;
		else {
			fragManager.beginTransaction().replace(R.id.content_frame, page)
					.commit();
			return true;
		}
	}
}
