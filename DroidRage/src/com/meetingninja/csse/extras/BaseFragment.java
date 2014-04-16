package com.meetingninja.csse.extras;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * This Fragment manages a single background task and retains itself across
 * configuration changes.
 */
public class BaseFragment extends Fragment {
	private static final String TAG = BaseFragment.class.getSimpleName();

	/**
	 * Callback interface through which the fragment can report the task's
	 * progress and results back to the Activity.
	 */
	public static interface TaskCallbacks {
		public void onPreExecute();

		public void onProgressUpdate(int percent);

		public void onCancelled();

		public void onPostExecute();
	}

	/**
	 * Android passes us a reference to the newly created Activity by calling
	 * this method after each configuration change.
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.i(TAG, "onAttach(Activity)");
		super.onAttach(activity);
		if (!(activity instanceof TaskCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement the TaskCallbacks interface.");
		}
	}

	/**
	 * This method is called only once when the Fragment is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate(Bundle)");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	/**
	 * This method is <em>not</em> called when the Fragment is being retained
	 * across Activity instances.
	 */
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	/************************/
	/***** LOGS & STUFF *****/
	/************************/

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated(Bundle)");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.i(TAG, "onStart()");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume()");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.i(TAG, "onPause()");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.i(TAG, "onStop()");
		super.onStop();
	}
}