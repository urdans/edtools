package test.java;

import eecalcs.circuits.OCPD;
import eecalcs.loads.MotorProperties;
import org.junit.jupiter.api.Test;

import static eecalcs.loads.MotorProperties.DesignLetter.DESIGN_B;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MotorPropertiesTest {

	@Test
	void getOCPDRatingPerType() {
		assertEquals(3.0,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.NON_TIME_DELAY_FUSE,
						MotorProperties.Type.AC1P, null));

		assertEquals(1.75,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.DUAL_ELEMENT_TIME_DELAY_FUSE,
						MotorProperties.Type.AC3P, null));

		assertEquals(8.0,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.INSTANTANEOUS_TRIP_BREAKER,
						MotorProperties.Type.AC2P, null));

		assertEquals(2.5,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.INVERSE_TIME_BREAKER,
						MotorProperties.Type.AC3PS, null));

		assertEquals(11.0,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.INSTANTANEOUS_TRIP_BREAKER,
						MotorProperties.Type.AC3PS, DESIGN_B));

		assertEquals(3.0,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.NON_TIME_DELAY_FUSE,
						MotorProperties.Type.AC3PS, DESIGN_B));

		assertEquals(2.5,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.INVERSE_TIME_BREAKER,
						MotorProperties.Type.AC3PS, DESIGN_B));

		assertEquals(8.0,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.INSTANTANEOUS_TRIP_BREAKER,
						MotorProperties.Type.AC3PS, null));

		assertEquals(1.5,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.NON_TIME_DELAY_FUSE,
						MotorProperties.Type.AC2P_WR, null));

		assertEquals(8.0,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.INSTANTANEOUS_TRIP_BREAKER,
						MotorProperties.Type.AC3P_WR, null));

		assertEquals(1.5,
				MotorProperties.getMaxOCPDRatingFactorPerType(OCPD.Type.INVERSE_TIME_BREAKER,
						MotorProperties.Type.AC3P_WR, null));
	}
}