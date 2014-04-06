package com.meetingninja.csse.extras;

public class SleeperThread extends Thread {

	private long ms;

	public SleeperThread(long ms) {
		this.ms = ms;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.getLocalizedMessage();
		}
	}
}
