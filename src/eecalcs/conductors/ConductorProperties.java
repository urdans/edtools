package eecalcs.conductors;

import eecalcs.conduits.OuterMaterial;
import eecalcs.systems.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static eecalcs.systems.Location.WET;

/**
 This class encapsulates static data and methods for the properties of
 conductors as defined in NEC 2020 tables 310.16, 5, 5A, 8 and 9.
 It assumes the conductors are located in a dry location, unless otherwise
 specified. See {@link #getTempRating(Insulation)}
 <p>
 This class groups all the properties related to conductors of a given size,
 when it is isolated, that is, the represented characteristics don't depend upon
 other conditions, like ambient temperature, number of conductor per raceway,
 type of raceway, voltage type (AC or DC), number of phases, special locations,
 load types, etc. */
public class ConductorProperties {
	private static final Properties[] table;
	//region Area of the cross-section of insulated conductors in inch-square, including its insulation.
	/*Table 5*/
	private static final Map<Size, Double> TW;
	private static final Map<Size, Double> RHW;
	private static final Map<Size, Double> THW;
	private static final Map<Size, Double> THWN;
	private static final Map<Size, Double> ZW;
	private static final Map<Size, Double> FEP;
	private static final Map<Size, Double> FEPB;
	private static final Map<Size, Double> RHH;
	private static final Map<Size, Double> RHW2;
	private static final Map<Size, Double> THHN;
	private static final Map<Size, Double> THHW;
	private static final Map<Size, Double> THW2;
	private static final Map<Size, Double> THWN2;
	private static final Map<Size, Double> XHH;
	private static final Map<Size, Double> XHHW;
	private static final Map<Size, Double> XHHW2;
	private static final Map<Size, Double> EMPTY;

	private static final Map<Insulation, Map<Size, Double>> insulatedDimensions;

	/*Table 5A*/
	private static final Map<Size, Double> compactRHH;
	private static final Map<Size, Double> compactRHW;
	private static final Map<Size, Double> compactUSE;
	private static final Map<Size, Double> compactTHW;
	private static final Map<Size, Double> compactTHHW;
	private static final Map<Size, Double> compactTHHN;
	private static final Map<Size, Double> compactXHHW;
	private static final Map<Size, Double> compactBareDimensions;
	private static final Map<Insulation, Map<Size, Double>> compactDimensions;

	private static final Map<TempRating, Insulation[]> insulationTempMap;
	//endregion

	/**
	 Encapsulates the properties for a conductor of a given size in
	 accordance with tables 310.16, 5, 5A, 8 and 9
	 */
	private static class Properties {
		private final Size size;
		private final int areaCM;
		private final double nonMagXL;
		private final double magXL;
		private final double CuAmp60;
		private final double CuAmp75;
		private final double CuAmp90;
		private final double AlAmp60;
		private final double AlAmp75;
		private final double AlAmp90;
		private final double CuResInPVCCond;
		private final double CuResInALCond;
		private final double CuResInSteelCond;
		private final double ALResInPVCCond;
		private final double ALResInALCond;
		private final double ALResInSteelCond;
		private final double CuResDCUncoated;
		private final double CuResDCCoated;
		private final double ALResDC;

		/**
		 @param size The size of the conductor, AWG or KCMIL
		 @param cuAmp60 Ampacity in ampere for CU conductors for 60° C insulation
		 @param cuAmp75 Ampacity for copper conductors for 75° C insulation
		 @param cuAmp90 Ampacity for copper conductors for 90° C insulation
		 @param alAmp60 Ampacity for aluminum conductors for 60° C insulation
		 @param alAmp75 Ampacity for aluminum conductors for 75° C insulation
		 @param alAmp90 Ampacity for aluminum conductors for 90° C insulation
		 @param nonMagXL Reactance in non magnetic conduit
		 @param magXL Reactance in magnetic conduit (steel)
		 @param cuResInPVCCond AC Resistance of CU conductor in PVC conduit
		 @param cuResInALCond AC Resistance of CU conductor in AL conduit
		 @param cuResInSteelCond AC Resistance of CU conductor in steel conduit
		 @param aLResInPVCCond AC Resistance of AL conductor in PVC conduit
		 @param aLResInALCond AC Resistance of AL conductor in PVC conduit
		 @param aLResInSteelCond AC Resistance of AL conductor in steel conduit
		 @param areaCM Area of the conductor in circular mil.
		 @param cuResDCUncoated DC Resistance of uncoated CU conductor
		 @param cuResDCCoated DC Resistance of coated CU conductor
		 @param aLResDC DC Resistance of AL conductor
		 All ampacities are in Amperes.
		 All reactances and resistances are in ohms per 1000 ft.
		 */
		private Properties(Size size, int cuAmp60, int cuAmp75,
		                        int cuAmp90,
		                  int alAmp60, int alAmp75, int alAmp90,
		                  double nonMagXL,
		                  double magXL, double cuResInPVCCond,
		                  double cuResInALCond, double cuResInSteelCond,
		                  double aLResInPVCCond,
		                  double aLResInALCond, double aLResInSteelCond,
		                  int areaCM, double cuResDCUncoated,
		                  double cuResDCCoated,
		                  double aLResDC) {
			this.size = size;
			this.areaCM = areaCM;
			this.nonMagXL = nonMagXL;
			this.magXL = magXL;
			this.CuAmp60 = cuAmp60;
			this.CuAmp75 = cuAmp75;
			this.CuAmp90 = cuAmp90;
			this.AlAmp60 = alAmp60;
			this.AlAmp75 = alAmp75;
			this.AlAmp90 = alAmp90;
			this.CuResInPVCCond = cuResInPVCCond;
			this.CuResInALCond = cuResInALCond;
			this.CuResInSteelCond = cuResInSteelCond;
			this.ALResInPVCCond = aLResInPVCCond;
			this.ALResInALCond = aLResInALCond;
			this.ALResInSteelCond = aLResInSteelCond;
			this.CuResDCUncoated = cuResDCUncoated;
			this.CuResDCCoated = cuResDCCoated;
			this.ALResDC = aLResDC;
		}
	}

