package com.meetingninja.csse.extras;

import com.meetingninja.csse.database.AsyncResponse;

import android.os.AsyncTask;

public abstract class DelegatedAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	protected AsyncResponse<Result> delegate;

	public DelegatedAsyncTask(AsyncResponse<Result> delegate) {
		this.delegate = delegate;
	}

	@Override
	abstract protected Result doInBackground(Params... params);

	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		delegate.processFinish(result);
	}

}
