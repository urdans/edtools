package eecalcs.loads;

import eecalcs.circuits.OCPD;

import java.util.HashMap;
import java.util.Map;

import static eecalcs.loads.Horsepower.*;

public class MotorProperties {
	/**FLC in amperes for DC motors, NEC-430.247*/
	private static final Map<Horsepower, Double> VDC90;
	private static final Map<Horsepower, Double> VDC120;
	private static final Map<Horsepower, Double> VDC180;
	private static final Map<Horsepower, Double> VDC240;
	private static final Map<Horsepower, Double> VDC500;
	private static final Map<Horsepower, Double> VDC550;
	private static final Map<Integer, Map<Horsepower, Double>> DCMotor;
	/**FLC in amperes for single-phase AC motors, NEC-430.248*/
	private static final Map<Horsepower, Double> VAC_1p_115;
	private static final Map<Horsepower, Double> VAC_1p_200;
	private static final Map<Horsepower, Double> VAC_1p_208;
	private static final Map<Horsepower, Double> VAC_1p_230;
	private static final Map<Integer, Map<Horsepower, Double>> AC1PMotor;
	/**FLC in amperes for two-phase AC motors (4-wire), NEC 430.249*/
	private static final Map<Horsepower, Double> VAC_2p_115;
	private static final Map<Horsepower, Double> VAC_2p_230;
	private static final Map<Horsepower, Double> VAC_2p_460;
	private static final Map<Horsepower, Double> VAC_2p_575;
	private static final Map<Horsepower, Double> VAC_2p_2300;
	private static final Map<Integer, Map<Horsepower, Double>> AC2PMotor;
	/**FLC in amperes for three-phase AC motors, NEC 430.250*/
	private static final Map<Horsepower, Double> VAC_3p_115;
	private static final Map<Horsepower, Double> VAC_3p_200;
	private static final Map<Horsepower, Double> VAC_3p_208;
	private static final Map<Horsepower, Double> VAC_3p_230;
	private static final Map<Horsepower, Double> VAC_3p_460;
	private static final Map<Horsepower, Double> VAC_3p_575;
	private static final Map<Horsepower, Double> VAC_3p_2300;
	private static final Map<Integer, Map<Horsepower, Double>> AC3PMotor;
	/**FLC in amperes for three-phase synchronous AC motors, NEC 430.250*/
	private static final Map<Horsepower, Double> VAC_S_230;
	private static final Map<Horsepower, Double> VAC_S_460;
	private static final Map<Horsepower, Double> VAC_S_575;
	private static final Map<Horsepower, Double> VAC_S_2300;
	private static final Map<Integer, Map<Horsepower, Double>> AC3PSMotor;

