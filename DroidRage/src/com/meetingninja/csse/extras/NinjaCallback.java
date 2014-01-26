package com.meetingninja.csse.extras;

import java.util.List;

public interface NinjaCallback {
	public void onCallback();

	public abstract void onCallback(Boolean result);

	public abstract void onCallback(Integer result);

	public abstract void onCallback(String result);

	public abstract void onCallback(List<Object> result);
}
