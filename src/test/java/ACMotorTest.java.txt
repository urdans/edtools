package test.java;

import eecalcs.circuits.Circuit;
import eecalcs.circuits.OCPD;
import eecalcs.loads.ACMotor;
import eecalcs.loads.Horsepower;
import eecalcs.loads.MotorProperties;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;

import static eecalcs.loads.MotorProperties.DesignLetter.DESIGN_B;
import static org.junit.jupiter.api.Assertions.*;

class ACMotorTest {
	ACMotor acMotor;
	@Test
	void getFlc(){
		//general
		assertEquals(0, MotorProperties.getFlc(null, 90, Horsepower.HP_1$4));
		assertEquals(0, MotorProperties.getFlc(MotorProperties.Type.DC, 191, Horsepower.HP_1$4));
		assertEquals(0, MotorProperties.getFlc(MotorProperties.Type.DC, 90, null));
		assertEquals(0, MotorProperties.getFlc(null, 0, null));
		//DC motors
		//90
		assertEquals(4.0, MotorProperties.getFlc(MotorProperties.Type.DC, 90, Horsepower.HP_1$4));
		assertEquals(12.2, MotorProperties.getFlc(MotorProperties.Type.DC, 90, Horsepower.HP_1));
		assertEquals(6.8, MotorProperties.getFlc(MotorProperties.Type.DC, 90, Horsepower.HP_1$2));
		//120
		assertEquals(3.1, MotorProperties.getFlc(MotorProperties.Type.DC, 120, Horsepower.HP_1$4));
		assertEquals(17.0, MotorProperties.getFlc(MotorProperties.Type.DC, 120, Horsepower.HP_2));
		assertEquals(76, MotorProperties.getFlc(MotorProperties.Type.DC, 120, Horsepower.HP_10));
		//180
		assertEquals(2.0, MotorProperties.getFlc(MotorProperties.Type.DC, 180, Horsepower.HP_1$4));
		assertEquals(16.0, MotorProperties.getFlc(MotorProperties.Type.DC, 180, Horsepower.HP_3));
		assertEquals(27.0, MotorProperties.getFlc(MotorProperties.Type.DC, 180, Horsepower.HP_5));
		//240
		assertEquals(2.7, MotorProperties.getFlc(MotorProperties.Type.DC, 240, Horsepower.HP_1$2));
		assertEquals(29.0, MotorProperties.getFlc(MotorProperties.Type.DC, 240, Horsepower.HP_7_1$2));
		assertEquals(675.0, MotorProperties.getFlc(MotorProperties.Type.DC, 240,Horsepower.HP_200));
		//500
		assertEquals(13.6, MotorProperties.getFlc(MotorProperties.Type.DC, 500,	Horsepower.HP_7_1$2));
		assertEquals(67.0, MotorProperties.getFlc(MotorProperties.Type.DC, 500, Horsepower.HP_40));
		assertEquals(246.0, MotorProperties.getFlc(MotorProperties.Type.DC, 500, Horsepower.HP_150));
		//550
		assertEquals(16.0, MotorProperties.getFlc(MotorProperties.Type.DC, 550, Horsepower.HP_10));
		assertEquals(75.0, MotorProperties.getFlc(MotorProperties.Type.DC, 550, Horsepower.HP_50));
		assertEquals(148.0, MotorProperties.getFlc(MotorProperties.Type.DC, 550, Horsepower.HP_100));
		//AC 1P motors
		//115
		assertEquals(4.4, MotorProperties.getFlc(MotorProperties.Type.AC1P, 115, Horsepower.HP_1$6));
		assertEquals(16.0, MotorProperties.getFlc(MotorProperties.Type.AC1P, 110, Horsepower.HP_1));
		assertEquals(100.0, MotorProperties.getFlc(MotorProperties.Type.AC1P, 120, Horsepower.HP_10));
		//200
		assertEquals(3.3, MotorProperties.getFlc(MotorProperties.Type.AC1P, 200, Horsepower.HP_1$4));
		assertEquals(11.5, MotorProperties.getFlc(MotorProperties.Type.AC1P, 200, Horsepower.HP_1_1$2));
		assertEquals(46.0, MotorProperties.getFlc(MotorProperties.Type.AC1P, 200, Horsepower.HP_7_1$2));
		//208
		assertEquals(4.0, MotorProperties.getFlc(MotorProperties.Type.AC1P, 208, Horsepower.HP_1$3));
		assertEquals(13.2, MotorProperties.getFlc(MotorProperties.Type.AC1P, 208, Horsepower.HP_2));
		assertEquals(30.8, MotorProperties.getFlc(MotorProperties.Type.AC1P, 208, Horsepower.HP_5));
		//230
		assertEquals(4.9, MotorProperties.getFlc(MotorProperties.Type.AC1P, 220, Horsepower.HP_1$2));
		assertEquals(6.9, MotorProperties.getFlc(MotorProperties.Type.AC1P, 230, Horsepower.HP_3$4));
		assertEquals(50, MotorProperties.getFlc(MotorProperties.Type.AC1P, 240, Horsepower.HP_10));
		//AC 2P motors
		//115
		assertEquals(4.0, MotorProperties.getFlc(MotorProperties.Type.AC2P, 115, Horsepower.HP_1$2));
		assertEquals(6.4, MotorProperties.getFlc(MotorProperties.Type.AC2P, 115, Horsepower.HP_1));
		assertEquals(11.8, MotorProperties.getFlc(MotorProperties.Type.AC2P, 115, Horsepower.HP_2));
		//230
		assertEquals(2.0, MotorProperties.getFlc(MotorProperties.Type.AC2P, 230, Horsepower.HP_1$2));
		assertEquals(90, MotorProperties.getFlc(MotorProperties.Type.AC2P, 230, Horsepower.HP_40));
		assertEquals(416, MotorProperties.getFlc(MotorProperties.Type.AC2P, 230, Horsepower.HP_200));
		//460
		assertEquals(1.0, MotorProperties.getFlc(MotorProperties.Type.AC2P, 440, Horsepower.HP_1$2));
		assertEquals(45, MotorProperties.getFlc(MotorProperties.Type.AC2P, 460, Horsepower.HP_40));
		assertEquals(208, MotorProperties.getFlc(MotorProperties.Type.AC2P, 480, Horsepower.HP_200));
		//575
		assertEquals(0.8, MotorProperties.getFlc(MotorProperties.Type.AC2P, 550, Horsepower.HP_1$2));
		assertEquals(8.0, MotorProperties.getFlc(MotorProperties.Type.AC2P, 575, Horsepower.HP_7_1$2));
		assertEquals(45.0, MotorProperties.getFlc(MotorProperties.Type.AC2P, 655, Horsepower.HP_50));
		//2300
		assertEquals(14.0, MotorProperties.getFlc(MotorProperties.Type.AC2P, 2300, Horsepower.HP_60));
		assertEquals(23.0, MotorProperties.getFlc(MotorProperties.Type.AC2P, 2300, Horsepower.HP_100));
		assertEquals(32.0, MotorProperties.getFlc(MotorProperties.Type.AC2P, 2300, Horsepower.HP_150));
		//AC 3P motors
		//115
		assertEquals(4.4, MotorProperties.getFlc(MotorProperties.Type.AC3P, 115, Horsepower.HP_1$2));
		assertEquals(12.0, MotorProperties.getFlc(MotorProperties.Type.AC3P, 115, Horsepower.HP_1_1$2));
		assertEquals(13.6, MotorProperties.getFlc(MotorProperties.Type.AC3P, 115, Horsepower.HP_2));
		//230
		assertEquals(2.2, MotorProperties.getFlc(MotorProperties.Type.AC3P, 230, Horsepower.HP_1$2));
		assertEquals(28.0, MotorProperties.getFlc(MotorProperties.Type.AC3P, 230, Horsepower.HP_10));
		assertEquals(480.0, MotorProperties.getFlc(MotorProperties.Type.AC3P, 230, Horsepower.HP_200));
		//2300
		assertEquals(16.0, MotorProperties.getFlc(MotorProperties.Type.AC3P, 2300, Horsepower.HP_60));
		assertEquals(31.0, MotorProperties.getFlc(MotorProperties.Type.AC3P, 2300, Horsepower.HP_125));
		assertEquals(60.0, MotorProperties.getFlc(MotorProperties.Type.AC3P, 2300, Horsepower.HP_250));

	}

