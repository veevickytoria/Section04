package com.droidrage.meetingninja.database;

public interface AsyncResponse<T> {
	void processFinish(T result);
}
