package com.meetingninja.csse.agenda;

import java.io.IOException;

import objects.Agenda;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AgendaDatabaseAdapter;
import com.meetingninja.csse.database.AsyncResponse;

public class AgendaSaveTask extends AsyncTask<Agenda, Void, String> {

	private static final String TAG = AgendaSaveTask.class.getSimpleName();

	private AsyncResponse<String> delegate;

	public AgendaSaveTask(AsyncResponse<String> del) {
		this.delegate = del;
	}

	@Override
	protected String doInBackground(Agenda... params) {
		Agenda create = params[0];
		String id = "";
		try {
//			System.out.println(JsonUtils.getObjectMapper().writeValueAsString(
//					create));
//			create =
			id = AgendaDatabaseAdapter.createAgenda(create);
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
		return id;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result != null)
			Log.v(TAG + " Agenda ID", result);
		delegate.processFinish(result);
	}

}