package test.java;

import eecalcs.conductors.raceways.Conductor;
import eecalcs.conduits.Material;
import eecalcs.voltagedrop.VoltageDropAC;
import tools.JSONTools;
import tools.ResultMessage;
import tools.ResultMessages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tools.JSONTools.toObject;

public class JsonConversionTest {

	//@Test
	void jsonResultMessage() {
		String msg = "This is an error message";
		ResultMessage resultMessage = new ResultMessage(msg, -1);
		String json1 = JSONTools.toJSON(resultMessage);
		ResultMessage fromJSON = toObject(json1, ResultMessage.class);

		assertEquals(resultMessage.getNumber(), fromJSON.getNumber());
		assertEquals(resultMessage.getMessage(), fromJSON.getMessage());
		assertEquals(json1, fromJSON.toString());

//		System.out.println(json1+"\n"+JSONTools.toJSON(fromJSON));
	}

	//@Test
	void jsonResultMessages() {
		ResultMessages resultMessages = new ResultMessages();
		resultMessages.add(new ResultMessage("Error message No 21", -21));
		resultMessages.add(new ResultMessage("warning message No 22", 22));

		String json1 = resultMessages.toString();
		ResultMessages fromJSON = toObject(json1, ResultMessages.class);

		assertEquals(resultMessages.getMessage(-21), fromJSON.getMessage
		(-21));
		assertEquals(1, fromJSON.errorCount());
		assertEquals(1, fromJSON.warningCount());
		assertEquals(json1, fromJSON.toString());

//		System.out.println(json1+"\n"+fromJSON.toString());
	}

	//@Test
	void jsonVoltageDropAC() {
		Conductor conductor = new Conductor();
		VoltageDropAC voltDrop = new VoltageDropAC(conductor)
				.setNumberOfSets(1)
				.setLoadCurrent(200)
				.setPowerFactor(1.0)
				.setConduitMaterial(Material.STEEL);
		String json1 = voltDrop.toString();
		System.out.println(json1);
		VoltageDropAC fromJSON = toObject(json1, VoltageDropAC.class);

		assertEquals(voltDrop.getSourceVoltageSystem(),	fromJSON.getSourceVoltageSystem());
		assertEquals(voltDrop.getMinSizeForMaxVD(), fromJSON.getMinSizeForMaxVD());
		assertEquals(voltDrop.getMaxLengthForMaxVD(), fromJSON.getMaxLengthForMaxVD());

		System.out.println(json1+"\n"+fromJSON.toString());
	}

}
