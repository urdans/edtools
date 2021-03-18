package tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONTools {
	private static final ObjectMapper mapper = new ObjectMapper();

	public static String toJSON(Object object){
		try {
			return mapper
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			return "{ }";
		}
	}
}