	static {
		//region Conductor properties
		table = new Properties[]{
				new Properties(Size.AWG_14, 15, 20, 25, 0, 0, 0, 0.058000,
						0.073000, 3.100000, 3.100000, 3.100000, 4.130600,
						4.130600, 4.130600, 4110, 3.070000, 3.190000,
						5.060000),
				new Properties(Size.AWG_12, 20, 25, 30, 15, 20, 25, 0.054000,
						0.068000, 2.000000, 2.000000, 2.000000, 3.200000,
						3.200000, 3.200000, 6530, 1.930000, 2.010000,
						3.180000),
				new Properties(Size.AWG_10, 30, 35, 40, 25, 30, 35, 0.054000,
						0.068000, 1.200000, 1.200000, 1.200000, 2.000000,
						2.000000, 2.000000, 10380, 1.210000, 1.260000,
						2.000000),
				new Properties(Size.AWG_8, 40, 50, 55, 35, 40, 45, 0.050000,
						0.063000, 0.780000, 0.780000, 0.780000, 1.300000,
						1.300000, 1.300000, 16510, 0.764000, 0.786000,
						1.260000),
				new Properties(Size.AWG_6, 55, 65, 75, 40, 50, 55, 0.051000,
						0.064000, 0.490000, 0.490000, 0.490000, 0.810000,
						0.810000, 0.810000, 26240, 0.491000, 0.510000,
						0.808000),
				new Properties(Size.AWG_4, 70, 85, 95, 55, 65, 75, 0.048000,
						0.060000, 0.310000, 0.310000, 0.310000, 0.510000,
						0.510000, 0.510000, 41740, 0.308000, 0.321000,
						0.508000),
				new Properties(Size.AWG_3, 85, 100, 115, 65, 75, 85, 0.047000,
						0.059000, 0.250000, 0.250000, 0.250000, 0.400000,
						0.410000, 0.400000, 52620, 0.245000, 0.254000,
						0.403000),
				new Properties(Size.AWG_2, 95, 115, 130, 75, 90, 100, 0.045000
						, 0.057000, 0.190000, 0.200000, 0.200000, 0.320000,
						0.320000, 0.320000, 66360, 0.194000, 0.201000,
						0.319000),
				new Properties(Size.AWG_1, 110, 130, 145, 85, 100, 115,
						0.046000, 0.057000, 0.150000, 0.160000, 0.160000,
						0.250000, 0.260000, 0.250000, 83690, 0.154000,
						0.160000, 0.253000),
				new Properties(Size.AWG_1$0, 125, 150, 170, 100, 120, 135,
						0.044000, 0.055000, 0.120000, 0.130000, 0.120000,
						0.200000, 0.210000, 0.200000, 105600, 0.122000,
						0.127000, 0.201000),
				new Properties(Size.AWG_2$0, 145, 175, 195, 115, 135, 150,
						0.043000, 0.054000, 0.100000, 0.100000, 0.100000,
						0.160000, 0.160000, 0.160000, 133100, 0.096700,
						0.101000, 0.159000),
				new Properties(Size.AWG_3$0, 165, 200, 225, 130, 155, 175,
						0.042000, 0.052000, 0.077000, 0.082000, 0.079000,
						0.130000, 0.130000, 0.130000, 167800, 0.076600,
						0.079700, 0.126000),
				new Properties(Size.AWG_4$0, 195, 230, 260, 150, 180, 205,
						0.041000, 0.051000, 0.062000, 0.067000, 0.063000,
						0.100000, 0.110000, 0.100000, 211600, 0.060800,
						0.062600, 0.100000),
				new Properties(Size.KCMIL_250, 215, 255, 290, 170, 205, 230,
						0.041000, 0.052000, 0.052000, 0.057000, 0.054000,
						0.085000, 0.090000, 0.086000, 250000, 0.051500,
						0.053500, 0.084700),
				new Properties(Size.KCMIL_300, 240, 285, 320, 195, 230, 260,
						0.041000, 0.051000, 0.044000, 0.049000, 0.045000,
						0.071000, 0.076000, 0.072000, 300000, 0.042900,
						0.044600, 0.070700),
				new Properties(Size.KCMIL_350, 260, 310, 350, 210, 250, 280,
						0.040000, 0.050000, 0.038000, 0.043000, 0.039000,
						0.061000, 0.066000, 0.063000, 350000, 0.036700,
						0.038200, 0.060500),
				new Properties(Size.KCMIL_400, 280, 335, 380, 225, 270, 305,
						0.040000, 0.049000, 0.033000, 0.038000, 0.035000,
						0.054000, 0.059000, 0.055000, 400000, 0.032100,
						0.033100, 0.052900),
				new Properties(Size.KCMIL_500, 320, 380, 430, 260, 310, 350,
						0.039000, 0.048000, 0.027000, 0.032000, 0.029000,
						0.043000, 0.048000, 0.045000, 500000, 0.025800,
						0.026500, 0.042400),
				new Properties(Size.KCMIL_600, 350, 420, 475, 285, 340, 385,
						0.039000, 0.048000, 0.023000, 0.028000, 0.025000,
						0.036000, 0.041000, 0.038000, 600000, 0.021400,
						0.022300, 0.035300),
				new Properties(Size.KCMIL_700, 385, 460, 520, 315, 375, 425,
						0.038500, 0.048000, 0.021000, 0.026000, 0.021900,
						0.032500, 0.038000, 0.033700, 700000, 0.018400,
						0.018900, 0.030300),
				new Properties(Size.KCMIL_750, 400, 475, 535, 320, 385, 435,
						0.038000, 0.048000, 0.019000, 0.024000, 0.021000,
						0.029000, 0.034000, 0.031000, 750000, 0.017100,
						0.017600, 0.028200),
				new Properties(Size.KCMIL_800, 410, 490, 555, 330, 395, 445,
						0.037800, 0.047600, 0.018200, 0.023000, 0.020400,
						0.027800, 0.032600, 0.029800, 800000, 0.016100,
						0.016600, 0.026500),
				new Properties(Size.KCMIL_900, 435, 520, 585, 355, 425, 480,
						0.037400, 0.046800, 0.016600, 0.021000, 0.019200,
						0.025400, 0.029800, 0.027400, 900000, 0.014300,
						0.014700, 0.023500),
				new Properties(Size.KCMIL_1000, 455, 545, 615, 375, 445, 500,
						0.037000, 0.046000, 0.015000, 0.019000, 0.018000,
						0.023000, 0.027000, 0.025000, 1000000, 0.012900,
						0.013200, 0.021200),
				new Properties(Size.KCMIL_1250, 495, 590, 665, 405, 485, 545,
						0.036000, 0.046000, 0.011351, 0.014523, 0.014523,
						0.017700, 0.023436, 0.021600, 1250000, 0.010300,
						0.010600, 0.016900),
				new Properties(Size.KCMIL_1500, 525, 625, 705, 435, 520, 585,
						0.035000, 0.045000, 0.009798, 0.013127, 0.013127,
						0.015000, 0.020941, 0.019300, 1500000, 0.008580,
						0.008830, 0.014100),
				new Properties(Size.KCMIL_1750, 545, 650, 735, 455, 545, 615,
						0.034000, 0.045000, 0.008710, 0.012275, 0.012275,
						0.013100, 0.019205, 0.017700, 1750000, 0.007350,
						0.007560, 0.012100),
				new Properties(Size.KCMIL_2000, 555, 665, 750, 470, 560, 630,
						0.034000, 0.044000, 0.007928, 0.011703, 0.011703,
						0.011700, 0.018011, 0.016600, 2000000, 0.006430,
						0.006620, 0.010600),
		};
		//endregion

		//region TW
		TW = new HashMap<>();
		TW.put(Size.AWG_14, 0.0139);
		TW.put(Size.AWG_12, 0.0181);
		TW.put(Size.AWG_10, 0.0243);
		TW.put(Size.AWG_8, 0.0437);
		TW.put(Size.AWG_6, 0.0726);
		TW.put(Size.AWG_4, 0.0973);
		TW.put(Size.AWG_3, 0.1134);
		TW.put(Size.AWG_2, 0.1333);
		TW.put(Size.AWG_1, 0.1901);
		TW.put(Size.AWG_1$0, 0.2223);
		TW.put(Size.AWG_2$0, 0.2624);
		TW.put(Size.AWG_3$0, 0.3117);
		TW.put(Size.AWG_4$0, 0.3718);
		TW.put(Size.KCMIL_250, 0.4596);
		TW.put(Size.KCMIL_300, 0.5281);
		TW.put(Size.KCMIL_350, 0.5958);
		TW.put(Size.KCMIL_400, 0.6619);
		TW.put(Size.KCMIL_500, 0.7901);
		TW.put(Size.KCMIL_600, 0.9729);
		TW.put(Size.KCMIL_700, 1.101);
		TW.put(Size.KCMIL_750, 1.1652);
		TW.put(Size.KCMIL_800, 1.2272);
		TW.put(Size.KCMIL_900, 1.3561);
		TW.put(Size.KCMIL_1000, 1.4784);
		TW.put(Size.KCMIL_1250, 1.8602);
		TW.put(Size.KCMIL_1500, 2.1695);
		TW.put(Size.KCMIL_1750, 2.4773);
		TW.put(Size.KCMIL_2000, 2.7818);
		//endregion

		//region RHW
		RHW = new HashMap<>();
		RHW.put(Size.AWG_14, 0.0293);
		RHW.put(Size.AWG_12, 0.0353);
		RHW.put(Size.AWG_10, 0.0437);
		RHW.put(Size.AWG_8, 0.0835);
		RHW.put(Size.AWG_6, 0.1041);
		RHW.put(Size.AWG_4, 0.1333);
		RHW.put(Size.AWG_3, 0.1521);
		RHW.put(Size.AWG_2, 0.1750);
		RHW.put(Size.AWG_1, 0.2660);
		RHW.put(Size.AWG_1$0, 0.3039);
		RHW.put(Size.AWG_2$0, 0.3505);
		RHW.put(Size.AWG_3$0, 0.4072);
		RHW.put(Size.AWG_4$0, 0.4754);
		RHW.put(Size.KCMIL_250, 0.6291);
		RHW.put(Size.KCMIL_300, 0.7088);
		RHW.put(Size.KCMIL_350, 0.7870);
		RHW.put(Size.KCMIL_400, 0.8626);
		RHW.put(Size.KCMIL_500, 1.0082);
		RHW.put(Size.KCMIL_600, 1.2135);
		RHW.put(Size.KCMIL_700, 1.3561);
		RHW.put(Size.KCMIL_750, 1.4272);
		RHW.put(Size.KCMIL_800, 1.4957);
		RHW.put(Size.KCMIL_900, 1.6377);
		RHW.put(Size.KCMIL_1000, 1.7719);
		RHW.put(Size.KCMIL_1250, 2.3479);
		RHW.put(Size.KCMIL_1500, 2.6938);
		RHW.put(Size.KCMIL_1750, 3.0357);
		RHW.put(Size.KCMIL_2000, 3.3719);
		//endregion

		//region THW
		THW = TW;
		//endregion

		//region THWN
		THWN = new HashMap<>();
		THWN.put(Size.AWG_14, 0.0097);
		THWN.put(Size.AWG_12, 0.0133);
		THWN.put(Size.AWG_10, 0.0211);
		THWN.put(Size.AWG_8, 0.0366);
		THWN.put(Size.AWG_6, 0.0507);
		THWN.put(Size.AWG_4, 0.0824);
		THWN.put(Size.AWG_3, 0.0973);
		THWN.put(Size.AWG_2, 0.1158);
		THWN.put(Size.AWG_1, 0.1562);
		THWN.put(Size.AWG_1$0, 0.1855);
		THWN.put(Size.AWG_2$0, 0.2223);
		THWN.put(Size.AWG_3$0, 0.2679);
		THWN.put(Size.AWG_4$0, 0.3237);
		THWN.put(Size.KCMIL_250, 0.3970);
		THWN.put(Size.KCMIL_300, 0.4608);
		THWN.put(Size.KCMIL_350, 0.5242);
		THWN.put(Size.KCMIL_400, 0.5863);
		THWN.put(Size.KCMIL_500, 0.7073);
		THWN.put(Size.KCMIL_600, 0.8676);
		THWN.put(Size.KCMIL_700, 0.9887);
		THWN.put(Size.KCMIL_750, 1.0496);
		THWN.put(Size.KCMIL_800, 1.1085);
		THWN.put(Size.KCMIL_900, 1.2311);
		THWN.put(Size.KCMIL_1000, 1.3478);
		//endregion

		//region ZW
		ZW = new HashMap<>();
		ZW.put(Size.AWG_14, 0.0139);
		ZW.put(Size.AWG_12, 0.0181);
		ZW.put(Size.AWG_10, 0.0243);
		ZW.put(Size.AWG_8, 0.0437);
		ZW.put(Size.AWG_6, 0.059);
		ZW.put(Size.AWG_4, 0.0814);
		ZW.put(Size.AWG_3, 0.0962);
		ZW.put(Size.AWG_2, 0.1146);
		//endregion

		//region FEP
		FEP = new HashMap<>();
		FEP.put(Size.AWG_14, 0.0100);
		FEP.put(Size.AWG_12, 0.0137);
		FEP.put(Size.AWG_10, 0.0191);
		FEP.put(Size.AWG_8, 0.0333);
		FEP.put(Size.AWG_6, 0.0468);
		FEP.put(Size.AWG_4, 0.0670);
		FEP.put(Size.AWG_3, 0.0804);
		FEP.put(Size.AWG_2, 0.0973);
		//endregion

		//region FEPB
		FEPB = FEP;
		//endregion

		//region RHH
		RHH = RHW;
		//endregion

		//region RHW-2
		RHW2 = RHW;
		//endregion

		//region THHN
		THHN = THWN;
		//endregion

		//region THHW
		THHW = TW;
		//endregion

		//region THW-2
		THW2 = TW;
		//endregion

		//region THWN2
		THWN2 = THWN;
		//endregion

		//region XHH
		XHH = new HashMap<>();
		XHH.put(Size.AWG_14, 0.0139);
		XHH.put(Size.AWG_12, 0.0181);
		XHH.put(Size.AWG_10, 0.0243);
		XHH.put(Size.AWG_8, 0.0437);
		XHH.put(Size.AWG_6, 0.0590);
		XHH.put(Size.AWG_4, 0.0814);
		XHH.put(Size.AWG_3, 0.0962);
		XHH.put(Size.AWG_2, 0.1146);
		XHH.put(Size.AWG_1, 0.1534);
		XHH.put(Size.AWG_1$0, 0.1825);
		XHH.put(Size.AWG_2$0, 0.2190);
		XHH.put(Size.AWG_3$0, 0.2642);
		XHH.put(Size.AWG_4$0, 0.3197);
		XHH.put(Size.KCMIL_250, 0.3904);
		XHH.put(Size.KCMIL_300, 0.4536);
		XHH.put(Size.KCMIL_350, 0.5166);
		XHH.put(Size.KCMIL_400, 0.5782);
		XHH.put(Size.KCMIL_500, 0.6984);
		XHH.put(Size.KCMIL_600, 0.8709);
		XHH.put(Size.KCMIL_700, 0.9923);
		XHH.put(Size.KCMIL_750, 1.0532);
		XHH.put(Size.KCMIL_800, 1.1122);
		XHH.put(Size.KCMIL_900, 1.2351);
		XHH.put(Size.KCMIL_1000, 1.3519);
		XHH.put(Size.KCMIL_1250, 1.7180);
		XHH.put(Size.KCMIL_1500, 2.0156);
		XHH.put(Size.KCMIL_1750, 2.3127);
		XHH.put(Size.KCMIL_2000, 2.6073);
		//endregion

		//region XHHW
		XHHW = XHH;
		//endregion

		//region XHHW-2
		XHHW2 = XHH;
		//endregion

		//region areaEMPTY for insulation TBS, SA, SIS, MI, USE, USE-2 and
		// ZW-2
		EMPTY = new HashMap<>();
		//endregion

		//region temperature of insulators
		/*
		XHHW & THHW are duplicated in 75 and 90 degrees columns. It is
		assumed both are 90 by definition of their double Hs. However, I
		found in NEC table 310.4(A) that THHW is 75 when used in wet
		location, but 90 in dry locations.
		XHHW is 90 for dry and damp but 75 for wet locations.
		This information should be part of the enum and also the
		"application provisions" of the conductor as described in this
		 table.
		*/
		insulationTempMap = new HashMap<>();
		insulationTempMap.put(TempRating.UNKNOWN, new Insulation[]{null});
		insulationTempMap.put(TempRating.T60, new Insulation[]{Insulation.TW});
		insulationTempMap.put(TempRating.T75, new Insulation[]{Insulation.RHW, Insulation.THW
				, Insulation.THWN, Insulation.USE, Insulation.ZW});
		insulationTempMap.put(TempRating.T90, new Insulation[]{Insulation.TBS, Insulation.SA,
				Insulation.SIS, Insulation.FEP, Insulation.FEPB, Insulation.MI, Insulation.RHH,
				Insulation.RHW2, Insulation.THHN,
				Insulation.THHW, Insulation.THW2, Insulation.THWN2, Insulation.USE2, Insulation.XHH,
				Insulation.XHHW, Insulation.XHHW2, Insulation.ZW2});
		//endregion

		//region dimensions of insulated conductors
		insulatedDimensions = new HashMap<>();
		insulatedDimensions.put(Insulation.TW, TW);
		insulatedDimensions.put(Insulation.RHW, RHW);
		insulatedDimensions.put(Insulation.THW, THW);
		insulatedDimensions.put(Insulation.THWN, THWN);
		insulatedDimensions.put(Insulation.USE, EMPTY);  //USE
		insulatedDimensions.put(Insulation.ZW, ZW);
		insulatedDimensions.put(Insulation.TBS, EMPTY);  //TBS
		insulatedDimensions.put(Insulation.SA, EMPTY);  //SA
		insulatedDimensions.put(Insulation.SIS, EMPTY);  //SIS
		insulatedDimensions.put(Insulation.FEP, FEP);
		insulatedDimensions.put(Insulation.FEPB, FEPB);
		insulatedDimensions.put(Insulation.MI, EMPTY); //MI
		insulatedDimensions.put(Insulation.RHH, RHH);
		insulatedDimensions.put(Insulation.RHW2, RHW2);
		insulatedDimensions.put(Insulation.THHN, THHN);
		insulatedDimensions.put(Insulation.THHW, THHW);
		insulatedDimensions.put(Insulation.THW2, THW2);
		insulatedDimensions.put(Insulation.THWN2, THWN2);
		insulatedDimensions.put(Insulation.USE2, EMPTY); //USE-2
		insulatedDimensions.put(Insulation.XHH, XHH);
		insulatedDimensions.put(Insulation.XHHW, XHHW);
		insulatedDimensions.put(Insulation.XHHW2, XHHW2);
		insulatedDimensions.put(Insulation.ZW2, EMPTY); //ZW-2
		//endregion

		//region compactRHH
		compactRHH = new HashMap<>();
		compactRHH.put(Size.AWG_8, 0.0531);
		compactRHH.put(Size.AWG_6, 0.0683);
		compactRHH.put(Size.AWG_4, 0.0881);
		compactRHH.put(Size.AWG_2, 0.1194);
		compactRHH.put(Size.AWG_1, 0.1698);
		compactRHH.put(Size.AWG_1$0, 0.1963);
		compactRHH.put(Size.AWG_2$0, 0.2290);
		compactRHH.put(Size.AWG_3$0, 0.2733);
		compactRHH.put(Size.AWG_4$0, 0.3217);
		compactRHH.put(Size.KCMIL_250, 0.4015);
		compactRHH.put(Size.KCMIL_300, 0.4596);
		compactRHH.put(Size.KCMIL_350, 0.5153);
		compactRHH.put(Size.KCMIL_400, 0.5741);
		compactRHH.put(Size.KCMIL_500, 0.6793);
		compactRHH.put(Size.KCMIL_600, 0.8413);
		compactRHH.put(Size.KCMIL_700, 0.9503);
		compactRHH.put(Size.KCMIL_750, 1.0118);
		compactRHH.put(Size.KCMIL_900, 1.2076);
		compactRHH.put(Size.KCMIL_1000, 1.2968);
		//endregion

		//region compactRHW
		compactRHW = compactRHH;
		//endregion

		//region compactUSE
		compactUSE = compactRHH;
		//endregion

		//region compactTHW
		compactTHW = new HashMap<>();
		compactTHW.put(Size.AWG_8, 0.0510);
		compactTHW.put(Size.AWG_6, 0.0660);
		compactTHW.put(Size.AWG_4, 0.0881);
		compactTHW.put(Size.AWG_2, 0.1194);
		compactTHW.put(Size.AWG_1, 0.1698);
		compactTHW.put(Size.AWG_1$0, 0.1963);
		compactTHW.put(Size.AWG_2$0, 0.2332);
		compactTHW.put(Size.AWG_3$0, 0.2733);
		compactTHW.put(Size.AWG_4$0, 0.3267);
		compactTHW.put(Size.KCMIL_250, 0.4128);
		compactTHW.put(Size.KCMIL_300, 0.4717);
		compactTHW.put(Size.KCMIL_350, 0.5281);
		compactTHW.put(Size.KCMIL_400, 0.5876);
		compactTHW.put(Size.KCMIL_500, 0.6939);
		compactTHW.put(Size.KCMIL_600, 0.8659);
		compactTHW.put(Size.KCMIL_700, 0.9676);
		compactTHW.put(Size.KCMIL_750, 1.0386);
		compactTHW.put(Size.KCMIL_900, 1.1766);
		compactTHW.put(Size.KCMIL_1000, 1.2968);
		//endregion

		//region compactTHHW
		compactTHHW = compactTHW;
		//endregion

		//region compactTHHN
		compactTHHN = new HashMap<>();
		compactTHHN.put(Size.AWG_6, 0.0452);
		compactTHHN.put(Size.AWG_4, 0.0730);
		compactTHHN.put(Size.AWG_2, 0.1017);
		compactTHHN.put(Size.AWG_1, 0.1352);
		compactTHHN.put(Size.AWG_1$0, 0.1590);
		compactTHHN.put(Size.AWG_2$0, 0.1924);
		compactTHHN.put(Size.AWG_3$0, 0.2290);
		compactTHHN.put(Size.AWG_4$0, 0.2780);
		compactTHHN.put(Size.KCMIL_250, 0.3525);
		compactTHHN.put(Size.KCMIL_300, 0.4071);
		compactTHHN.put(Size.KCMIL_350, 0.4656);
		compactTHHN.put(Size.KCMIL_400, 0.5216);
		compactTHHN.put(Size.KCMIL_500, 0.6151);
		compactTHHN.put(Size.KCMIL_600, 0.7620);
		compactTHHN.put(Size.KCMIL_700, 0.8659);
		compactTHHN.put(Size.KCMIL_750, 0.9076);
		compactTHHN.put(Size.KCMIL_900, 1.1196);
		compactTHHN.put(Size.KCMIL_1000, 1.2370);
		//endregion

		//region compactXHHW
		compactXHHW = new HashMap<>();
		compactXHHW.put(Size.AWG_8, 0.0394);
		compactXHHW.put(Size.AWG_6, 0.0530);
		compactXHHW.put(Size.AWG_4, 0.0730);
		compactXHHW.put(Size.AWG_2, 0.1017);
		compactXHHW.put(Size.AWG_1, 0.1352);
		compactXHHW.put(Size.AWG_1$0, 0.1590);
		compactXHHW.put(Size.AWG_2$0, 0.1885);
		compactXHHW.put(Size.AWG_3$0, 0.2290);
		compactXHHW.put(Size.AWG_4$0, 0.2733);
		compactXHHW.put(Size.KCMIL_250, 0.3421);
		compactXHHW.put(Size.KCMIL_300, 0.4015);
		compactXHHW.put(Size.KCMIL_350, 0.4536);
		compactXHHW.put(Size.KCMIL_400, 0.5026);
		compactXHHW.put(Size.KCMIL_500, 0.6082);
		compactXHHW.put(Size.KCMIL_600, 0.7542);
		compactXHHW.put(Size.KCMIL_700, 0.8659);
		compactXHHW.put(Size.KCMIL_750, 0.9331);
		compactXHHW.put(Size.KCMIL_900, 1.0733);
		compactXHHW.put(Size.KCMIL_1000, 1.1882);
		//endregion

		//region dimension of compact conductors
		compactDimensions = new HashMap<>();
		compactDimensions.put(Insulation.RHH, compactRHH);
		compactDimensions.put(Insulation.RHW, compactRHW);
		compactDimensions.put(Insulation.USE, compactUSE);
		compactDimensions.put(Insulation.THW, compactTHW);
		compactDimensions.put(Insulation.THHW, compactTHHW);
		compactDimensions.put(Insulation.THHN, compactTHHN);
		compactDimensions.put(Insulation.XHHW, compactXHHW);
		//endregion

		//region dimension of compact bare conductors
		compactBareDimensions = new HashMap<>();
		compactBareDimensions.put(Size.AWG_8, 0.0141);
		compactBareDimensions.put(Size.AWG_6, 0.0224);
		compactBareDimensions.put(Size.AWG_4, 0.0356);
		compactBareDimensions.put(Size.AWG_2, 0.0564);
		compactBareDimensions.put(Size.AWG_1, 0.0702);
		compactBareDimensions.put(Size.AWG_1$0, 0.0887);
		compactBareDimensions.put(Size.AWG_2$0, 0.1110);
		compactBareDimensions.put(Size.AWG_3$0, 0.1405);
		compactBareDimensions.put(Size.AWG_4$0, 0.1772);
		compactBareDimensions.put(Size.KCMIL_250, 0.2124);
		compactBareDimensions.put(Size.KCMIL_300, 0.2552);
		compactBareDimensions.put(Size.KCMIL_350, 0.2980);
		compactBareDimensions.put(Size.KCMIL_400, 0.3411);
		compactBareDimensions.put(Size.KCMIL_500, 0.4254);
		compactBareDimensions.put(Size.KCMIL_600, 0.5191);
		compactBareDimensions.put(Size.KCMIL_700, 0.6041);
		compactBareDimensions.put(Size.KCMIL_750, 0.6475);
		compactBareDimensions.put(Size.KCMIL_900, 0.7838);
		compactBareDimensions.put(Size.KCMIL_1000, 0.8825);
		//endregion
	}


