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
package edu.meetingninja.rhit.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Stubbed Account Authenticator based off of
 * {@code http://udinic.wordpress.com/2013/04/24/write-your-own-android-authenticator/}
 * 
 * @author moorejm
 * 
 */
public class NinjaAccountAuthenticator extends AbstractAccountAuthenticator {

	private String TAG = NinjaAccountAuthenticator.class.getSimpleName();
	private final Context mContext;

	public NinjaAccountAuthenticator(Context context) {
		super(context);

		mContext = context;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

}
