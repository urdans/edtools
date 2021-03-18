package tools;
/**
 * This class encapsulates a message. A message is a string text explaining a
 * condition to the user. An unique number is associated with a message.
 * ResultMessage objects are usually returned by objects that perform calculations.
 * If the message's number is negative, it must be interpreted as an error
 * message. If this number is positive it must be interpreted as a warning.
 * A number equals to zero has no meaning and eventually can be used to indicate
 * a neutral message, like a "status".
 */
public class ResultMessage {
	public final String message;
	public final int number;

	/**
	 * Constructs a message object with a text message and number
	 * @param message The string containing the message
	 * @param number The number of the message
	 */
	public ResultMessage(String message, int number) {
		this.message = message;
		this.number = number;
	}

	public ResultMessage append(String additionalInfo){
		return new ResultMessage(message + "\n" + additionalInfo, number);
	}
}
