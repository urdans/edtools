package tools;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 This class contains and manage resultMessages that resultsVD from any calculations
 or validation performed by this package tool. Negative message number are
 considered errors that impeaches proper calculation. Positive resultMessages numbers
 are warnings that doesn't affect the calculation result but that the user
 needs to be aware of.
 */
public class ResultMessages implements ROResultMessages {
	private final List<ResultMessage> resultMessages =  new ArrayList<>();

	/**
	 Adds a new message to this result message container; if the message number
	 already exists, nothing is added and the existing message will remain.
	 Negative numbers are considered errors, while positive numbers are
	 considered warnings.

	 @param resultMessage The text of the message.
	 @param number The number of the message.
	 @see ResultMessage
	 */
	public void add(String resultMessage, int number) {
		for(ResultMessage msg: resultMessages){
			if(msg.number == number) return;
		}
		resultMessages.add(new ResultMessage(resultMessage, number));
	}

	/**
	 Adds a message object to this result message container.

	 @param msg The existing message object to be added to this result message
	 container.

	 @see ResultMessage
	 */
	public void add(ResultMessage msg){
		if(resultMessages.contains(msg)) return;
		resultMessages.add(msg);
	}

	@Override
	public String getMessage(int number) {
		for(ResultMessage msg: resultMessages){
			if(msg.number == number) return msg.message;
		}
		return "";
	}

	@Override
	public boolean containsMessage(int number){
		return !getMessage(number).equals("");
	}

/*	@Override
	public boolean containsMessage(ResultMessage msg){
		return resultMessages.contains(msg);
	}*/

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
		int result = 0;
		for(ResultMessage msg: resultMessages){
			if(msg.number < 0) result++;
		}
		return result;
	}

	@Override
	public int warningCount() {
		int result = 0;
		for(ResultMessage msg: resultMessages){
			if(msg.number > 0) result++;
		}
		return result;
	}

	/**
	 Removes from this result message container the message identified with the
	 given number.

	 @param number The number of the message to be removed from this container.
	 */
	public void remove(int number){
		for(ResultMessage msg : resultMessages){
			if(msg.number == number) {
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

	@Override
	public List<ResultMessage> getMessages() {
		List<ResultMessage> m = new ArrayList<>();
		//m = new ArrayList<>(resultMessages);
		for (ResultMessage mes: resultMessages) {
			m.add(new ResultMessage(mes.message, mes.number));
		}
		return m;
	}

	/**
	 Clear all the registered resultMessages in this container.
	 */
	public void clearMessages(){
		resultMessages.clear();
	}

	/**
	 Copies all the resultMessages from the given source into this object. The
	 existing resultMessages are preserved. If the source has resultMessages that already
	 exists in this object, such resultMessages are not copied.
	 @param source The ResultMessages object from which resultMessages are copied.
	 @return The number of resultMessages copied.
	 */
	public int copyFrom(@NotNull ROResultMessages source){
		int count = 0;
		for(ResultMessage sourceResultMessage : source.getMessages()) {
			if (!containsMessage(sourceResultMessage.number)){
				count++;
				add(sourceResultMessage);
			}
		}
		return count;
	}

	@Override
	public String toString(){
		return JSONTools.toJSON(this);
	}
}


