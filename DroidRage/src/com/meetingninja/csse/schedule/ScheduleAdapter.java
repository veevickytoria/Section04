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
package com.meetingninja.csse.schedule;

import java.util.List;

import objects.Event;
import objects.Schedule;

import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foound.widget.AmazingAdapter;
import com.meetingninja.csse.R;
import com.meetingninja.csse.extras.NinjaDateUtils;

public class ScheduleAdapter extends AmazingAdapter {
	private List<Event> events;
	private final Context mContext;
	private AsyncTask<Integer, Void, Pair<Boolean, Schedule>> backgroundTask;
	private DateTimeFormatter timeFormat;
	private boolean is24;

	public ScheduleAdapter(Context context, Schedule schedule) {
		this.mContext = context;
		this.events = schedule.getEvents();
		is24 = android.text.format.DateFormat.is24HourFormat(context);
		timeFormat = is24 ? NinjaDateUtils.JODA_24_TIME_FORMAT
				: NinjaDateUtils.JODA_12_TIME_FORMAT;
	}

	public void reset() {
		if (backgroundTask != null)
			backgroundTask.cancel(false);

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Event getItem(int position) {
		if (position >= 0 && position < events.size())
			return events.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected void onNextPageRequested(int page) {
		Log.d(TAG, "Got onNextPageRequested page=" + page);

		if (backgroundTask != null) {
			backgroundTask.cancel(false);
		}

	}

	@Override
	protected void bindSectionHeader(View view, int position,
			boolean displaySectionHeader) {
		if (displaySectionHeader) {
			Log.d(TAG, "Binding at " + position);
			view.findViewById(R.id.schedule_header).setVisibility(View.VISIBLE);
			TextView lSectionTitle = (TextView) view
					.findViewById(R.id.schedule_header);
			String sectionTitle = getSections()[getSectionForPosition(position)];
			// DateTime dt = new DateTime();
			// dt = Long.parseLong(sectionTitle);
			// dt = MyDateUtils.JODA_SERVER_DATE_FORMAT
			// .parseDateTime(sectionTitle);
			lSectionTitle.setText(NinjaDateUtils.JODA_APP_DATE_FORMAT
					.print(Long.parseLong(sectionTitle)));
		} else {
			view.findViewById(R.id.schedule_header).setVisibility(View.GONE);
		}

	}

	@Override
	public View getAmazingView(int position, View convertView, ViewGroup parent) {
		View res = convertView;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (res == null)
			res = inflater.inflate(R.layout.list_item_schedule, null);

		TextView lTitle = (TextView) res.findViewById(R.id.block_title);
		TextView lSubtitle = (TextView) res.findViewById(R.id.block_subtitle);
		TextView lStartTime = (TextView) res.findViewById(R.id.block_time);
		TextView lEndTime = (TextView) res.findViewById(R.id.block_endtime);

		Event event = getItem(position);
		lTitle.setText(event.getTitle());
		lSubtitle.setText(event.getDescription());
		lStartTime.setText(convertTime(event.getStartTime()));
		lEndTime.setText(convertTime(event.getEndTime()));

		return res;
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		TextView lSectionHeader = (TextView) header;
		lSectionHeader.setText(getSections()[getSectionForPosition(position)]);
		lSectionHeader.setBackgroundColor(alpha << 24 | (0x333333));
		// lSectionHeader.setTextColor(alpha << 24 | (0x000000));
	}

	@Override
	public int getPositionForSection(int section) {
		// Log.w(TAG, "start");
		// Log.d(TAG, "Section : " + section);
		if (section <= 0)
			return 0;
		if (section >= events.size())
			return events.size() - 1;
		Event prev = null;
		int sectionIndex = -1;
		for (int i = 0; i < events.size(); i++) {
			if (prev == null
					|| !TextUtils.equals(events.get(i).getStartTime(),
							prev.getStartTime())) {
				sectionIndex++;
				prev = events.get(i);
				if (section == sectionIndex) {
					// Log.d(TAG, "Position : " + sectionIndex);
					return i;
				}
			}
		}
		return events.size() - 1;
	}

	@Override
	public int getSectionForPosition(int position) {
		getItem(position);
		getSections();
		// for (int i = 0; i < events.size(); i++) {
		// if (TextUtils.equals(convertDate(e.getStartTime()), dates[i])) {
		// return i;
		// }
		// }
		// return -1;
		Event prev = null;
		int sectionIndex = -1;
		for (int i = 0; i < events.size(); i++) {
			if (prev == null
					|| !TextUtils.equals(events.get(i).getStartTime(),
							prev.getStartTime())) {
				sectionIndex++;
				prev = events.get(i);
				if (i == sectionIndex) {
					// Log.d(TAG, "Position : " + sectionIndex);
					return i;
				}
			}
		}
		return events.size() - 1;
	}

	@Override
	public String[] getSections() {
		String[] dates = new String[events.size()];
		for (int i = 0; i < events.size(); i++) {
			dates[i] = convertDate(events.get(i).getStartTime());
		}
		return dates;
	}

	private String convertDate(String dateString) {
		// Date d = new Date();
		// try {
		// d = MyDateUtils.SERVER_DATE_FORMAT.parse(dateString);
		// } catch (ParseException e) {
		// Log.e(TAG, e.getLocalizedMessage());
		// try {
		// d = MyDateUtils.APP_DATE_FORMAT.parse(dateString);
		// } catch (ParseException e1) {
		// Log.e(TAG, e1.getLocalizedMessage());
		// }
		// }
		// return MyDateUtils.APP_DATE_FORMAT.format(d);
		return dateString;
	}

	private String convertTime(String timeString) {

		// Date d = new Date();
		// boolean is24 =
		// android.text.format.DateFormat.is24HourFormat(mContext);
		// try {
		// d = MyDateUtils.SERVER_DATE_FORMAT.parse(timeString);
		// } catch (ParseException e) {
		// Log.e(TAG, e.getLocalizedMessage());
		// }
		// return is24 ? MyDateUtils._24_TIME_FORMAT.format(d)
		// : MyDateUtils._12_TIME_FORMAT.format(d);
		return timeFormat.print(Long.parseLong(timeString));
	}
}
