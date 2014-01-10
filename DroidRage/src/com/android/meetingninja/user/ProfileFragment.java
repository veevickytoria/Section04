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
package com.android.meetingninja.user;

import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import objects.User;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.meetingninja.R;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.meetings.MeetingItemAdapter;
import com.loopj.android.image.SmartImageView;

public class ProfileFragment extends Fragment {

	private static final String TAG = ProfileFragment.class.getSimpleName();

	private ListView meetingList;
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private MeetingItemAdapter adpt;

	private SessionManager session;
	private TextView mTitleCompany, mName, mPhone, mEmail, mLocation;
	private SmartImageView mUserImage;
	private User displayedUser;

	private RetUserObj infoFetcher = null;
	private View pageView;

	// private GetProfBitmap bitmapFetcher = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		pageView = inflater
				.inflate(R.layout.fragment_profile, container, false);
		setupViews(pageView);
		session = SessionManager.getInstance();

		// Peter Pan URL
		// mUserImage
		// .setImageUrl("http://www.tdnforums.com/uploads/profile/photo-27119.jpg?_r=0");

		infoFetcher = new RetUserObj();
		infoFetcher.execute(session.getUserID());

		/*
		 * meetingList = (ListView) pageView
		 * .findViewById(R.id.profile_meetingList); adpt = new
		 * MeetingItemAdapter(getActivity(), R.layout.list_item_meeting,
		 * meetings); meetingList.setAdapter(adpt);
		 * 
		 * MeetingFetcherTask fetcher = new MeetingFetcherTask(this);
		 * fetcher.execute(session.getUserID());
		 */

		return pageView;

	}

	private void setupViews(View v) {
		mUserImage = (SmartImageView) v.findViewById(R.id.view_prof_pic);

		mName = (TextView) v.findViewById(R.id.profile_name);
		mTitleCompany = (TextView) v.findViewById(R.id.profile_title_company);
		mTitleCompany.setVisibility(View.GONE);
		mLocation = (TextView) v.findViewById(R.id.profile_location);
		mLocation.setVisibility(View.GONE);

		mEmail = (TextView) v.findViewById(R.id.profile_email);

		mPhone = (TextView) v.findViewById(R.id.profile_phone);
		v.findViewById(R.id.profile_phone_row).setVisibility(View.GONE);

	}

	private void setUser(User user) {
		this.displayedUser = user;
		if (user != null) {
			// set display name
			mName.setText(user.getDisplayName());

			// set email
			mEmail.setText(user.getEmail());

			// set title & company
			StringBuilder sb = new StringBuilder();
			if (!(user.getTitle().isEmpty() || user.getCompany().isEmpty())) {
				if (!user.getTitle().isEmpty())
					sb.append(user.getTitle());
				if (!user.getTitle().isEmpty())
					sb.append(", " + user.getCompany());
				mTitleCompany.setText(sb);
				mTitleCompany.setVisibility(View.VISIBLE);
			} else {
				mTitleCompany.setVisibility(View.GONE);
			}

			// set location
			if (!user.getLocation().isEmpty()) {
				mLocation.setText(user.getLocation());
				mLocation.setVisibility(View.VISIBLE);
			} else {
				mLocation.setVisibility(View.GONE);
			}

			// set phone
			if (!user.getPhone().isEmpty()) {
				mPhone.setText(user.getPhone());
				pageView.findViewById(R.id.profile_phone_row).setVisibility(
						View.VISIBLE);
			} else {
				pageView.findViewById(R.id.profile_phone_row).setVisibility(
						View.GONE);
			}
		} else {
			mTitleCompany.setVisibility(View.GONE);
			mLocation.setVisibility(View.GONE);
			pageView.findViewById(R.id.profile_phone_row).setVisibility(
					View.GONE);

		}
	}

	final class RetUserObj implements AsyncResponse<User> {

		private UserInfoFetcher infoFetcher;

		public RetUserObj() {
			infoFetcher = new UserInfoFetcher(this);
		}

		public void execute(String userID) {
			infoFetcher.execute(userID);
		}

		@Override
		public void processFinish(User result) {
			if (isAdded())
				setUser(result);
		}
	}

}
