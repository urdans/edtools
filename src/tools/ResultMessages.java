package tools;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 This class is a container of result messages for classes that perform
 calculations of any type. Refer to {@link ResultMessage} for details.
 @Immutable


 Error & warning numbers schedule:
 001->049: VoltageDrop class
 050->099: Conductor & Cable class
 100->149: Conduit class
 150->199: Bundle class
 200->299: Circuit class

 */
public class ResultMessages implements ROResultMessages{
	private final List<ResultMessage> resultMessages = new ArrayList<>();

	/**
	 Adds a message object to this result message container.
	 @param msg The existing message object to be added to this result message
	 container.
	 @see ResultMessage
	 */
	public void add(ResultMessage msg){
		if(resultMessages.contains(msg))
			return;
		resultMessages.add(msg);
	}

	/**
	 @return The string message that correspond to the given message number.
	 @param number A message number.
	 */
	@Override
	public String getMessage(int number) {
		return resultMessages.stream()
				.filter(msg -> msg.getNumber() == number)
				.map(ResultMessage::getMessage)
				.findFirst().orElse("");
	}

	/**
	 Asks if this result message container already contains a message with
	 the given number.
	 @param number The message number to check.
	 @return True if this container contains that message number, false
	 otherwise.
	 */
	@Override
	public boolean containsMessage(int number){
		return !getMessage(number).equals("");
	}

	@Override
	public boolean containsMessage(ResultMessage msg) {
		return resultMessages.contains(msg);
	}

	/**
	 Asks if the container has messages.
	 @return True if there is at least one message in the container, false
	 otherwise.
	 */
	@Override
	public boolean hasMessages() {
		return resultMessages.size() > 0;
	}

	/**
	 Asks if the container has error messages, that is, any message whose number
	 is negative.
	 @return True if there is at least one error message, false otherwise.
	 */
	@Override
	public boolean hasErrors() {
		return errorCount() > 0;
	}

	/**
	 Asks if the container has warning messages, that is, any message whose
	 number is positive.
	 @return True if there is at least one warning message, false otherwise.
	 */
	@Override
	public boolean hasWarnings() {
		return warningCount() > 0;
	}

	/**
	 Returns the number of error messages in this container.
	 @return The number of error messages in this container.
	 */
	@Override
	public int errorCount() {
		return (int) resultMessages.stream()
				.filter(msg -> msg.getNumber() < 0)
				.count();
	}

	/**
	 Returns the number of warning messages in this container.
	 @return The number of warning messages in this container.
	 */
	@Override
	public int warningCount() {
		return (int) resultMessages.stream()
				.filter(msg -> msg.getNumber() > 0)
				.count();
	}

	/**
	 @return a list with copies of the messages that this object contains.
	 */
	@Override
	public List<ResultMessage> getMessages() {
		//todo: there is no need to make a deep copy. Returning a new container (an ArrayList) is enough. The container can grow
		// in the number of elements or even delete some of them, but this will not affect the container for this class.
		// Also, each ResultMessage is immutable. MAKE TESTS.
		return new ArrayList<>(resultMessages);
	}

	/**
	 Removes from this result message container the message identified with the
	 given number.
	 @param number The number of the message to be removed from this container.
	 */
	public void remove(int number){
		for(ResultMessage msg : resultMessages){
			if(msg.getNumber() == number) {
				resultMessages.remove(msg);
				break;
			}
		}
	}

	/**
	 Removes the message objects from this result message container.
	 @param msg The existing message objects to be removed from this result
	 message container.
	 @see ResultMessage
	 */
	public void remove(ResultMessage...msg){
		for (ResultMessage message : msg) {
			resultMessages.remove(message);
		}
	}

	/**
	 Clear all the registered resultMessages in this container.
	 */
	public void clearMessages(){
		resultMessages.clear();
	}

	/**
	 @return A JSON string of this class.
	 */
	public String toJSON(){
		return JSONTools.toJSON(this);
	}

	@Override
	public String toString() {
		return "ResultMessages{" + "resultMessages=" + resultMessages + '}';
	}
}


