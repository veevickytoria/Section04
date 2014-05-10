package com.meetingninja.csse.projects;

import objects.Contact;
import objects.User;

import com.meetingninja.csse.user.UserListFragment;

public class MemberListFragment extends UserListFragment {
	
	ViewProjectActivity pCont;
	public MemberListFragment setProjectController(ViewProjectActivity pCont){
		this.pCont = pCont;
		return this;
	}
	@Override
	protected void addContact(User user) {
		pCont.addMember(user);
	}
	@Override
	protected void deleteContact(Contact item) {
		pCont.deleteMember(item.getContact());	
	}
}
