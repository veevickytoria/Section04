/*******************************************************************************
 * Copyright (C) 2014 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.meetingninja.csse.sync;

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
