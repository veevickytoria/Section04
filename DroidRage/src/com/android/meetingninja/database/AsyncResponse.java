package com.android.meetingninja.database;

public interface AsyncResponse<T> {
	void processFinish(T result);
}
