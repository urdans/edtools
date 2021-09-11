package tools;

import java.util.List;

/**
 This interface defines the read-only properties of the {@link ResultMessages}
 class.
 */
public interface ROResultMessages {
	/**
	 @return The string message that correspond to the given message number
	 for the default context.
	 @param number A message number.
	 */
	String getMessage(int number);

	/**
	 @return The string message that correspond to the given message number
	 for the given context.
	 @param context The context of the message.
	 @param number A message number.
	 */
	String getMessage(String context, int number);

	/**
	 Asks if this result message container already contains a message with
	 the given number, under the default context.
	 @param number The message number to check.
	 @return True if this container contains that message number, false
	 otherwise.
	 */
	boolean containsMessage(int number);

	/**
	 Asks if this result message container already contains a message with
	 the given number, under the given context.
	 @param context The context of the message.
	 @param number The message number to check.
	 @return True if this container contains that message number, false
	 otherwise.
	 */
	boolean containsMessage(String context, int number);

	/**
	 Asks if this result message container already contains the given message
	 object under the default context.
	 @param msg The message object to check.
	 @return True if this container contains that message object, false
	 otherwise.
	 */
	boolean containsMessage(ResultMessage msg);

	/**
	 Asks if this result message container already contains the given message
	 object under the given context.
	 @param context The context of the message.
	 @param msg The message object to check.
	 @return True if this container contains that message object, false
	 otherwise.
	 */
	boolean containsMessage(String context, ResultMessage msg);

	/**
	 Asks if the container has messages under the default context.
	 @return True if there is at least one message in the container, false
	 otherwise.
	 */
	boolean hasMessages();

	/**
	 Asks if the container has messages under the given context.
	 @return True if there is at least one message in the container, false
	 otherwise.
	 */
	boolean hasMessages(String context);

	/**
	 Asks if the container has error messages under the default context, that
	 is, any message whose number is negative.
	 @return True if there is at least one error message, false otherwise.
	 */
	boolean hasErrors();

	/**
	 Asks if the container has error messages under the given context, that
	 is, any message whose number is negative.
	 @return True if there is at least one error message, false otherwise.
	 */
	boolean hasErrors(String context);

	/**
	 Asks if the container has warning messages, that is, any message whose
	 number is positive, under the default context.
	 @return True if there is at least one warning message, false otherwise.
	 */
	boolean hasWarnings();

	/**
	 Asks if the container has warning messages, that is, any message whose
	 number is positive, under the given context.
	 @return True if there is at least one warning message, false otherwise.
	 */
	boolean hasWarnings(String context);

	/**
	 Returns the number of error messages in this container under the default
	 context.
	 @return The number of error messages in this container.
	 */
	int errorCount();

	/**
	 Returns the number of error messages in this container under the
	 given context.
	 @return The number of error messages in this container.
	 */
	int errorCount(String context);

	/**
	 Returns the number of warning messages in this container, under the
	 default context.
	 @return The number of warning messages in this container.
	 */
	int warningCount();

	/**
	 Returns the number of warning messages in this container, under the
	 given context.
	 @return The number of warning messages in this container.
	 */
	int warningCount(String context);

	/**
	 @return a list containing a copy of all the registered message objects.
	 */
	List<ContextResultMessage> getMessages();
}
