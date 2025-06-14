package eecalcs.conduits;

import eecalcs.systems.NECEdition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.HashMap;
import java.util.Map;

/**
 Encapsulates constants, static data and methods about electrical conduits as
 found in NEC table 4.
 */
public class ConduitProperties {
	private static final Map<TradeSize, Double> areaEMT;
	private static final Map<TradeSize, Double> areaENT;
	private static final Map<TradeSize, Double> areaFMT;
	private static final Map<TradeSize, Double> areaIMC;
	private static final Map<TradeSize, Double> areaLFNCA;
	private static final Map<TradeSize, Double> areaLFNCB;
	private static final Map<TradeSize, Double> areaLFMC;
	private static final Map<TradeSize, Double> areaRMC;
	private static final Map<TradeSize, Double> areaPVC80;
	private static final Map<TradeSize, Double> areaPVC40;
	private static final Map<TradeSize, Double> areaHDPE;
	private static final Map<TradeSize, Double> areaPVCA;
	private static final Map<TradeSize, Double> areaPVCEB;
	private static final Map<Type, Map<TradeSize, Double>> dimensions;

	private static final double ROOFTOP_CONDITION_DISTANCE_2014 = 36.0;
	private static final double ROOFTOP_CONDITION_DISTANCE_2017_2020 = 7.0/8.0;

