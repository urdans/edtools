package javaTests;

import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.TradeSize;
import eecalcs.conduits.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConduitPropertiesTest {

	@Test
	void getArea() {
		assertEquals(0, ConduitProperties.getArea(Type.EMT, TradeSize.T3$8));
		assertEquals(0.304, ConduitProperties.getArea(Type.EMT, TradeSize.T1$2));
		assertEquals(0, ConduitProperties.getArea(Type.EMT, TradeSize.T6));

		assertEquals(0, ConduitProperties.getArea(Type.IMC, TradeSize.T3$8));
		assertEquals(3.63, ConduitProperties.getArea(Type.IMC, TradeSize.T2));
		assertEquals(0, ConduitProperties.getArea(Type.IMC, TradeSize.T6));

		assertEquals(0.192, ConduitProperties.getArea(Type.LFMC, TradeSize.T3$8));
		assertEquals(12.692, ConduitProperties.getArea(Type.LFMC, TradeSize.T4));
		assertEquals(0, ConduitProperties.getArea(Type.LFMC, TradeSize.T5));

		assertEquals(0, ConduitProperties.getArea(Type.PVCEB, TradeSize.T3$8));
		assertEquals(3.874, ConduitProperties.getArea(Type.PVCEB, TradeSize.T2));
		assertEquals(31.53, ConduitProperties.getArea(Type.PVCEB, TradeSize.T6));
	}
}