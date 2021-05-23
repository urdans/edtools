package tools;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 This class encapsulates a message. A message is a string text explaining a
 condition to the user. An unique number is associated with a message.
 ResultMessage objects are usually returned by objects that perform calculations.
 If the message's number is negative, it must be interpreted as an error
 message. If this number is positive it must be interpreted as a warning.
 A number equals to zero has no meaning and eventually can be used to indicate
 a neutral message, like a "status".
 @Immutable
 */
public class ResultMessage {
	private final String message;
	private final int number;

	/**
	 * Constructs a message object with a text message and number
	 * @param message The string containing the message
	 * @param number The number of the message
	 */
	@JsonCreator
	public ResultMessage(@JsonProperty("message") String message,
	                     @JsonProperty("number") int number) {
		this.message = message;
		this.number = number;
	}

	public ResultMessage append(String additionalInfo){
		return new ResultMessage(message + "\n" + additionalInfo, number);
	}

	public String getMessage() {
		return message;
	}

	public int getNumber() {
		return number;
	}

	/**
	 @return A JSON string of this class.
	 */
	public String toJSON(){
		return JSONTools.toJSON(this);
	}

	@Override
	public String toString() {
		return "ResultMessage{" + "message='" + message + '\'' + ", number=" + number + '}';
	}
}