	/**
	 Returns a Properties object for the given conductor size.
	 @param conductorSize The size of the conductor for which the properties
	 object is being requested. It cannot be null.
	 @return A Properties object for the given conductor size.
	 @see Size
	 */
	private static Properties bySize(@NotNull Size conductorSize) {
		return table[conductorSize.ordinal()];
	}

	/**
	 Returns the area of an insulated conductor (conductor + insulation) of the
	 given size and  insulation.
	 @param conductorSize The size of the conductor as defined by {@link Size}
	 @param insulation The insulation type of the conductor as defined by
	 {@link
	 Insulation}
	 @return The area of the requested insulated conductor, in square inches or
	 zero if the conductor doesn't have an area as defined in NEC table 5.
	 */
	public static double getInsulatedConductorAreaIn2(Size conductorSize,
	                                                  Insulation insulation) {
		if (hasInsulatedAreaDefined(conductorSize, insulation))
			return insulatedDimensions.get(insulation).get(conductorSize);
		return 0;
	}

	/**
	 Returns the area of a compact conductor (Table 5A) of the given size and
	 insulation.
	 @param conductorSize The size of the conductor as defined by {@link Size}
	 @param insulation The insulation type of the conductor as defined by
	 {@link
	 Insulation}
	 @return The area of the compact conductor, in square inches, or zero if
	 the area of  the given conductor is not defined in table 5A.
	 */
	public static double getCompactConductorAreaIn2(Size conductorSize,
	                                                Insulation insulation) {
		if (hasCompactAreaDefined(conductorSize, insulation))
			return compactDimensions.get(insulation).get(conductorSize);
		return 0;
	}