	static {
		//region EMT
		areaEMT = new HashMap<>();
		areaEMT.put(TradeSize.T1$2,    0.304);
		areaEMT.put(TradeSize.T3$4,    0.533);
		areaEMT.put(TradeSize.T1,      0.864);
		areaEMT.put(TradeSize.T1_1$4,  1.496);
		areaEMT.put(TradeSize.T1_1$2,  2.036);
		areaEMT.put(TradeSize.T2,      3.356);
		areaEMT.put(TradeSize.T2_1$2,  5.858);
		areaEMT.put(TradeSize.T3,      8.846);
		areaEMT.put(TradeSize.T3_1$2, 11.545);
		areaEMT.put(TradeSize.T4,     14.753);
		//endregion

		//region ENT
		areaENT = new HashMap<>();
		areaENT.put(TradeSize.T1$2,   0.285);
		areaENT.put(TradeSize.T3$4,   0.508);
		areaENT.put(TradeSize.T1,     0.832);
		areaENT.put(TradeSize.T1_1$4, 1.453);
		areaENT.put(TradeSize.T1_1$2, 1.986);
		areaENT.put(TradeSize.T2,     3.291);
		//endregion

		//region FMT
		areaFMT = new HashMap<>();
		areaFMT.put(TradeSize.T3$8,   0.116);
		areaFMT.put(TradeSize.T1$2,   0.317);
		areaFMT.put(TradeSize.T3$4,   0.533);
		areaFMT.put(TradeSize.T1,     0.817);
		areaFMT.put(TradeSize.T1_1$4, 1.277);
		areaFMT.put(TradeSize.T1_1$2, 1.858);
		areaFMT.put(TradeSize.T2,     3.269);
		areaFMT.put(TradeSize.T2_1$2, 4.909);
		areaFMT.put(TradeSize.T3,     7.069);
		areaFMT.put(TradeSize.T3_1$2, 9.621);
		areaFMT.put(TradeSize.T4,    12.566);
		//endregion

		//region IMC
		areaIMC = new HashMap<>();
		areaIMC.put(TradeSize.T1$2,    0.342);
		areaIMC.put(TradeSize.T3$4,    0.586);
		areaIMC.put(TradeSize.T1,      0.959);
		areaIMC.put(TradeSize.T1_1$4,  1.647);
		areaIMC.put(TradeSize.T1_1$2,  2.225);
		areaIMC.put(TradeSize.T2,       3.63);
		areaIMC.put(TradeSize.T2_1$2,  5.135);
		areaIMC.put(TradeSize.T3,      7.922);
		areaIMC.put(TradeSize.T3_1$2, 10.584);
		areaIMC.put(TradeSize.T4,     13.631);
		//endregion

		//region LFNCA
		areaLFNCA = new HashMap<>();
		areaLFNCA.put(TradeSize.T3$8,   0.192);
		areaLFNCA.put(TradeSize.T1$2,   0.312);
		areaLFNCA.put(TradeSize.T3$4,   0.535);
		areaLFNCA.put(TradeSize.T1,     0.854);
		areaLFNCA.put(TradeSize.T1_1$4, 1.502);
		areaLFNCA.put(TradeSize.T1_1$2, 2.018);
		areaLFNCA.put(TradeSize.T2,     3.343);
		//endregion

		//region LFNCB
		areaLFNCB = new HashMap<>();
		areaLFNCB.put(TradeSize.T3$8,   0.192);
		areaLFNCB.put(TradeSize.T1$2,   0.314);
		areaLFNCB.put(TradeSize.T3$4,   0.541);
		areaLFNCB.put(TradeSize.T1,     0.873);
		areaLFNCB.put(TradeSize.T1_1$4, 1.528);
		areaLFNCB.put(TradeSize.T1_1$2, 1.981);
		areaLFNCB.put(TradeSize.T2,     3.246);
		//endregion

		//region LFMC
		areaLFMC = new HashMap<>();
		areaLFMC.put(TradeSize.T3$8,   0.192);
		areaLFMC.put(TradeSize.T1$2,   0.314);
		areaLFMC.put(TradeSize.T3$4,   0.541);
		areaLFMC.put(TradeSize.T1,     0.873);
		areaLFMC.put(TradeSize.T1_1$4, 1.528);
		areaLFMC.put(TradeSize.T1_1$2, 1.981);
		areaLFMC.put(TradeSize.T2,     3.246);
		areaLFMC.put(TradeSize.T2_1$2, 4.881);
		areaLFMC.put(TradeSize.T3,     7.475);
		areaLFMC.put(TradeSize.T3_1$2, 9.731);
		areaLFMC.put(TradeSize.T4,    12.692);
		//endregion

		//region RMC
		areaRMC = new HashMap<>();
		areaRMC.put(TradeSize.T1$2,   0.314);
		areaRMC.put(TradeSize.T3$4,   0.549);
		areaRMC.put(TradeSize.T1,     0.887);
		areaRMC.put(TradeSize.T1_1$4, 1.526);
		areaRMC.put(TradeSize.T1_1$2, 2.071);
		areaRMC.put(TradeSize.T2,     3.408);
		areaRMC.put(TradeSize.T2_1$2, 4.866);
		areaRMC.put(TradeSize.T3,     7.499);
		areaRMC.put(TradeSize.T3_1$2, 10.01);
		areaRMC.put(TradeSize.T4,    12.882);
		areaRMC.put(TradeSize.T5,    20.212);
		areaRMC.put(TradeSize.T6,    29.158);
		//endregion

		//region PVC80
		areaPVC80 = new HashMap<>();
		areaPVC80.put(TradeSize.T1$2,   0.217);
		areaPVC80.put(TradeSize.T3$4,   0.409);
		areaPVC80.put(TradeSize.T1,     0.688);
		areaPVC80.put(TradeSize.T1_1$4, 1.237);
		areaPVC80.put(TradeSize.T1_1$2, 1.711);
		areaPVC80.put(TradeSize.T2,     2.874);
		areaPVC80.put(TradeSize.T2_1$2, 4.119);
		areaPVC80.put(TradeSize.T3,     6.442);
		areaPVC80.put(TradeSize.T3_1$2, 8.688);
		areaPVC80.put(TradeSize.T4,    11.258);
		areaPVC80.put(TradeSize.T5,    17.855);
		areaPVC80.put(TradeSize.T6,    25.598);
		//endregion

		//region PVC40
		areaPVC40 = new HashMap<>();
		areaPVC40.put(TradeSize.T1$2,   0.285);
		areaPVC40.put(TradeSize.T3$4,   0.508);
		areaPVC40.put(TradeSize.T1,     0.832);
		areaPVC40.put(TradeSize.T1_1$4, 1.453);
		areaPVC40.put(TradeSize.T1_1$2, 1.986);
		areaPVC40.put(TradeSize.T2,     3.291);
		areaPVC40.put(TradeSize.T2_1$2, 4.695);
		areaPVC40.put(TradeSize.T3,     7.268);
		areaPVC40.put(TradeSize.T3_1$2, 9.737);
		areaPVC40.put(TradeSize.T4,    12.554);
		areaPVC40.put(TradeSize.T5,    19.761);
		areaPVC40.put(TradeSize.T6,    28.567);
		//endregion

		//region HDPE
		areaHDPE = new HashMap<>();
		areaHDPE.put(TradeSize.T1$2,   0.285);
		areaHDPE.put(TradeSize.T3$4,   0.508);
		areaHDPE.put(TradeSize.T1,     0.832);
		areaHDPE.put(TradeSize.T1_1$4, 1.453);
		areaHDPE.put(TradeSize.T1_1$2, 1.986);
		areaHDPE.put(TradeSize.T2,     3.291);
		areaHDPE.put(TradeSize.T2_1$2, 4.695);
		areaHDPE.put(TradeSize.T3,     7.268);
		areaHDPE.put(TradeSize.T3_1$2, 9.737);
		areaHDPE.put(TradeSize.T4,    12.554);
		areaHDPE.put(TradeSize.T5,    19.761);
		areaHDPE.put(TradeSize.T6,    28.567);
		//endregion

		//region PVCA
		areaPVCA = new HashMap<>();
		areaPVCA.put(TradeSize.T1$2,    0.385);
		areaPVCA.put(TradeSize.T3$4,     0.65);
		areaPVCA.put(TradeSize.T1,      1.084);
		areaPVCA.put(TradeSize.T1_1$4,  1.767);
		areaPVCA.put(TradeSize.T1_1$2,  2.324);
		areaPVCA.put(TradeSize.T2,      3.647);
		areaPVCA.put(TradeSize.T2_1$2,  5.453);
		areaPVCA.put(TradeSize.T3,      8.194);
		areaPVCA.put(TradeSize.T3_1$2, 10.694);
		areaPVCA.put(TradeSize.T4,     13.723);
		//endregion

		//region PVCEB
		areaPVCEB = new HashMap<>();
		areaPVCEB.put(TradeSize.T2,      3.874);
		areaPVCEB.put(TradeSize.T3,      8.709);
		areaPVCEB.put(TradeSize.T3_1$2, 11.365);
		areaPVCEB.put(TradeSize.T4,     14.448);
		areaPVCEB.put(TradeSize.T5,     22.195);
		areaPVCEB.put(TradeSize.T6,      31.53);
		//endregion

		//region dimensions
		dimensions = new HashMap<>();
		dimensions.put(Type.EMT,    areaEMT);
		dimensions.put(Type.EMTAL,  areaEMT);
		dimensions.put(Type.ENT,    areaENT);
		dimensions.put(Type.FMC,    areaFMT);
		dimensions.put(Type.FMCAL,  areaFMT);
		dimensions.put(Type.IMC,    areaIMC);
		dimensions.put(Type.LFNCA,  areaLFNCA);
		dimensions.put(Type.LFNCB,  areaLFNCB);
		dimensions.put(Type.LFMC,   areaLFMC);
		dimensions.put(Type.LFMCAL, areaLFMC);
		dimensions.put(Type.RMC,    areaRMC);
		dimensions.put(Type.RMCAL,  areaRMC);
		dimensions.put(Type.PVC80,  areaPVC80);
		dimensions.put(Type.PVC40,  areaPVC40);
		dimensions.put(Type.HDPE,   areaHDPE);
		dimensions.put(Type.PVCA,   areaPVCA);
		dimensions.put(Type.PVCEB,  areaPVCEB);
		//endregion
	}