	static {
		//region DC NEC-430.247
		VDC90 = new HashMap<>();
		VDC90.put(HP_1$4, 4.0);
		VDC90.put(HP_1$3, 5.2);
		VDC90.put(HP_1$2, 6.8);
		VDC90.put(HP_3$4, 9.6);
		VDC90.put(HP_1, 12.2);

		VDC120 = new HashMap<>();
		VDC120.put(HP_1$4, 3.1);
		VDC120.put(HP_1$3, 4.1);
		VDC120.put(HP_1$2, 5.4);
		VDC120.put(HP_3$4, 7.6);
		VDC120.put(HP_1, 9.5);
		VDC120.put(HP_1_1$2, 13.2);
		VDC120.put(HP_2, 17.0);
		VDC120.put(HP_3, 25.0);
		VDC120.put(HP_5, 40.0);
		VDC120.put(HP_7_1$2, 58.0);
		VDC120.put(HP_10, 76.0);

		VDC180 = new HashMap<>();
		VDC180.put(HP_1$4, 2.0);
		VDC180.put(HP_1$3, 2.6);
		VDC180.put(HP_1$2, 3.4);
		VDC180.put(HP_3$4, 4.8);
		VDC180.put(HP_1, 6.1);
		VDC180.put(HP_1_1$2, 8.3);
		VDC180.put(HP_2, 10.8);
		VDC180.put(HP_3, 16.0);
		VDC180.put(HP_5, 27.0);

		VDC240 = new HashMap<>();
		VDC240.put(HP_1$4, 1.6);
		VDC240.put(HP_1$3, 2.0);
		VDC240.put(HP_1$2, 2.7);
		VDC240.put(HP_3$4, 3.8);
		VDC240.put(HP_1, 4.7);
		VDC240.put(HP_1_1$2, 6.6);
		VDC240.put(HP_2, 8.5);
		VDC240.put(HP_3, 12.2);
		VDC240.put(HP_5, 20.0);
		VDC240.put(HP_7_1$2, 29.0);
		VDC240.put(HP_10, 38.0);
		VDC240.put(HP_15, 55.0);
		VDC240.put(HP_20, 72.0);
		VDC240.put(HP_25, 89.0);
		VDC240.put(HP_30, 106.0);
		VDC240.put(HP_40, 140.0);
		VDC240.put(HP_50, 173.0);
		VDC240.put(HP_60, 206.0);
		VDC240.put(HP_75, 255.0);
		VDC240.put(HP_100, 341.0);
		VDC240.put(HP_125, 425.0);
		VDC240.put(HP_150, 506.0);
		VDC240.put(HP_200, 675.0);

		VDC500 = new HashMap<>();
		VDC500.put(HP_7_1$2, 13.6);
		VDC500.put(HP_10, 18.0);
		VDC500.put(HP_15, 27.0);
		VDC500.put(HP_20, 34.0);
		VDC500.put(HP_25, 43.0);
		VDC500.put(HP_30, 51.0);
		VDC500.put(HP_40, 67.0);
		VDC500.put(HP_50, 83.0);
		VDC500.put(HP_60, 99.0);
		VDC500.put(HP_75, 123.0);
		VDC500.put(HP_100, 164.0);
		VDC500.put(HP_125, 205.0);
		VDC500.put(HP_150, 246.0);
		VDC500.put(HP_200, 330.0);

		VDC550 = new HashMap<>();
		VDC550.put(HP_7_1$2, 12.2);
		VDC550.put(HP_10, 16.0);
		VDC550.put(HP_15, 24.0);
		VDC550.put(HP_20, 31.0);
		VDC550.put(HP_25, 38.0);
		VDC550.put(HP_30, 46.0);
		VDC550.put(HP_40, 61.0);
		VDC550.put(HP_50, 75.0);
		VDC550.put(HP_60, 90.0);
		VDC550.put(HP_75, 111.0);
		VDC550.put(HP_100, 148.0);
		VDC550.put(HP_125, 185.0);
		VDC550.put(HP_150, 222.0);
		VDC550.put(HP_200, 294.0);

		DCMotor = new HashMap<>();
		DCMotor.put(90, VDC90);
		DCMotor.put(120, VDC120);
		DCMotor.put(180, VDC180);
		DCMotor.put(240, VDC240);
		DCMotor.put(500, VDC500);
		DCMotor.put(550, VDC550);
		//endregion

		//region AC 1φ NEC 430.248
		VAC_1p_115 = new HashMap<>();
		VAC_1p_115.put(HP_1$6, 4.4);
		VAC_1p_115.put(HP_1$4, 5.8);
		VAC_1p_115.put(HP_1$3, 7.2);
		VAC_1p_115.put(HP_1$2, 9.8);
		VAC_1p_115.put(HP_3$4, 13.8);
		VAC_1p_115.put(HP_1, 16.0);
		VAC_1p_115.put(HP_1_1$2, 20.0);
		VAC_1p_115.put(HP_2, 24.0);
		VAC_1p_115.put(HP_3, 34.0);
		VAC_1p_115.put(HP_5, 56.0);
		VAC_1p_115.put(HP_7_1$2, 80.0);
		VAC_1p_115.put(HP_10, 100.0);

		VAC_1p_200 = new HashMap<>();
		VAC_1p_200.put(HP_1$6, 2.5);
		VAC_1p_200.put(HP_1$4, 3.3);
		VAC_1p_200.put(HP_1$3, 4.1);
		VAC_1p_200.put(HP_1$2, 5.6);
		VAC_1p_200.put(HP_3$4, 7.9);
		VAC_1p_200.put(HP_1, 9.2);
		VAC_1p_200.put(HP_1_1$2, 11.5);
		VAC_1p_200.put(HP_2, 13.8);
		VAC_1p_200.put(HP_3, 19.6);
		VAC_1p_200.put(HP_5, 32.2);
		VAC_1p_200.put(HP_7_1$2, 46.0);
		VAC_1p_200.put(HP_10, 57.5);

		VAC_1p_208 = new HashMap<>();
		VAC_1p_208.put(HP_1$6, 2.4);
		VAC_1p_208.put(HP_1$4, 3.2);
		VAC_1p_208.put(HP_1$3, 4.0);
		VAC_1p_208.put(HP_1$2, 5.4);
		VAC_1p_208.put(HP_3$4, 7.6);
		VAC_1p_208.put(HP_1, 8.8);
		VAC_1p_208.put(HP_1_1$2, 11.0);
		VAC_1p_208.put(HP_2, 13.2);
		VAC_1p_208.put(HP_3, 18.7);
		VAC_1p_208.put(HP_5, 30.8);
		VAC_1p_208.put(HP_7_1$2, 44.0);
		VAC_1p_208.put(HP_10, 55.0);

		VAC_1p_230 = new HashMap<>();
		VAC_1p_230.put(HP_1$6, 2.2);
		VAC_1p_230.put(HP_1$4, 2.9);
		VAC_1p_230.put(HP_1$3, 3.6);
		VAC_1p_230.put(HP_1$2, 4.9);
		VAC_1p_230.put(HP_3$4, 6.9);
		VAC_1p_230.put(HP_1, 8.0);
		VAC_1p_230.put(HP_1_1$2, 10.0);
		VAC_1p_230.put(HP_2, 12.0);
		VAC_1p_230.put(HP_3, 17.0);
		VAC_1p_230.put(HP_5, 28.0);
		VAC_1p_230.put(HP_7_1$2, 40.0);
		VAC_1p_230.put(HP_10, 50.0);

		AC1PMotor = new HashMap<>();
		AC1PMotor.put(115, VAC_1p_115);
		AC1PMotor.put(200, VAC_1p_200);
		AC1PMotor.put(208, VAC_1p_208);
		AC1PMotor.put(230, VAC_1p_230);
		//endregion

		//region AC 2φ NEC 430.249
		VAC_2p_115 = new HashMap<>();
		VAC_2p_115.put(HP_1$2, 4.0);
		VAC_2p_115.put(HP_3$4, 4.8);
		VAC_2p_115.put(HP_1, 6.4);
		VAC_2p_115.put(HP_1_1$2, 9.0);
		VAC_2p_115.put(HP_2, 11.8);

		VAC_2p_230 = new HashMap<>();
		VAC_2p_230.put(HP_1$2, 2.0);
		VAC_2p_230.put(HP_3$4, 2.4);
		VAC_2p_230.put(HP_1, 3.2);
		VAC_2p_230.put(HP_1_1$2, 4.5);
		VAC_2p_230.put(HP_2, 5.9);
		VAC_2p_230.put(HP_3, 8.3);
		VAC_2p_230.put(HP_5, 13.2);
		VAC_2p_230.put(HP_7_1$2, 19.0);
		VAC_2p_230.put(HP_10, 24.0);
		VAC_2p_230.put(HP_15, 36.0);
		VAC_2p_230.put(HP_20, 47.0);
		VAC_2p_230.put(HP_25, 59.0);
		VAC_2p_230.put(HP_30, 69.0);
		VAC_2p_230.put(HP_40, 90.0);
		VAC_2p_230.put(HP_50, 113.0);
		VAC_2p_230.put(HP_60, 133.0);
		VAC_2p_230.put(HP_75, 166.0);
		VAC_2p_230.put(HP_100, 218.0);
		VAC_2p_230.put(HP_125, 270.0);
		VAC_2p_230.put(HP_150, 312.0);
		VAC_2p_230.put(HP_200, 416.0);

		VAC_2p_460 = new HashMap<>();
		VAC_2p_460.put(HP_1$2, 1.0);
		VAC_2p_460.put(HP_3$4, 1.2);
		VAC_2p_460.put(HP_1, 1.6);
		VAC_2p_460.put(HP_1_1$2, 2.3);
		VAC_2p_460.put(HP_2, 3.0);
		VAC_2p_460.put(HP_3, 4.2);
		VAC_2p_460.put(HP_5, 6.6);
		VAC_2p_460.put(HP_7_1$2, 9.0);
		VAC_2p_460.put(HP_10, 12.0);
		VAC_2p_460.put(HP_15, 18.0);
		VAC_2p_460.put(HP_20, 23.0);
		VAC_2p_460.put(HP_25, 29.0);
		VAC_2p_460.put(HP_30, 35.0);
		VAC_2p_460.put(HP_40, 45.0);
		VAC_2p_460.put(HP_50, 56.0);
		VAC_2p_460.put(HP_60, 67.0);
		VAC_2p_460.put(HP_75, 83.0);
		VAC_2p_460.put(HP_100, 109.0);
		VAC_2p_460.put(HP_125, 135.0);
		VAC_2p_460.put(HP_150, 156.0);
		VAC_2p_460.put(HP_200, 208.0);

		VAC_2p_575 = new HashMap<>();
		VAC_2p_575.put(HP_1$2, .8);
		VAC_2p_575.put(HP_3$4, 1.0);
		VAC_2p_575.put(HP_1, 1.3);
		VAC_2p_575.put(HP_1_1$2, 1.8);
		VAC_2p_575.put(HP_2, 2.4);
		VAC_2p_575.put(HP_3, 3.3);
		VAC_2p_575.put(HP_5, 5.3);
		VAC_2p_575.put(HP_7_1$2, 8.0);
		VAC_2p_575.put(HP_10, 10.0);
		VAC_2p_575.put(HP_15, 14.0);
		VAC_2p_575.put(HP_20, 19.0);
		VAC_2p_575.put(HP_25, 24.0);
		VAC_2p_575.put(HP_30, 28.0);
		VAC_2p_575.put(HP_40, 36.0);
		VAC_2p_575.put(HP_50, 45.0);
		VAC_2p_575.put(HP_60, 53.0);
		VAC_2p_575.put(HP_75, 66.0);
		VAC_2p_575.put(HP_100, 87.0);
		VAC_2p_575.put(HP_125, 108.0);
		VAC_2p_575.put(HP_150, 125.0);
		VAC_2p_575.put(HP_200, 167.0);

		VAC_2p_2300 = new HashMap<>();
		VAC_2p_2300.put(HP_60, 14.0);
		VAC_2p_2300.put(HP_75, 18.0);
		VAC_2p_2300.put(HP_100, 23.0);
		VAC_2p_2300.put(HP_125, 28.0);
		VAC_2p_2300.put(HP_150, 32.0);
		VAC_2p_2300.put(HP_200, 43.0);

		AC2PMotor = new HashMap<>();
		AC2PMotor.put(115, VAC_2p_115);
		AC2PMotor.put(230, VAC_2p_230);
		AC2PMotor.put(460, VAC_2p_460);
		AC2PMotor.put(575, VAC_2p_575);
		AC2PMotor.put(2300, VAC_2p_2300);
		//endregion

		//region AC 3φ NEC 430.250
		VAC_3p_115 = new HashMap<>();
		VAC_3p_115.put(HP_1$2, 4.4);
		VAC_3p_115.put(HP_3$4, 6.4);
		VAC_3p_115.put(HP_1, 8.4);
		VAC_3p_115.put(HP_1_1$2, 12.0);
		VAC_3p_115.put(HP_2, 13.6);

		VAC_3p_200 = new HashMap<>();
		VAC_3p_200.put(HP_1$2, 2.5);
		VAC_3p_200.put(HP_3$4, 3.7);
		VAC_3p_200.put(HP_1, 4.8);
		VAC_3p_200.put(HP_1_1$2, 6.9);
		VAC_3p_200.put(HP_2, 7.8);
		VAC_3p_200.put(HP_3, 11.0);
		VAC_3p_200.put(HP_5, 17.5);
		VAC_3p_200.put(HP_7_1$2, 25.3);
		VAC_3p_200.put(HP_10, 32.2);
		VAC_3p_200.put(HP_15, 48.3);
		VAC_3p_200.put(HP_20, 62.1);
		VAC_3p_200.put(HP_25, 78.2);
		VAC_3p_200.put(HP_30, 92.0);
		VAC_3p_200.put(HP_40, 120.0);
		VAC_3p_200.put(HP_50, 150.0);
		VAC_3p_200.put(HP_60, 177.0);
		VAC_3p_200.put(HP_75, 221.0);
		VAC_3p_200.put(HP_100, 285.0);
		VAC_3p_200.put(HP_125, 359.0);
		VAC_3p_200.put(HP_150, 414.0);
		VAC_3p_200.put(HP_200, 552.0);

		VAC_3p_208 = new HashMap<>();
		VAC_3p_208.put(HP_1$2, 2.4);
		VAC_3p_208.put(HP_3$4, 3.5);
		VAC_3p_208.put(HP_1, 4.6);
		VAC_3p_208.put(HP_1_1$2, 6.6);
		VAC_3p_208.put(HP_2, 7.5);
		VAC_3p_208.put(HP_3, 10.6);
		VAC_3p_208.put(HP_5, 16.7);
		VAC_3p_208.put(HP_7_1$2, 24.2);
		VAC_3p_208.put(HP_10, 30.8);
		VAC_3p_208.put(HP_15, 46.2);
		VAC_3p_208.put(HP_20, 59.4);
		VAC_3p_208.put(HP_25, 74.8);
		VAC_3p_208.put(HP_30, 88.0);
		VAC_3p_208.put(HP_40, 114.0);
		VAC_3p_208.put(HP_50, 143.0);
		VAC_3p_208.put(HP_60, 169.0);
		VAC_3p_208.put(HP_75, 211.0);
		VAC_3p_208.put(HP_100, 273.0);
		VAC_3p_208.put(HP_125, 343.0);
		VAC_3p_208.put(HP_150, 396.0);
		VAC_3p_208.put(HP_200, 528.0);

		VAC_3p_230 = new HashMap<>();
		VAC_3p_230.put(HP_1$2, 2.2);
		VAC_3p_230.put(HP_3$4, 3.2);
		VAC_3p_230.put(HP_1, 4.2);
		VAC_3p_230.put(HP_1_1$2, 6.0);
		VAC_3p_230.put(HP_2, 6.8);
		VAC_3p_230.put(HP_3, 9.6);
		VAC_3p_230.put(HP_5, 15.2);
		VAC_3p_230.put(HP_7_1$2, 22.0);
		VAC_3p_230.put(HP_10, 28.0);
		VAC_3p_230.put(HP_15, 42.0);
		VAC_3p_230.put(HP_20, 54.0);
		VAC_3p_230.put(HP_25, 68.0);
		VAC_3p_230.put(HP_30, 80.0);
		VAC_3p_230.put(HP_40, 104.0);
		VAC_3p_230.put(HP_50, 130.0);
		VAC_3p_230.put(HP_60, 154.0);
		VAC_3p_230.put(HP_75, 192.0);
		VAC_3p_230.put(HP_100, 248.0);
		VAC_3p_230.put(HP_125, 312.0);
		VAC_3p_230.put(HP_150, 360.0);
		VAC_3p_230.put(HP_200, 480.0);

		VAC_3p_460 = new HashMap<>();
		VAC_3p_460.put(HP_1$2, 1.1);
		VAC_3p_460.put(HP_3$4, 1.6);
		VAC_3p_460.put(HP_1, 2.1);
		VAC_3p_460.put(HP_1_1$2, 3.0);
		VAC_3p_460.put(HP_2, 3.4);
		VAC_3p_460.put(HP_3, 4.8);
		VAC_3p_460.put(HP_5, 7.6);
		VAC_3p_460.put(HP_7_1$2, 11.0);
		VAC_3p_460.put(HP_10, 14.0);
		VAC_3p_460.put(HP_15, 21.0);
		VAC_3p_460.put(HP_20, 27.0);
		VAC_3p_460.put(HP_25, 34.0);
		VAC_3p_460.put(HP_30, 40.0);
		VAC_3p_460.put(HP_40, 52.0);
		VAC_3p_460.put(HP_50, 65.0);
		VAC_3p_460.put(HP_60, 77.0);
		VAC_3p_460.put(HP_75, 96.0);
		VAC_3p_460.put(HP_100, 124.0);
		VAC_3p_460.put(HP_125, 156.0);
		VAC_3p_460.put(HP_150, 180.0);
		VAC_3p_460.put(HP_200, 240.0);
		VAC_3p_460.put(HP_250, 302.0);
		VAC_3p_460.put(HP_300, 361.0);
		VAC_3p_460.put(HP_350, 414.0);
		VAC_3p_460.put(HP_400, 477.0);
		VAC_3p_460.put(HP_450, 515.0);
		VAC_3p_460.put(HP_500, 590.0);

		VAC_3p_575 = new HashMap<>();
		VAC_3p_575.put(HP_1$2, .9);
		VAC_3p_575.put(HP_3$4, 1.3);
		VAC_3p_575.put(HP_1, 1.7);
		VAC_3p_575.put(HP_1_1$2, 2.4);
		VAC_3p_575.put(HP_2, 2.7);
		VAC_3p_575.put(HP_3, 3.9);
		VAC_3p_575.put(HP_5, 6.1);
		VAC_3p_575.put(HP_7_1$2, 9.0);
		VAC_3p_575.put(HP_10, 11.0);
		VAC_3p_575.put(HP_15, 17.0);
		VAC_3p_575.put(HP_20, 22.0);
		VAC_3p_575.put(HP_25, 27.0);
		VAC_3p_575.put(HP_30, 32.0);
		VAC_3p_575.put(HP_40, 41.0);
		VAC_3p_575.put(HP_50, 52.0);
		VAC_3p_575.put(HP_60, 62.0);
		VAC_3p_575.put(HP_75, 77.0);
		VAC_3p_575.put(HP_100, 99.0);
		VAC_3p_575.put(HP_125, 125.0);
		VAC_3p_575.put(HP_150, 144.0);
		VAC_3p_575.put(HP_200, 192.0);
		VAC_3p_575.put(HP_250, 242.0);
		VAC_3p_575.put(HP_300, 289.0);
		VAC_3p_575.put(HP_350, 336.0);
		VAC_3p_575.put(HP_400, 382.0);
		VAC_3p_575.put(HP_450, 412.0);
		VAC_3p_575.put(HP_500, 472.0);

		VAC_3p_2300 = new HashMap<>();
		VAC_3p_2300.put(HP_60, 16.0);
		VAC_3p_2300.put(HP_75, 20.0);
		VAC_3p_2300.put(HP_100, 26.0);
		VAC_3p_2300.put(HP_125, 31.0);
		VAC_3p_2300.put(HP_150, 37.0);
		VAC_3p_2300.put(HP_200, 49.0);
		VAC_3p_2300.put(HP_250, 60.0);
		VAC_3p_2300.put(HP_300, 72.0);
		VAC_3p_2300.put(HP_350, 83.0);
		VAC_3p_2300.put(HP_400, 95.0);
		VAC_3p_2300.put(HP_450, 103.0);
		VAC_3p_2300.put(HP_500, 118.0);

		AC3PMotor = new HashMap<>();
		AC3PMotor.put(115, VAC_3p_115);
		AC3PMotor.put(200, VAC_3p_200);
		AC3PMotor.put(208, VAC_3p_208);
		AC3PMotor.put(230, VAC_3p_230);
		AC3PMotor.put(460, VAC_3p_460);
		AC3PMotor.put(575, VAC_3p_575);
		AC3PMotor.put(2300, VAC_3p_2300);
		//endregion

		//region AC 3φ synchronous NEC 430.250
		VAC_S_230 = new HashMap<>();
		VAC_S_230.put(HP_25, 53.0);
		VAC_S_230.put(HP_30, 63.0);
		VAC_S_230.put(HP_40, 83.0);
		VAC_S_230.put(HP_50, 104.0);
		VAC_S_230.put(HP_60, 123.0);
		VAC_S_230.put(HP_75, 155.0);
		VAC_S_230.put(HP_100, 202.0);
		VAC_S_230.put(HP_125, 253.0);
		VAC_S_230.put(HP_150, 302.0);
		VAC_S_230.put(HP_200, 400.0);

		VAC_S_460 = new HashMap<>();
		VAC_S_460.put(HP_25, 26.0);
		VAC_S_460.put(HP_30, 32.0);
		VAC_S_460.put(HP_40, 41.0);
		VAC_S_460.put(HP_50, 52.0);
		VAC_S_460.put(HP_60, 61.0);
		VAC_S_460.put(HP_75, 78.0);
		VAC_S_460.put(HP_100, 101.0);
		VAC_S_460.put(HP_125, 126.0);
		VAC_S_460.put(HP_150, 151.0);
		VAC_S_460.put(HP_200, 201.0);

		VAC_S_575 = new HashMap<>();
		VAC_S_575.put(HP_25, 21.0);
		VAC_S_575.put(HP_30, 26.0);
		VAC_S_575.put(HP_40, 33.0);
		VAC_S_575.put(HP_50, 42.0);
		VAC_S_575.put(HP_60, 49.0);
		VAC_S_575.put(HP_75, 62.0);
		VAC_S_575.put(HP_100, 81.0);
		VAC_S_575.put(HP_125, 101.0);
		VAC_S_575.put(HP_150, 121.0);
		VAC_S_575.put(HP_200, 161.0);

		VAC_S_2300 = new HashMap<>();
		VAC_S_2300.put(HP_60, 12.0);
		VAC_S_2300.put(HP_75, 15.0);
		VAC_S_2300.put(HP_100, 20.0);
		VAC_S_2300.put(HP_125, 25.0);
		VAC_S_2300.put(HP_150, 30.0);
		VAC_S_2300.put(HP_200, 40.0);

		AC3PSMotor = new HashMap<>();
		AC3PSMotor.put(230, VAC_S_230);
		AC3PSMotor.put(460, VAC_S_460);
		AC3PSMotor.put(575, VAC_S_575);
		AC3PSMotor.put(2300, VAC_S_2300);
		//endregion
	}