	@Test
	void constructingAnACMotor(){
		//case 1
		//DC motor not supported
		assertThrows(IllegalArgumentException.class, ()->
				new ACMotor(MotorProperties.Type.DC, 120,
						Horsepower.HP_1$4, VoltageSystemAC.v120_1ph_2w));

		//no 1φ 480v motors on NEC-430.248 (wrong motorVoltageRating)
		assertThrows(IllegalArgumentException.class, ()->
				new ACMotor(MotorProperties.Type.AC1P, 480,
						Horsepower.HP_1$4, VoltageSystemAC.v480_1ph_2w));

		//no 1φ 208v motors on NEC-430.249 (wrong motorVoltageRating)
		assertThrows(IllegalArgumentException.class, ()->
				new ACMotor(MotorProperties.Type.AC2P, 208,
						Horsepower.HP_15, VoltageSystemAC.v208_1ph_2w));

		//no 1φ 115v 3HP motors on NEC-430.249 (wrong motorHorsepower)
		assertThrows(IllegalArgumentException.class, ()->
				new ACMotor(MotorProperties.Type.AC2P, 115,
						Horsepower.HP_3, VoltageSystemAC.v208_1ph_2w));

		//no 3φ 90v motors on NEC-430.250 (wrong voltage system voltage value)
		assertThrows(IllegalArgumentException.class, ()->
				new ACMotor(MotorProperties.Type.AC3P, 115,
						Horsepower.HP_2,
						VoltageSystemAC.v_other.setValues(90, 3,3)));

		//no 3φ 90v motors on NEC-430.250 (wrong voltage system phases value)
		assertThrows(IllegalArgumentException.class, ()->
				new ACMotor(MotorProperties.Type.AC3P, 115,
						Horsepower.HP_2,
						VoltageSystemAC.v_other.setValues(90, 1,3)));

		//no 3φ 90v motors on NEC-430.250 (wrong voltage system phases value)
		assertThrows(IllegalArgumentException.class, ()->
				new ACMotor(MotorProperties.Type.AC3P, 115,
						Horsepower.HP_2,
						VoltageSystemAC.v_other.setValues(90, 1,3)));
		//----------------------------------------------------------------------
		//case 2
		assertDoesNotThrow(()->
				new ACMotor(MotorProperties.Type.AC1P, 120,
						Horsepower.HP_1$4, VoltageSystemAC.v120_1ph_2w));


		assertDoesNotThrow(()->
				new ACMotor(MotorProperties.Type.AC1P, 110,
						Horsepower.HP_1$4, VoltageSystemAC.v120_1ph_2w));


		assertDoesNotThrow(()->
				new ACMotor(MotorProperties.Type.AC2P, 230,
						Horsepower.HP_15, VoltageSystemAC.v240_1ph_3w));

		assertDoesNotThrow(()->
				new ACMotor(MotorProperties.Type.AC2P, 115,
						Horsepower.HP_2, VoltageSystemAC.v120_1ph_2w));

		assertDoesNotThrow(()->
				new ACMotor(MotorProperties.Type.AC3P, 115,
						Horsepower.HP_2,
						VoltageSystemAC.v_other.setValues(115, 3,3)));


		assertDoesNotThrow(()->
				new ACMotor(MotorProperties.Type.AC3P, 115,
						Horsepower.HP_2,
						VoltageSystemAC.v_other.setValues(115, 3,3)));

		assertDoesNotThrow(()->
				new ACMotor(MotorProperties.Type.AC3P, 600,
						Horsepower.HP_2,
						VoltageSystemAC.v_other.setValues(600, 3,3)));
	}

