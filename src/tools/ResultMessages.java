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
	private final List<ContextResultMessage> resultMessages = new ArrayList<>();
	private final String defaultContext;

	public ResultMessages(String defaultContext) {
		this.defaultContext = defaultContext;
	}

	/**
	 Adds a message object (using the default context) to this result message
	 container.
	 @param msg The existing message object to be added to this result message
	 container.
	 @see ResultMessage
	 */
	public void add(ResultMessage msg){
		add(defaultContext, msg);
	}

	/**
	 Adds a message object (using the given context) to this result message
	 container.
	 @param context The context to which the given result message belongs.
	 @param msg The existing message object to be added to this result message
	 container.
	 @see ResultMessage
	 */
	public void add(String context, ResultMessage msg){
		if(containsMessage(context, msg))
			return;
		resultMessages.add(new ContextResultMessage(context, msg));
	}

	@Override
	public String getMessage(int number) {
		return getMessage(defaultContext, number);
	}

	@Override
	public String getMessage(String context, int number) {
		return resultMessages.stream()
				.filter(crm -> crm.context == context
						&& crm.resultMessage.getNumber() == number)
				.map(crm -> crm.resultMessage.getMessage())
				.findFirst().orElse("");
	}

	@Override
	public boolean containsMessage(int number){
		return containsMessage(defaultContext, number);
	}

	@Override
	public boolean containsMessage(String context, int number){
		return resultMessages.stream()
				.anyMatch(crm -> crm.context == context
						&& crm.resultMessage.getNumber() == number);
	}

	@Override
	public boolean containsMessage(ResultMessage msg) {
		return containsMessage(defaultContext, msg);
	}

	@Override
	public boolean containsMessage(String context, ResultMessage msg) {
		return resultMessages.stream()
				.anyMatch(crm -> crm.context == context
						&& crm.resultMessage == msg);
	}

	@Override
	public boolean hasMessages() {
		return hasMessages(defaultContext);
	}

	@Override
	public boolean hasMessages(String context) {
		return resultMessages.stream()
				.anyMatch(crm -> crm.context == context);
	}

	@Override
	public boolean hasErrors() {
		return hasErrors(defaultContext);
	}

	@Override
	public boolean hasErrors(String context) {
		return errorCount(context) > 0;
	}

	@Override
	public boolean hasWarnings() {
		return warningCount(defaultContext) > 0;
	}

	@Override
	public boolean hasWarnings(String context) {
		return warningCount(context) > 0;
	}

	@Override
	public int errorCount() {
		return errorCount(defaultContext);
	}

	@Override
	public int errorCount(String context) {
		return (int) resultMessages.stream()
				.filter(crm -> crm.resultMessage.getNumber() < 0)
				.count();
	}

	@Override
	public int warningCount() {
		return warningCount(defaultContext);
	}

	@Override
	public int warningCount(String context) {
		return (int) resultMessages.stream()
				.filter(crm -> crm.resultMessage.getNumber() > 0)
				.count();
	}

	@Override
	public List<ContextResultMessage> getMessages() {
		return new ArrayList<>(resultMessages);
	}

	/**
	 Removes from this result message container the message identified with the
	 given number, under the default context.
	 @param number The number of the message to be removed from this container.
	 */
	public void remove(int number){
		remove(defaultContext, number);
	}

	/**
	 Removes from this result message container the message identified with the
	 given number, under the given context.
	 @param context The context of the message.
	 @param number The number of the message to be removed from this container.
	 */
	public void remove(String context, int number){
		for(ContextResultMessage crm : resultMessages){
			if(crm.context == context && crm.resultMessage.getNumber() == number) {
				resultMessages.remove(crm);
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
		remove(defaultContext, msg);
	}

	/**
	 Removes the message objects from this result message container, under
	 the given context.
	 @param context The context of the message.
	 @param msg The existing message objects to be removed from this result
	 message container.
	 @see ResultMessage
	 */
	public void remove(String context, ResultMessage...msg){
		for (ResultMessage message : msg) {
			remove(context, message.getNumber());
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


