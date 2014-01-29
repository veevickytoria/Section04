package com.meetingninja.csse;

import java.io.Serializable;

public class SimpleUser implements Serializable {
	private String name;
	private String email;

	public SimpleUser(String n, String e) {
		name = n;
		email = e;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return name;
	}
}