	@Test
	void getMotorVoltage() {
		assertEquals(115,new ACMotor(MotorProperties.Type.AC1P, 110,
				Horsepower.HP_1$6, VoltageSystemAC.v120_1ph_2w).getMotorVoltage());

		assertEquals(115,new ACMotor(MotorProperties.Type.AC1P, 115,
				Horsepower.HP_1$6, VoltageSystemAC.v120_1ph_2w).getMotorVoltage());

		assertEquals(115,new ACMotor(MotorProperties.Type.AC1P, 120,
				Horsepower.HP_1$6, VoltageSystemAC.v120_1ph_2w).getMotorVoltage());

		assertEquals(200,new ACMotor(MotorProperties.Type.AC1P, 200,
				Horsepower.HP_1$6,
				VoltageSystemAC.v_other.setValues(200,1, 2)).getMotorVoltage());

		assertEquals(208,new ACMotor(MotorProperties.Type.AC1P, 208,
				Horsepower.HP_1$6, VoltageSystemAC.v208_1ph_2w).getMotorVoltage());

		assertEquals(230,new ACMotor(MotorProperties.Type.AC1P, 220,
				Horsepower.HP_1$6, VoltageSystemAC.v240_1ph_2w).getMotorVoltage());

		assertEquals(230,new ACMotor(MotorProperties.Type.AC1P, 240,
				Horsepower.HP_1$6, VoltageSystemAC.v240_1ph_2w).getMotorVoltage());
	}