	/**
	 Enum type for motor as described in NEC table 430.247 to 430.250.
	 Notice, a wound rotor motor is different from a squirrel cage type motor
	 in the fact that the number of wires is:
	 - 4 or 5 for 1φ wound rotor (4 ccc, or 4 ccc + 1 neutral)
	 - 6 or 7 for 3φ wound rotor (6 ccc, or 6 ccc + 1 neutral)
	 */
	public enum Type {
		DC,   //direct-current motors
		AC1P, //1φ AC motors
		AC2P, //1φ induction-type squirrel cage
		AC2P_WR, //1φ induction-type wound rotor AC motor
		AC3P, //3φ induction-type squirrel cage
		AC3P_WR, //3φ induction-type wound rotor AC motor
		AC3PS //3φ synchronous AC motor
	};

	/**Design letters for motor efficiency*/
	public enum DesignLetter {
		/**slip max=5%, high to med starting current, normal locked rotor
		 torque, for fans and pumps*/
		DESIGN_A,
		/**slip max=5%, low starting current, high locked rotor torque, for
		 HVAC compressors and pumps*/
		DESIGN_B,
		/**slip max=5%, low starting current, high locked rotor torque, for
		 conveyors, positive displacement pumps and high inertia-high torque
		 at start*/
		DESIGN_C,
		/**slip max=5~13%, low starting current, very high locked rotor
		 torque, for cranes, hoists and high inertia at start*/
		DESIGN_D
	};

