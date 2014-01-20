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
package com.android.meetingninja.notes;

import java.util.List;

import objects.Note;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.meetingninja.R;

/**
 * A class to display the Notes in a specific format for the items of the list.
 * This class uses the note_item XML file.
 * 
 * @author moorejm
 * 
 */
public class NoteItemAdapter extends ArrayAdapter<Note> {
	// declaring our ArrayList of items
	private final List<Note> notes;
	private final Context context;

	/*
	 * Override the constructor to initialize the list to display
	 */
	public NoteItemAdapter(Context context, int textViewResourceId,
			List<Note> notes) {
		super(context, textViewResourceId, notes);
		this.notes = notes;
		this.context = context;
	}

	// class for caching the views in a row
	private class ViewHolder {
		TextView title, content;
	}

	ViewHolder viewHolder;

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.list_item_note, parent, false);
			viewHolder = new ViewHolder();

			viewHolder.title = (TextView) rowView.findViewById(R.id.noteName);
			viewHolder.content = (TextView) rowView
					.findViewById(R.id.noteContent);

			rowView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) rowView.getTag();

		// Setup from the note_item XML file
		Note note = notes.get(position);

		viewHolder.title.setText(note.getTitle());
		String content = note.getContent();
		int max_length = 200;
		if (content.length() > max_length)
			viewHolder.content
					.setText(content.substring(0, max_length) + "...");
		else
			viewHolder.content.setText(content);

		return rowView;
	}
}