	@Test
	void getHorsepower() {
		assertEquals(Horsepower.HP_1$6,new ACMotor(MotorProperties.Type.AC1P, 110,
				Horsepower.HP_1$6, VoltageSystemAC.v120_1ph_2w).getHorsepower());

		assertEquals(Horsepower.HP_1$4,new ACMotor(MotorProperties.Type.AC1P
				, 115,
				Horsepower.HP_1$4, VoltageSystemAC.v120_1ph_2w).getHorsepower());

		assertEquals(Horsepower.HP_3$4,new ACMotor(MotorProperties.Type.AC1P
				, 120,
				Horsepower.HP_3$4, VoltageSystemAC.v120_1ph_2w).getHorsepower());

		assertEquals(Horsepower.HP_1,new ACMotor(MotorProperties.Type.AC1P, 200,
				Horsepower.HP_1, VoltageSystemAC.v_other.setValues (200,1,2)).getHorsepower());

		assertEquals(Horsepower.HP_3,new ACMotor(MotorProperties.Type.AC1P
				, 208,
				Horsepower.HP_3, VoltageSystemAC.v208_1ph_2w).getHorsepower());

		assertEquals(Horsepower.HP_7_1$2,new ACMotor(MotorProperties.Type.AC1P,
				220,
				Horsepower.HP_7_1$2, VoltageSystemAC.v240_1ph_2w).getHorsepower());

		assertEquals(Horsepower.HP_10,new ACMotor(MotorProperties.Type.AC1P
				, 240,
				Horsepower.HP_10, VoltageSystemAC.v240_1ph_2w).getHorsepower());
	}

	@Test
	void getMCA() {
		assertEquals(4.4*1.25, new ACMotor(MotorProperties.Type.AC1P,110,
				Horsepower.HP_1$6, VoltageSystemAC.v120_1ph_2w).getMCA());

		assertEquals(50*1.25, new ACMotor(MotorProperties.Type.AC1P,220,
				Horsepower.HP_10, VoltageSystemAC.v240_1ph_2w).getMCA());

		assertEquals(1.2*1.25, new ACMotor(MotorProperties.Type.AC2P,480,
				Horsepower.HP_3$4, VoltageSystemAC.v480_1ph_2w).getMCA());

		assertEquals(416.0*1.25, new ACMotor(MotorProperties.Type.AC2P,240,
				Horsepower.HP_200, VoltageSystemAC.v240_1ph_2w).getMCA());

		assertEquals(2.2*1.25, new ACMotor(MotorProperties.Type.AC3P,240,
				Horsepower.HP_1$2, VoltageSystemAC.v240_3ph_3w).getMCA());

		assertEquals(590.0*1.25, new ACMotor(MotorProperties.Type.AC3P,480,
				Horsepower.HP_500, VoltageSystemAC.v480_3ph_3w).getMCA());

		assertEquals(53.0*1.25, new ACMotor(MotorProperties.Type.AC3PS,220,
				Horsepower.HP_25, VoltageSystemAC.v240_3ph_3w).getMCA());

		assertEquals(201.0*1.25, new ACMotor(MotorProperties.Type.AC3PS,460,
				Horsepower.HP_200, VoltageSystemAC.v480_3ph_3w).getMCA());
	}