	/**
	 Returns the area in square inches of the requested bare compact conductor
	 size (Table 5A).
	 @param conductorSize The size of the conductor as defined by {@link Size}.
	 @return The area of the bare compact conductor or zero if the area is not
	 defined in table 5A.
	 */
	public static double getBareCompactConductorAreaIn2(@NotNull Size conductorSize) {
		if (hasCompactBareAreaDefined(conductorSize))
			return compactBareDimensions.get(conductorSize);
		return 0;
	}

	/**
	 Returns true if an insulated conductor of the given size and insulation
	 type has an area defined in table 5.
	 @param conductorSize The size of the conductor as defined by {@link Size}.
	 @param insulation The insulation type of the conductor as defined by
	 {@link
	 Insulation}.
	 @return True if the area is defined in table 5, false otherwise or
	 parameters are null.
	 */
	public static boolean hasInsulatedAreaDefined(Size conductorSize,
	                                              Insulation insulation) {
		return insulatedDimensions.containsKey(insulation) && insulatedDimensions.get(insulation).containsKey(conductorSize);
	}

	/**
	 Returns true if the compact conductor of the given size and insulation has
	 its area defined in table 5A.
	 @param conductorSize The size of the conductor as defined by {@link Size}.
	 @param insulation The insulation type of the conductor as defined by
	 {@link
	 Insulation}.
	 @return True if the area is defined in table 5A, false otherwise.
	 */
	public static boolean hasCompactAreaDefined(Size conductorSize,
	                                            Insulation insulation) {
		return compactDimensions.containsKey(insulation) && compactDimensions.get(insulation).containsKey(conductorSize);
	}

