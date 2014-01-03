package com.android.meetingninja.database.local;

import android.database.Cursor;

public abstract class ModelCursor<T> {
	protected final Cursor crsr;
	protected T model;

	public ModelCursor(Cursor c) {
		this.crsr = c;
	}

	abstract public T getModel();
}