	@Test
	void getNominalCurrent() {
		assertEquals(4.4, new ACMotor(MotorProperties.Type.AC1P,110,
				Horsepower.HP_1$6, VoltageSystemAC.v120_1ph_2w).getNominalCurrent());

		assertEquals(50, new ACMotor(MotorProperties.Type.AC1P,220,
				Horsepower.HP_10, VoltageSystemAC.v240_1ph_2w).getNominalCurrent());

		assertEquals(1.2, new ACMotor(MotorProperties.Type.AC2P,480,
				Horsepower.HP_3$4, VoltageSystemAC.v480_1ph_2w).getNominalCurrent());

		assertEquals(416.0, new ACMotor(MotorProperties.Type.AC2P,240,
				Horsepower.HP_200, VoltageSystemAC.v240_1ph_3w).getNominalCurrent());

		assertEquals(2.2, new ACMotor(MotorProperties.Type.AC3P,240,
				Horsepower.HP_1$2, VoltageSystemAC.v240_3ph_4w).getNominalCurrent());

		assertEquals(590.0, new ACMotor(MotorProperties.Type.AC3P,480,
				Horsepower.HP_500, VoltageSystemAC.v480_3ph_4w).getNominalCurrent());

		assertEquals(53.0, new ACMotor(MotorProperties.Type.AC3PS,220,
				Horsepower.HP_25, VoltageSystemAC.v240_3ph_4w).getNominalCurrent());

		assertEquals(201.0, new ACMotor(MotorProperties.Type.AC3PS,460,
				Horsepower.HP_200, VoltageSystemAC.v480_3ph_3w).getNominalCurrent());
	}

	@Test
	void getRequiredCircuitType() {
		ACMotor acMotor = new ACMotor(MotorProperties.Type.AC1P, 110, Horsepower.HP_1,
				VoltageSystemAC.v120_1ph_2w);
		assertEquals(Circuit.CircuitType.DEDICATED_BRANCH,
				acMotor.getRequiredCircuitType());

		acMotor.setCircuitType(Circuit.CircuitType.FEEDER);
		assertEquals(Circuit.CircuitType.FEEDER,
				acMotor.getRequiredCircuitType());
	}