	/**
	 Returns true if a compact bare conductor of the given has its area defined
	 in table 5A.
	 @param conductorSize The size of the conductor as defined by
	 {@link Size}. It cannot be null.
	 @return True if the area is defined in table 5A, false otherwise.
	 */
	public static boolean hasCompactBareAreaDefined(@NotNull Size conductorSize) {
		return compactBareDimensions.containsKey(conductorSize);
	}

	/**
	 Requests the temperature rating of the given insulation. It assumes the
	 location of the underlined conductor is DRY.
	 @param insulation The requested insulation as defined in {@link Insulation}. It
	 cannot be null.
	 @return A {@link TempRating} enum representing the temperature rating of
	 this insulation.
	 */
	public static @NotNull TempRating getTempRating(@NotNull Insulation insulation) {
		TempRating result = null;
		for (TempRating tempRating : TempRating.values()) {
			for (Insulation insul : insulationTempMap.get(tempRating)) {
				if (insul == insulation) {
					result = tempRating;
					break;
				}
			}
			if (result != null)
				break;
		}
		assert result != null: "Temperature rating for the given insulation " +
				"not found";
		return result;
	}

	/**
	 Requests the temperature rating of the given insulation for the given
	 location condition.
	 @param insulation The requested insulation, as defined in {@link Insulation}.
	 It cannot be null.
	 @param location The location condition. See {@link Location}. It cannot
	 be null.
	 @return A {@link TempRating} enum representing the temperature rating of
	 this insulation.
	 */
	public static @NotNull TempRating getTempRating(@NotNull Insulation insulation,
			@NotNull Location location) {
		if ((location == WET) && (insulation == Insulation.THHW || insulation == Insulation.XHHW)) {
			return TempRating.T75;
		}
		return getTempRating(insulation);
	}

