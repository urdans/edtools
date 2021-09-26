package tools;

import java.util.List;

/**
 This interface defines the read-only properties of the {@link ResultMessages}
 class.
 */
public interface ROResultMessages {
	/**
	 @return The string message that correspond to the given message number.
	 @param number A message number.
	 */
	String getMessage(int number);

	/**
	 Asks if this result message container already contains a message with
	 the given number.
	 @param number The message number to check.
	 @return True if this container contains that message number, false
	 otherwise.
	 */
	boolean containsMessage(int number);

	/**
	 Asks if this result message container already contains the given message
	 objects.
	 @param msg The message object to check.
	 @return True if this container contains that message object, false
	 otherwise.
	 */
	boolean containsMessage(/*ResultMessage msg*//*ResultMessage msg*/ResultMessage...msg);

	/**
	 Asks if the container has messages.
	 @return True if there is at least one message in the container, false
	 otherwise.
	 */
	boolean hasMessages();

	/**
	 Asks if the container has error messages, that is, any message whose
	 number is negative.
	 @return True if there is at least one error message, false otherwise.
	 */
	boolean hasErrors();

	/**
	 Asks if the container has warning messages, that is, any message whose
	 number is positive.
	 @return True if there is at least one warning message, false otherwise.
	 */
	boolean hasWarnings();

	/**
	 Returns the number of error messages in this container.
	 @return The number of error messages in this container.
	 */
	int errorCount();

	/**
	 Returns the number of warning messages in this container.
	 @return The number of warning messages in this container.
	 */
	int warningCount();

	/**
	 @return a list containing a copy of all the registered message objects.
	 */
	List<ResultMessage> getMessages();
}
