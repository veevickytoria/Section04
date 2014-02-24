package objects;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractJSONObject<T> implements IJSONObject<T> {
	protected static final ObjectMapper MAPPER = new ObjectMapper();

	public abstract String getID();

	public abstract void setID(String id);

	protected abstract void setID(int id);
}
