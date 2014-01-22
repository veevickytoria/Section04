package objects;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonNode;

public interface IJSONObject<T> {
	public JsonNode toJSON() throws JsonGenerationException, IOException;
}
