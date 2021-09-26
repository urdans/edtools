package tools;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.io.StringWriter;

public class JSONTools {
	private static final ObjectMapper mapper = new ObjectMapper();
	// Setup a pretty printer with an indenter (indenter has 4 spaces in this case)
	private static final DefaultPrettyPrinter.Indenter indenter =
			new DefaultIndenter("\t", DefaultIndenter.SYS_LF);
	private static final DefaultPrettyPrinter printer = new DefaultPrettyPrinter();

	static {
		printer.indentObjectsWith(indenter);
		printer.indentArraysWith(indenter);
		mapper.setDefaultPrettyPrinter(printer);
	}

	public static String toJSON(Object object){
		try {
			return mapper
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			System.out.println(e.getMessage());
			return "{ }";
		}
	}

	public static <T> T toObject(String json, Class <T> classType){
		ObjectMapper mapper = new ObjectMapper();
		T fromJSON = null;
		try {
			fromJSON = mapper.readValue(json, classType);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return fromJSON;
	}

	public static JsonGenerator getJsonGenerator(){
		StringWriter jsonObjectWriter = new StringWriter();
		JsonFactory jsonF = new JsonFactory();
		JsonGenerator jsonGenerator = null;
		try {
			jsonGenerator = jsonF.createGenerator(jsonObjectWriter);
			jsonGenerator.setPrettyPrinter(printer);
			jsonGenerator.setCodec(mapper);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonGenerator;
	}
}
