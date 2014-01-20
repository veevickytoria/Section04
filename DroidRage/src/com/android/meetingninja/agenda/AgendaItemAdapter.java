package com.android.meetingninja.agenda;

import com.android.meetingninja.R;
import com.doomonafireball.betterpickers.hmspicker.HmsPickerBuilder;
import com.doomonafireball.betterpickers.hmspicker.HmsPickerDialogFragment;

import objects.Agenda;
import objects.Topic;
import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This is a very simple adapter that provides very basic tree view with a
 * checkboxes and simple item description.
 * 
 */
public class AgendaItemAdapter extends AbstractTreeViewAdapter<Topic> {

	private Agenda mAgenda;
	private Context mContext;

	// private final OnCheckedChangeListener onCheckedChange = new
	// OnCheckedChangeListener() {
	// @Override
	// public void onCheckedChanged(final CompoundButton buttonView,
	// final boolean isChecked) {
	// final Long id = (Long) buttonView.getTag();
	// changeSelected(isChecked, id);
	// }
	//
	// };

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	private final OnClickListener onTimeBtnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			HmsPickerBuilder hms = new HmsPickerBuilder().setFragmentManager(
					((FragmentActivity) getActivity())
							.getSupportFragmentManager()).setStyleResId(
					R.style.BetterPickersDialogFragment);
			hms.show();

		}
	};

	// private void changeSelected(final boolean isChecked, final Long id) {
	// if (isChecked) {
	// selected.add(id);
	// } else {
	// selected.remove(id);
	// }
	// }

	public AgendaItemAdapter(final Context context, final Agenda agenda,
			final TreeStateManager<Topic> treeStateManager,
			final int numberOfLevels) {
		super((Activity) context, treeStateManager, numberOfLevels);
		this.mAgenda = agenda;
		this.mContext = context;
	}

	// private String getDescription(final long id) {
	// final Integer[] hierarchy = getManager().getHierarchyDescription(id);
	// return "Node " + id + Arrays.asList(hierarchy);
	// return "Topic " + id;
	// }

	private String getDescription(final Topic topic) {
		StringBuilder sb = new StringBuilder(topic.getTitle());
		int mins = Integer.valueOf(topic.getTime()); // in minutes
		int hrs = 0; // get hours
		hrs = mins % 60;
		mins -= hrs*60;
		if (mins >= 0) {
			sb.append(String.format(" (%dh, %2dm)", hrs, mins));
		}
		
		return sb.toString();
	}
	
	// class for caching the views in a row
	private class ViewHolder {
		TextView title;
		EditText editor;
		Button addTopicBtn, setTimeBtn;
	}

	ViewHolder viewHolder;

	@Override
	public View getNewChildView(final TreeNodeInfo<Topic> treeNodeInfo) {
		final LinearLayout viewLayout = (LinearLayout) getActivity()
				.getLayoutInflater().inflate(R.layout.list_item_agenda, null);
		return updateView(viewLayout, treeNodeInfo);
	}

	@Override
	public LinearLayout updateView(final View view,
			final TreeNodeInfo<Topic> treeNodeInfo) {
		final LinearLayout rowView = (LinearLayout) view;

		final TextView topicTitleView = (TextView) rowView
				.findViewById(R.id.agenda_topic_title);
		final EditText topicTitleEditor = (EditText) rowView
				.findViewById(R.id.agenda_edit_topic);
//		topicTitleEditor.setVisibility(View.GONE);
		// final TextView levelView = (TextView) rowView
		// .findViewById(R.id.demo_list_item_level);
		topicTitleView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (topicTitleView.isShown()) {
					// topicTitleView.setVisibility(View.GONE);
					topicTitleEditor.setVisibility(View.VISIBLE);
				}

			}
		});

		topicTitleView.setText(getDescription(treeNodeInfo.getId()));
		// levelView.setText(Integer.toString(treeNodeInfo.getLevel()));
		final Button timeBtn = (Button) rowView
				.findViewById(R.id.agenda_topicTimeBtn);
		timeBtn.setTag(treeNodeInfo.getId());
		timeBtn.setOnClickListener(onTimeBtnClick);

		if (treeNodeInfo.isWithChildren()) {
			timeBtn.setVisibility(View.GONE);
		} else {
			timeBtn.setVisibility(View.VISIBLE);
			// timeBtn.setChecked(selected.contains(treeNodeInfo.getId()));
		}
		// timeBtn.setOnCheckedChangeListener(onCheckedChange);
		return rowView;
	}

	// @Override
	// public void handleItemClick(final View view, final Object id) {
	// final Long longId = (Long) id;
	// final TreeNodeInfo<Topic> info = getManager().getNodeInfo(longId);
	// if (info.isWithChildren()) {
	// super.handleItemClick(view, id);
	// } else {
	// final ViewGroup vg = (ViewGroup) view;
	// final CheckBox cb = (CheckBox) vg
	// .findViewById(R.id.demo_list_checkbox);
	// cb.performClick();
	// }
	// }

	@Override
	public long getItemId(final int position) {
		return (long) position;
	}

	private class TimeClickListener implements OnClickListener,
			HmsPickerDialogFragment.HmsPickerDialogHandler {
		Button button;
		

		public TimeClickListener(Button b) {
			this.button = b;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDialogHmsSet(int reference, int hours, int minutes,
				int seconds) {
			if (seconds >= 30)
				minutes++;
			String time = String.format("%dh:%dm", hours, minutes);
			
		}
	}
}