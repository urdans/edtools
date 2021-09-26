package tools;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
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

	@Override
	public String getMessage(int number) {
		return resultMessages.stream()
				.filter(msg -> msg.getNumber() == number)
				.map(ResultMessage::getMessage)
				.findFirst().orElse("");
	}

	@Override
	public boolean containsMessage(int number){
		return !getMessage(number).equals("");
	}

	@Override
	public boolean containsMessage(ResultMessage...msg) {
		for(ResultMessage rm : msg) {
			if(containsMessage(rm.getNumber()))
				return true;
		}
		return false;
	}

	@Override
	public boolean hasMessages() {
		return resultMessages.size() > 0;
	}

	@Override
	public boolean hasErrors() {
		return errorCount() > 0;
	}

	@Override
	public boolean hasWarnings() {
		return warningCount() > 0;
	}

	@Override
	public int errorCount() {
		return (int) resultMessages.stream()
				.filter(msg -> msg.getNumber() < 0)
				.count();
	}

	@Override
	public int warningCount() {
		return (int) resultMessages.stream()
				.filter(msg -> msg.getNumber() > 0)
				.count();
	}

	@Override
	public List<ResultMessage> getMessages() {
		return new ArrayList<>(resultMessages);
	}

	/**
	 Removes from this result message container the message identified with the
	 given number, under the default context.
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
	 Removes the message objects from this result message container, under
	 the default context.
	 @param msg The existing message objects to be removed from this result
	 message container.
	 @see ResultMessage
	 */
	public void remove(ResultMessage...msg){
		for (ResultMessage message : msg)
			resultMessages.remove(message);
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


