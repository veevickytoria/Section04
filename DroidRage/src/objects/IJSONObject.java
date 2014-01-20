package objects;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;

public interface IJSONObject {
	public String toJSON() throws JsonGenerationException, IOException;
}
