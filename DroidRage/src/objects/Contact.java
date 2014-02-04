package objects;

public class Contact {

	private User contact;
	private String relationID;

	public Contact() {
		// Required empty constructor
	}

	public Contact(User contact, String relationID) {
		setContact(contact);
		setRelationID(relationID);
	}

	public void setContact(User contact) {
		this.contact = contact;
	}

	public void setRelationID(String relationID) {
		this.relationID = relationID;
	}

	public User getContact() {
		return contact;
	}

	public String getRelationID() {
		return relationID;
	}

}
