package com.meetingninja.csse.projects;

import objects.Contact;
import objects.User;

import com.meetingninja.csse.user.UserListFragment;

public class MemberListFragment extends UserListFragment {
	
	ViewProjectActivity pCont;
	@Override
	protected void addContact(User user) {
//		AddContactTask adder = new AddContactTask(this);
//		adder.addContact(user.getID());
		pCont.addMember(user);
	}
	@Override
	protected void deleteContact(Contact item) {
//		DeleteContactTask deleter = new DeleteContactTask(this);
//		deleter.deleteContact(relationID);
		pCont.deleteMember(item.getContact());
		
	}
	
	public MemberListFragment setProjectController(ViewProjectActivity pCont){
		this.pCont = pCont;
		return this;
	}
	
	
}
