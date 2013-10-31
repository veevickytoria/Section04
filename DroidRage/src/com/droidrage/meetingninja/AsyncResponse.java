package com.droidrage.meetingninja;

public interface AsyncResponse<T> {
	void processFinish(T result);
}
