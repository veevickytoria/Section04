package objects;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "userID", "displayName", "email" })
public class SerializableUser extends User implements Serializable {
	/**
	 * Generated SerialID
	 */
	private static final long serialVersionUID = 1679882752426691670L;

}