	@Test
	void getMaxOCPDRating() {
		//case 1
		acMotor = new ACMotor(MotorProperties.Type.AC1P,115,
				Horsepower.HP_3$4, VoltageSystemAC.v120_1ph_2w);
		assertEquals(13.8, acMotor.getNominalCurrent());
		assertEquals(OCPD.Type.INVERSE_TIME_BREAKER, acMotor.getOcdpType());
		assertEquals(35, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.INSTANTANEOUS_TRIP_BREAKER);
		assertEquals(110, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.DUAL_ELEMENT_TIME_DELAY_FUSE);
		assertEquals(25, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.NON_TIME_DELAY_FUSE);
		assertEquals(40, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.NON_TIME_DELAY_FUSE);
		acMotor.setDesignLetter(DESIGN_B);
		assertEquals(40, acMotor.getMaxOCPDRating());

		//case 2
		acMotor = new ACMotor(MotorProperties.Type.AC2P,230,
				Horsepower.HP_1, VoltageSystemAC.v240_1ph_2w);
		assertEquals(3.2, acMotor.getNominalCurrent());
		assertEquals(OCPD.Type.INVERSE_TIME_BREAKER, acMotor.getOcdpType());
		assertEquals(15, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.INSTANTANEOUS_TRIP_BREAKER);
		assertEquals(25, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.DUAL_ELEMENT_TIME_DELAY_FUSE);
		assertEquals(15, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.NON_TIME_DELAY_FUSE);
		assertEquals(15, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.NON_TIME_DELAY_FUSE);
		acMotor.setDesignLetter(DESIGN_B);
		assertEquals(15, acMotor.getMaxOCPDRating());

		//case 3
		acMotor = new ACMotor(MotorProperties.Type.AC2P,480,
				Horsepower.HP_200, VoltageSystemAC.v480_1ph_2w);
		assertEquals(208.0, acMotor.getNominalCurrent());
		assertEquals(OCPD.Type.INVERSE_TIME_BREAKER, acMotor.getOcdpType());
		assertEquals(500, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.INSTANTANEOUS_TRIP_BREAKER);
		assertEquals(1600, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.DUAL_ELEMENT_TIME_DELAY_FUSE);
		assertEquals(350, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.NON_TIME_DELAY_FUSE);
		assertEquals(600, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.NON_TIME_DELAY_FUSE);
		acMotor.setDesignLetter(DESIGN_B);
		assertEquals(600, acMotor.getMaxOCPDRating());

		//case 4
		acMotor = new ACMotor(MotorProperties.Type.AC3P,480,
				Horsepower.HP_40, VoltageSystemAC.v480_3ph_4w);
		assertEquals(52.0, acMotor.getNominalCurrent());
		assertEquals(OCPD.Type.INVERSE_TIME_BREAKER, acMotor.getOcdpType());
		assertEquals(125, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.INSTANTANEOUS_TRIP_BREAKER);
		assertEquals(400, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.DUAL_ELEMENT_TIME_DELAY_FUSE);
		assertEquals(90, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.NON_TIME_DELAY_FUSE);
		assertEquals(150, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.NON_TIME_DELAY_FUSE);
		acMotor.setDesignLetter(DESIGN_B);
		assertEquals(150, acMotor.getMaxOCPDRating());

		//case 5
		acMotor = new ACMotor(MotorProperties.Type.AC3PS,600,
				Horsepower.HP_25, VoltageSystemAC.v_other.setValues(600,3,3));
		assertEquals(21.0, acMotor.getNominalCurrent());
		assertEquals(OCPD.Type.INVERSE_TIME_BREAKER, acMotor.getOcdpType());
		assertEquals(50, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.INSTANTANEOUS_TRIP_BREAKER);
		assertEquals(175, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.DUAL_ELEMENT_TIME_DELAY_FUSE);
		assertEquals(35, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.NON_TIME_DELAY_FUSE);
		assertEquals(60, acMotor.getMaxOCPDRating());

		acMotor.setOcdpType(OCPD.Type.INSTANTANEOUS_TRIP_BREAKER);
		acMotor.setDesignLetter(DESIGN_B);
		assertEquals(225, acMotor.getMaxOCPDRating());
	}

	@Test
	void getDSRating() {
		acMotor = new ACMotor(MotorProperties.Type.AC1P,115,
				Horsepower.HP_1_1$2, VoltageSystemAC.v120_1ph_2w);
		assertEquals(30, acMotor.getDSRating());
		assertEquals(20*120, acMotor.getVoltAmperes());
		assertEquals(20*120*0.8, acMotor.getWatts());

		acMotor = new ACMotor(MotorProperties.Type.AC3P,440,
				Horsepower.HP_500, VoltageSystemAC.v480_3ph_3w);
		assertEquals(800, acMotor.getDSRating());
		assertEquals(590*480*Math.sqrt(3), acMotor.getVoltAmperes());
		assertEquals(590*480*Math.sqrt(3)*0.8, acMotor.getWatts());
	}

	@Test
	void getOverloadRating() {
	}

	@Test
	void isNonlinear() {
	}


}