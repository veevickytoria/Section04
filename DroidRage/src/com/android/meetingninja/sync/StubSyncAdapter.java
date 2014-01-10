package com.android.meetingninja.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Stubbed SyncAdapter based off of
 * {@code http://udinic.wordpress.com/2013/07/24/write-your-own-android-sync-adapter/}
 * 
 * @author moorejm
 * 
 */
public class StubSyncAdapter extends AbstractThreadedSyncAdapter {
	private final AccountManager mAccountManager;

	public StubSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context);
	}

	public StubSyncAdapter(Context context, boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		mAccountManager = AccountManager.get(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		// TODO Auto-generated method stub

	}

}
