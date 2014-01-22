package com.android.meetingninja.database;

public interface Keys {

	public final String _ID = "id";
	public final String TYPE = "type";
	public final String DELETED = "deleted";
	public final String ERROR_ID = "errorID";
	public final String ERROR_MESSAGE = "errorMessage";

	public interface User {
		public final String PARCEL = "UserParcel";
		public final String LIST = "users";
		public final String ID = "userID";
		public final String NAME = "name";
		public final String EMAIL = "email";
		public final String PHONE = "phone";
		public final String COMPANY = "company";
		public final String TITLE = "title";
		public final String LOCATION = "location";
		public final String CONTACTS = "contacts";
		public final String SCHEDULE = "schedule";
	}

	public interface Note {
		public final String PARCEL = "NoteParcel";
		public final String LIST = "notes";
		public final String ID = "noteID";
		public final String CREATED_BY = "createdBy";
		public final String TITLE = "title";
		public final String DESC = "description";
		public final String CONTENT = "content";
		public final String UPDATED = "dateCreated";
	}

	public interface Task {
		public final String PARCEL = "TaskParcel";
		public final String LIST = "tasks";
		public final String ID = "taskID";
		public final String TITLE = "title";
		public final String DESC = "description";
		public final String DEADLINE = "deadline";
		public final String DATE_CREATED = "dateCreated";
		public final String DATE_ASSIGNED = "dateAssigned";
		public final String CRITERIA = "completionCriteria";
		public final String ASSIGNED_TO = "assignedTo";
		public final String ASSIGNED_FROM = "assignedFrom";
		public final String CREATED_BY = "createdBy";
		public final String COMPLETED = "isCompleted";
	}

	public interface Meeting {
		public final String PARCEL = "MeetingParcel";
		public final String LIST = "meetings";
		public final String ID = "meetingID";
		public final String TITLE = "title";
		public final String LOCATION = "location";
		public final String DATETIME = "datetime";
		public final String START = "datetimeStart";
		public final String END = "datetimeEnd";
		public final String DESC = "description";
		public final String ATTEND = "attendance";
	}

	public interface Comment {
		public final String LIST = "comments";
		public final String ID = "commentID";
		public final String BY = "commentBy";
		public final String ON = "commentOn";
		public final String TEXT = "content";
		public final String DATE = "datePosted";
	}

	public interface Agenda {
		public final String PARCEL = "AgendaParcel";
		public final String ID = "agendaID";
		public final String TITLE = "title";
		public final String MEETING = "meeting";
		public final String CONTENT = "content";
		public final String SUBTOPIC = "subtopic";
		public final String TOPIC = "topic";
		public final String TIME = "time";
		public final String DESC = "description";
	}

	public interface Notification {

	}

	public interface Project {
		public final String PARCEL = "ProjectParcel";
		public final String LIST = "projects";
		public final String ID = "projectID";
		public final String TITLE = "projectTitle";
	}

	public interface Group {
		public final String PARCEL = "GroupParcel";
		public final String LIST = "groups";
		public final String ID = "groupID";
		public final String TITLE = "groupTitle";
		public final String MEMBERS = "members";
	}
}
