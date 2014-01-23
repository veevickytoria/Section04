package com.android.meetingninja.test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.meetingninja.rhit.MainActivity;
import edu.meetingninja.rhit.database.Keys;
import edu.meetingninja.rhit.user.ProfileActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.AndroidTestCase;

public class MockitoTests extends AndroidTestCase {
	@Mock
	Context context;

	@Override
	protected void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	public void testQuery() throws Exception {
		Intent intent = new Intent(context, ProfileActivity.class);
		intent.putExtra("QUERY", "query");
		intent.putExtra("VALUE", "value");

		assertNotNull(intent);
		Bundle extras = intent.getExtras();
		assertNotNull(extras);
		assertEquals("query", extras.getString("QUERY"));
		assertEquals("value", extras.getString("VALUE"));
	}
}