	/**
	 Compares the two given conductor sizes and returns the biggest one.
	 @param size1 Size to be compared.
	 @param size2 Size to be compared.
	 @return The biggest of the two sizes.
	 */
	public static Size getBiggestSize(@NotNull Size size1, @NotNull Size size2) {
		return getAreaCM(size1)<getAreaCM(size2) ? size2 : size1;
	}

	/**
	 Returns the reactance property of this conductor under the given conduit
	 magnetic condition.
	 @param conductorSize The size of the conductor as defined by
	 {@link Size}. It cannot be null.
	 @param magneticConduit Indicates if the conduit is magnetic or not.
	 @return The reactance of this conductor in ohms per 1000 feet.
	 */
	public static double getReactance(@NotNull Size conductorSize,
	                                  boolean magneticConduit) {
		if (magneticConduit)
			return bySize(conductorSize).magXL;
		return bySize(conductorSize).nonMagXL;
	}

	/**
	 Returns the total reactance of this conductor for the given magnetic
	 conduit condition, length and number of parallel conductors.
	 @param conductorSize The size of the conductor as defined by
	 {@link Size}. It cannot be null.
	 @param magneticConduit Indicates if the conduit is magnetic or not.
	 @param oneWayLength The length in feet of this conductor. Must be >0
	 @param numberOfSets The number of conductors in parallel. Must be >0
	 @return The total reactance under the specified conditions, in ohms.
	 */
	public static double getReactance(@NotNull Size conductorSize,
	                                  boolean magneticConduit,
	                                  double oneWayLength,
	                                  int numberOfSets) {
		if (oneWayLength <= 0)
			throw new IllegalArgumentException("oneWayLength must be > 0");
		if (numberOfSets <= 0 )
			throw new IllegalArgumentException("numberOfSets must be > 0");

		return getReactance(conductorSize, magneticConduit) * 0.001 * oneWayLength / numberOfSets;
	}

