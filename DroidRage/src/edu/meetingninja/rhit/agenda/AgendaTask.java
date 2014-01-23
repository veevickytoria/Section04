package edu.meetingninja.rhit.agenda;
import java.io.IOException;

import objects.Agenda;
import android.os.AsyncTask;
import android.util.Log;
import edu.meetingninja.rhit.ApplicationController;
import edu.meetingninja.rhit.database.AgendaDatabaseAdapter;

public class AgendaTask extends AsyncTask<Agenda, Void, Agenda> {

	private static final String TAG = AgendaTask.class.getSimpleName();

	@Override
	protected Agenda doInBackground(Agenda... params) {
		Agenda create = params[0];
		try {
			System.out.println(ApplicationController.getInstance()
					.getObjectMapper().writeValueAsString(create));
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