package test.java;

import org.junit.jupiter.api.Test;
import tools.ROResultMessages;
import tools.ResultMessage;
import tools.ResultMessages;

class ResultMessagesTest {

	class Owner{
		private final ResultMessages resultMessages = new ResultMessages();
		public Owner(){
			resultMessages.add(new ResultMessage("Test error message",-1));
		}

		public ROResultMessages getResultMessages() {
			return resultMessages;
		}
	}

	@Test
	void immutabilityOfReturnedClass() {
		/*Owner owner = new Owner();
		List<ResultMessage> messages = owner.getResultMessages().getMessages();
		messages.forEach(m -> {
			m.message= "I screwed this up!";
			m.number = 2000000;
		});*/
	}
}