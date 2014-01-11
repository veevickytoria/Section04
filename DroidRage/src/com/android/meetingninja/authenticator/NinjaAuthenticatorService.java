package com.android.meetingninja.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NinjaAuthenticatorService extends Service {
	public NinjaAuthenticatorService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		NinjaAccountAuthenticator authenticator = new NinjaAccountAuthenticator(
				this);
		return authenticator.getIBinder();
	}
}