	/**
	 Returns the metal area of the given conductor, in Circular Mils.
	 @param conductorSize The size of the conductor as defined by
	 {@link Size}. It cannot be null.
	 @return The area in Circular Mils.
	 */
	public static int getAreaCM(@NotNull Size conductorSize) {
		return bySize(conductorSize).areaCM;
	}

	/**
	 @return The size of the conductor whose cross-sectional area is equal or immediately above of the given area, or
	 null if there is no conductor with a size whose area is equal or greater than the given one.
	 @param areaCM The area for which a conductor size is requested. Must be > 0. and in circular mils.
	 */
	public static @Nullable Size getSizePerArea(double areaCM) {
		if (areaCM <= 0)
			throw new IllegalArgumentException("areaCM must be > 0");

		for (Properties properties : table) {
			if (properties.areaCM >= areaCM)
				return properties.size;
		}
		return null;
	}

	/**
	 Returns the DC resistance of this conductor size for the given conductiveMetal.
	 @param conductorSize The size of the conductor as defined by {@link Size}
	 @param conductiveMetal The conductiveMetal of the conductor as defined by  {@link ConductiveMetal}.
	 None of these parameters can be null.
	 @return The DC resistance of this conductor in ohms per 1000 feet.
	 */
	public static double getDCResistance(@NotNull Size conductorSize,
	                                     @NotNull ConductiveMetal conductiveMetal) {
		if (conductiveMetal == ConductiveMetal.COPPER)
			return bySize(conductorSize).CuResDCUncoated;
		if (conductiveMetal == ConductiveMetal.COPPERCOATED)
			return bySize(conductorSize).CuResDCCoated;
		return bySize(conductorSize).ALResDC;
	}

	/**
	 Returns the DC resistance of this conductor size for the given conductiveMetal,
	 length, sets, etc.
	 @param conductorSize Not null. The size of the conductor as defined by
	 {@link Size}.
	 @param conductiveMetal Not null. The conductiveMetal of the conductor as defined by
	 {@link ConductiveMetal}.
	 @param oneWayLength The length of the conductor. Must be >0
	 @param numberOfSets The number of conductors in parallel. Must be >0.
	 @return The DC resistance in ohms of this conductor size under the given
	 conditions.
	 */
	public static double getDCResistance(@NotNull Size conductorSize,
	                                     @NotNull ConductiveMetal conductiveMetal,
	                                     double oneWayLength, int numberOfSets) {
		if (oneWayLength <= 0)
			throw new IllegalArgumentException("oneWayLength must be > 0");
		if (numberOfSets <= 0 )
			throw new IllegalArgumentException("numberOfSets must be > 0");

		return getDCResistance(conductorSize, conductiveMetal) * 0.001 * oneWayLength / numberOfSets;
	}

