package objects;

public class Contact implements Comparable<Contact>{

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
	public int compareTo(Contact another) {
		if (another == null) {
			return 1;
		}
		return this.contact.getDisplayName().compareToIgnoreCase(another.getContact().getDisplayName());
	}
//	@Override
//	public boolean equals(Object p){
//		if(p instanceof Project){
//			return this.getProjectID().equals(((Project) p).getProjectID());
//		}
//		return false;
//	}

}