	public static double getRooftopConditionDistance(){
		if(NECEdition.getDefault() == NECEdition.NEC2014)
			return ROOFTOP_CONDITION_DISTANCE_2014;
		else //NEC2017 or NEC2020
			return ROOFTOP_CONDITION_DISTANCE_2017_2020;
	}

	/**
	 Asks if the given conduit type and trade size has an internal area, that
	 is, if the type and trade size have an entry in NEC Table 4.

	 @param conduitType The type of conduit. Cannot be null.
	 @param tradeSize The trade size of the conduit. Cannot be null.
	 @return True if the requested conduit type and trade size have an internal
	 area in table 4.
	 */
	public static boolean hasArea(@NotNull Type conduitType, @NotNull TradeSize tradeSize){
		return dimensions.get(conduitType).containsKey(tradeSize);
	}

	/**
	 Gets the area of the given conduit type and trade size.

	 @param conduitType  The type of conduit. Cannot be null.
	 @param tradeSize The size of the conduit. Cannot be null.
	 @return The area in square inches of the conduit, or zero if not in NEC Table 4.
	 */
	public static double getArea(@NotNull Type conduitType, @NotNull TradeSize tradeSize){
		if(hasArea(conduitType, tradeSize))
			return dimensions.get(conduitType).get(tradeSize);
		return 0;
	}

	/**
	 @return The map of trade-size-and-area pair values corresponding to the
	 given conduit type.
	 @param conduitType The conduit type for which the areas are requested. Cannot be null.
	 */
	private static Map<TradeSize, Double> getAreasForType(@NotNull Type conduitType){
		return dimensions.get(conduitType);
	}

	/**
	 @return The smaller conduit trade size whose internal area is equal or bigger than the given area, or the
	 minimum given trade size, whichever is higher. Returns null if the given area is larger than the biggest
	 conduit trade size for the given conduit type.
	 @param area The area for which a trade size is requested. Must be >= 0.
	 @param type The type of the conduit. Cannot be null.
	 @param minimumTradeSizeSize The minimum trade size desired. Cannot be null.
	 */
	public static @Nullable TradeSize getTradeSizeForArea(double area, @NotNull Type type,
	                                                      @NotNull TradeSize minimumTradeSizeSize){
		if (area < 0)
			throw new IllegalArgumentException("The area parameter must be >= 0");
		Map<TradeSize, Double> areasForType =
				ConduitProperties.getAreasForType(type);

		for (int i = minimumTradeSizeSize.ordinal(); i < TradeSize.values().length; i++)
			if (ConduitProperties.hasArea(type, TradeSize.values()[i])) {
				if (areasForType.get(TradeSize.values()[i]) >= area)
					return TradeSize.values()[i];
			}
		return null;
	}

	/**
	 * Returns the material (aluminum, steel or pvc) of the given conduit type.
	 * @param type The conduit type as defined in {@link Type} for which the material is requested.
	 * @return The requested material.
	 */
	public static OuterMaterial getMaterial(Type type){
		if(type == Type.EMTAL | type == Type.FMCAL | type == Type.LFMCAL | type == Type.RMCAL)
			return OuterMaterial.ALUMINUM;
		else if(type == Type.EMT | type == Type.FMC | type == Type.IMC | type == Type.LFMC | type == Type.RMC)
			return OuterMaterial.STEEL;
		return OuterMaterial.PVC;
	}

}