	public static double getFlc(Type type, int voltage,
	                            Horsepower horsepower) {
		Map<Integer, Map<Horsepower, Double>> motorColumn;
		if(type == Type.DC)
			motorColumn = DCMotor;
		else {
			if (type == Type.AC1P)
				motorColumn = AC1PMotor;
			else if (type == Type.AC2P)
				motorColumn = AC2PMotor;
			else if (type == Type.AC3P)
				motorColumn = AC3PMotor;
			else if (type == Type.AC3PS)
				motorColumn = AC3PSMotor;
			else
				return 0;
			voltage = getNormalizedVoltage(voltage);
			if(voltage == 0)
				return 0;
		}
		if(motorColumn.containsKey(voltage)){
			Map<Horsepower, Double> flcMap = motorColumn.get(voltage);
			if(flcMap.containsKey(horsepower))
				return flcMap.get(horsepower);
		}
		return 0;
	}

	/**
	 @return The standard voltage (as defined in NEC tables 430.248, 430.249
	 & 430.250) corresponding to the given voltage. The purpose is to provide
	 a voltage value as in the NEC tables that is compatible with the
	 provided motor voltage rating. A returned value of zero means there is
	 no standard voltage that correspond to the given voltage.
	 @param voltage A voltage value corresponding to a motor nameplate voltage.
	 */
	public static int getNormalizedVoltage(int voltage) {
		voltage = Math.abs(voltage);
		if(voltage >= 110 && voltage <= 120)
			return 115;
		if(voltage == 200 || voltage == 208 || voltage == 2300)
			return voltage;
		else if(voltage >= 220 && voltage <= 240)
			return 230;
		else if(voltage >= 440 && voltage <= 480)
			return 460;
		else if(voltage >= 550 && voltage <= 1000)
			return 575;
		return 0;
	}


	public static double getMaxOCPDRatingFactorPerType(OCPD.Type ocpdType,
           Type motorType, DesignLetter designLetter){
		if(ocpdType == null || motorType == null)
			return 0;
		if(ocpdType == OCPD.Type.NON_TIME_DELAY_FUSE){
			if(motorType == Type.AC2P_WR || motorType == Type.AC3P_WR || motorType == Type.DC)
				return 1.5;
			return 3.0;
		}
		if(ocpdType == OCPD.Type.DUAL_ELEMENT_TIME_DELAY_FUSE){
			if(motorType == Type.AC2P_WR || motorType == Type.AC3P_WR || motorType == Type.DC)
				return 1.5;
			return 1.75;
		}
		if(ocpdType == OCPD.Type.INSTANTANEOUS_TRIP_BREAKER){
			if(motorType == Type.DC)
				return 2.5;
			else if(designLetter == DesignLetter.DESIGN_B)
				return 11.0;
			return 8.0;
		}
		//ocpdType == OCPD.Type.INVERSE_TIME_BREAKER
		if(motorType == Type.AC2P_WR || motorType == Type.AC3P_WR || motorType == Type.DC)
			return 1.5;
		return 2.5;
	}
}
