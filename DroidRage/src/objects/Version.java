package objects;

public class Version {
	private String date;
	private String editor;
	private String id;

	public Version() {
	}

	public Version(String date, String editor) {
		this.date = date;
		this.editor = editor;
	}

	public String getId() {
		return id;
	}

	public void setId(int id) {
		this.id = Integer.toString(id);
	}

	public void setId(String id) {
		int testInt = Integer.valueOf(id);
		setId(testInt);
	}

	public String getDate() {
		return this.date;
	}

	public String getEditor() {
		return this.editor;
	}

}