	/**
	 Returns the AC resistance of this conductor size for the given conductiveMetal and
	 conduit material.
	 @param conductorSize Not null. The size of the conductor as defined by
	 {@link Size}.
	 @param conductiveMetal Not null. The conductiveMetal of the conductor as defined by
	 {@link ConductiveMetal}.
	 @param conduitMaterial The material type of the conduit as specified in {@link OuterMaterial}, or NULL if the
	 conductor is in free air.
	 @return The AC resistance in ohms per 1000 feet.
	 */
	public static double getACResistance(@NotNull Size conductorSize, @NotNull ConductiveMetal conductiveMetal,
	                                     @Nullable OuterMaterial conduitMaterial) {
		if (conductiveMetal == ConductiveMetal.COPPER) {
			if (conduitMaterial == OuterMaterial.PVC)
				return bySize(conductorSize).CuResInPVCCond;
			else if (conduitMaterial == OuterMaterial.ALUMINUM)
				return bySize(conductorSize).CuResInALCond;
			else
				return bySize(conductorSize).CuResInSteelCond;
		} else {
			if (conduitMaterial == OuterMaterial.PVC || conduitMaterial == null)
				return bySize(conductorSize).ALResInPVCCond;
			else if (conduitMaterial == OuterMaterial.ALUMINUM)
				return bySize(conductorSize).ALResInALCond;
			else
				return bySize(conductorSize).ALResInSteelCond;
		}
	}

	/**
	 Returns the AC resistance of this conductor size for the given conductiveMetal,
	 conduit material, length and number of sets.
	 @param conductorSize Not null. The size of the conductor as defined by
	 {@link Size}
	 @param conductiveMetal Not null. The conductiveMetal of the conductor as defined by
	 {@link ConductiveMetal}.
	 @param conduitMaterial Not null. The material type of the conduit as
	 specified in {@link OuterMaterial}.
	 @param oneWayLength The length of the conductor in feet. Must be >0
	 @param numberOfSets The number of sets (conductors in parallel per
	 phase). Must be > 0
	 @return The AC resistance in ohms of this conductor size under the given
	 conditions.
	 */
	public static double getACResistance(@NotNull Size conductorSize, @NotNull ConductiveMetal conductiveMetal,
	                                     @NotNull OuterMaterial conduitMaterial,
	                                     double oneWayLength, int numberOfSets) {
		if (oneWayLength < 0)
			throw new IllegalArgumentException("oneWayLength must be > 0");
		if (numberOfSets <= 0 )
			throw new IllegalArgumentException("numberOfSets must be > 0");
		return getACResistance(conductorSize, conductiveMetal, conduitMaterial) * 0.001 * oneWayLength / numberOfSets;
	}

	/**
	 Returns the ampacity of the given conductor size for the given conductiveMetal and
	 temperature rating per the NEC table 310.16 (old 310.15(B)(16)) (30 °C, up to 3
	 current-carrying conductors)
	 @param conductorSize Not null. The size of the conductor as defined by
	 {@link Size}
	 @param conductiveMetal Not null. The conductiveMetal of the conductor as defined in
	 {@link ConductiveMetal}.
	 @param temperatureRating Not null. The temperature rating as defined in
	 {@link TempRating}
	 @return The ampacity in amperes.
	 */
	public static double getStandardAmpacity(@NotNull Size conductorSize,
	                                         @NotNull ConductiveMetal conductiveMetal,
	                                         @NotNull TempRating temperatureRating) {
		if (conductiveMetal == ConductiveMetal.COPPER) {
			if (temperatureRating == TempRating.T60)
				return bySize(conductorSize).CuAmp60;
			else if (temperatureRating == TempRating.T75)
				return bySize(conductorSize).CuAmp75;
			else //temperatureRating == TempRating.T90
				return bySize(conductorSize).CuAmp90;
		} else { //conductiveMetal == ConductiveMetal.ALUMINUM
			if (temperatureRating == TempRating.T60)
				return bySize(conductorSize).AlAmp60;
			else if (temperatureRating == TempRating.T75)
				return bySize(conductorSize).AlAmp75;
			else //temperatureRating == TempRating.T90
				return bySize(conductorSize).AlAmp90;
		}
	}

	/**
	 Returns the minimum allowed size for a conductor of the given conductiveMetal and temperature rating, for the
	 given current, as per table 310.16.<p>
	 The returned size does not account for any correction or adjustment factor, that is, the ambient temperature is
	 86 °F and no more than 3 current-carrying conductors in a raceway.
	 @param current The current in amperes. This value should be the one obtained once all conditions of use have
	 been accounted for, that is, once the temperature correction factor, the adjustment factor and any other
	 restriction have been applied. Must be > 0.
	 @param conductiveMetal Not null. The metal of the conductor as defined in {@link ConductiveMetal}.
	 @param tempRating Not null. The temperature rating of the conductor (its insulation) as defined in
	 {@link TempRating}.
	 @return The minimum size of the conductor that can carry the given current for the temperature rating, or null
	 if no conductor can carry the given high current.
	 */
	public static @Nullable Size getSizePerCurrent(double current,
	                                               @NotNull ConductiveMetal conductiveMetal,
	                                               @NotNull TempRating tempRating) {
		if (current <= 0)
			throw new IllegalArgumentException("current must be > 0");

		if (conductiveMetal == ConductiveMetal.COPPER) {
			if (tempRating == TempRating.T60) {
				for (Properties properties : table)
					if (properties.CuAmp60 >= current)
						return properties.size;
			} else if (tempRating == TempRating.T75) {
				for (Properties properties : table)
					if (properties.CuAmp75 >= current)
						return properties.size;
			} else { //tempRating == TempRating.T90
				for (Properties properties : table)
					if (properties.CuAmp90 >= current)
						return properties.size;
			}
		} else { //conductiveMetal == ConductiveMetal.ALUMINUM
			if (tempRating == TempRating.T60) {
				for (Properties properties : table)
					if (properties.AlAmp60 >= current)
						return properties.size;
			} else if (tempRating == TempRating.T75) {
				for (Properties properties : table)
					if (properties.AlAmp75 >= current)
						return properties.size;
			} else { //tempRating == TempRating.T90
				for (Properties properties : table)
					if (properties.AlAmp90 >= current)
						return properties.size;
			}
		}
		/*this will only happen when the allowed ampacity is higher than any of
		 the ampacity of a 2000 KCMIL conductor.*/
		return null;
	}
}
