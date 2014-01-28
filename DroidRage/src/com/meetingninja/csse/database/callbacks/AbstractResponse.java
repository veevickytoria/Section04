package com.meetingninja.csse.database.callbacks;

import com.meetingninja.csse.database.AsyncResponse;

public abstract class AbstractResponse<T> implements AsyncResponse<T> {

	protected T data;
	protected ResponseThread t = new ResponseThread();

	@Override
	public void processFinish(T result) {
		this.data = result;
	}

	public synchronized T getData() {
		while (data == null);
		return data;
	}

	protected class ResponseThread extends Thread {

		@Override
		public void run() {
			while (data == null) {
				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
