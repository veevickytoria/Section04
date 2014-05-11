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
package com.meetingninja.csse.user;

import java.util.Map;

import objects.User;
import objects.parcelable.ParcelDataFactory;
import objects.parcelable.UserParcel;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.loopj.android.image.SmartImageView;
import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.meetingninja.csse.extras.JsonUtils;

public class ProfileFragment extends Fragment {

	private static final String TAG = ProfileFragment.class.getSimpleName();

	private TextView mTitleCompany, mName, mPhone, mEmail, mLocation;
	private View pageView, informationView, emptyView;
	private SmartImageView mUserImage;

	private SessionManager session;
	private User displayedUser;
	private int menu;

	private ImageButton smsButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		pageView = inflater
				.inflate(R.layout.fragment_profile, container, false);
		setupViews(pageView);
		session = SessionManager.getInstance();

		Bundle extras = getArguments();
		displayedUser = new User();

		if (extras != null && extras.containsKey(Keys.User.PARCEL)) {
			displayedUser = new ParcelDataFactory(extras).getUser();
			try {
				Log.d(TAG, JsonUtils.getObjectMapper().writeValueAsString(displayedUser));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} else {
			Log.v(TAG, "Displaying Current User");
			displayedUser.setID(SessionManager.getUserID());
		}
		if (extras != null && extras.containsKey("notMine")) {
			menu = R.menu.menu_profile;
		} else {
			menu = R.menu.menu_view_profile;
		}
		setHasOptionsMenu(true);
		fetchUserInfo(displayedUser.getID());

		return pageView;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(this.menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit_item_profile:
			Intent i = new Intent(getActivity(), EditProfileActivity.class);
			i.putExtra(Keys.User.PARCEL, new UserParcel(displayedUser));
			startActivityForResult(i, 7);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 7) {
				displayedUser = new ParcelDataFactory(data.getExtras())
						.getUser();
				setUser(displayedUser);
			}
		}
	}

	private void setupViews(View v) {
		informationView = v.findViewById(R.id.profile_container);
		emptyView = v.findViewById(android.R.id.empty);

		mUserImage = (SmartImageView) v.findViewById(R.id.view_prof_pic);

		mName = (TextView) v.findViewById(R.id.profile_name);
		mTitleCompany = (TextView) v.findViewById(R.id.profile_title_company);
		mTitleCompany.setVisibility(View.GONE);
		mLocation = (TextView) v.findViewById(R.id.profile_location);
		mLocation.setVisibility(View.GONE);

		mEmail = (TextView) v.findViewById(R.id.profile_email);
		mEmail.setOnClickListener(new ContactListener());

		mPhone = (TextView) v.findViewById(R.id.profile_phone);
		v.findViewById(R.id.profile_phone_row).setVisibility(View.GONE);
		mPhone.setOnClickListener(new ContactListener());

		smsButton = (ImageButton) v.findViewById(R.id.profile_chat_button);
		smsButton.setOnClickListener(new ContactListener());

	}

	private void fetchUserInfo(final String userID) {
		displayedUser = new User();
		// Local user is stored in SessionManager, so do not fetch
		if (TextUtils.equals(userID, SessionManager.getUserID())) {
			displayedUser.setID(SessionManager.getUserID());
			Map<String, String> details = session.getUserDetails();
			displayedUser.setDisplayName(details.get(SessionManager.USER));
			displayedUser.setCompany(details.get(SessionManager.COMPANY));
			displayedUser.setTitle(details.get(SessionManager.TITLE));
			displayedUser.setLocation(details.get(SessionManager.LOCATION));
			displayedUser.setEmail(details.get(SessionManager.EMAIL));
			displayedUser.setPhone(details.get(SessionManager.PHONE));
			setUser(displayedUser);
			return;
		}

		// Swap visibility while loading information
		emptyView.setVisibility(View.VISIBLE);
		informationView.setVisibility(View.GONE);

		UserVolleyAdapter.fetchUserInfo(userID, new AsyncResponse<User>() {
			@Override
			public void processFinish(User result) {
				if (ProfileFragment.this.isAdded())
					ProfileFragment.this.setUser(result);

			}
		});
	}

	private class ContactListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.profile_email:
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
						Uri.fromParts("mailto", mEmail.getText().toString(),
								null));
				startActivity(Intent
						.createChooser(emailIntent, "Send email..."));
				break;
			case R.id.profile_phone:

				Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
				dialerIntent.setData(Uri.parse("tel:"
						+ mPhone.getText().toString()));
				try {
					startActivity(dialerIntent);
				} catch (Exception e) {
					Log.e(TAG, e.getLocalizedMessage());
				}
				break;

			case R.id.profile_chat_button:
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.setType("vnd.android-dir/mms-sms");
				smsIntent.putExtra("address", mPhone.getText().toString());

				try {
					startActivity(smsIntent);
				} catch (Exception e) {
					Log.e(TAG, e.getLocalizedMessage());
				}
				break;
			default:
				break;
			}
		}

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

		// Swap visibility after loading information
		emptyView.setVisibility(View.GONE);
		informationView.setVisibility(View.VISIBLE);
	}

}
