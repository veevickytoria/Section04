package com.meetingninja.csse.agenda;

import java.io.IOException;

import objects.Agenda;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AgendaDatabaseAdapter;
import com.meetingninja.csse.extras.JsonUtils;

public class AgendaSaveTask extends AsyncTask<Agenda, Void, Agenda> {

	private static final String TAG = AgendaSaveTask.class.getSimpleName();

	@Override
	protected Agenda doInBackground(Agenda... params) {
		Agenda create = params[0];
		try {
			System.out.println(JsonUtils.getObjectMapper().writeValueAsString(
					create));
			create = AgendaDatabaseAdapter.createAgenda(create);
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
		return create;
	}

	@Override
	protected void onPostExecute(Agenda result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result != null)
			Log.v(TAG, result.getID());
	}

}