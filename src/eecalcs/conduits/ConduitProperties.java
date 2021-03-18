package eecalcs.conduits;

import tools.ResultMessage;

import java.util.HashMap;
import java.util.Map;

/**
 Encapsulates constants, static data and methods about electrical conduits as
 found in NEC 2014 table 4.
 */
public class ConduitProperties {
	public static final boolean Magnetic = true;
	public static final boolean nonMagnetic = false;
	private static final Map<Trade, Double> areaEMT;
	private static final Map<Trade, Double> areaENT;
	private static final Map<Trade, Double> areaFMT;
	private static final Map<Trade, Double> areaIMC;
	private static final Map<Trade, Double> areaLFNCA;
	private static final Map<Trade, Double> areaLFNCB;
	private static final Map<Trade, Double> areaLFMC;
	private static final Map<Trade, Double> areaRMC;
	private static final Map<Trade, Double> areaPVC80;
	private static final Map<Trade, Double> areaPVC40;
	private static final Map<Trade, Double> areaHDPE;
	private static final Map<Trade, Double> areaPVCA;
	private static final Map<Trade, Double> areaPVCEB;
	private static Map<Trade, Double> areaINVALID;
	private static final Map<Type, Map<Trade, Double>> dimensions;
	private static final ResultMessage ERROR40 = new ResultMessage("Invalid conduit type" +
			".", -40);
	private static final ResultMessage ERROR41 = new ResultMessage("Trade size not " +
			"available for this conduit type.", -41);
	private static final ResultMessage ERROR42 = new ResultMessage("Area of conduit " +
			"not available.", -42);

	static {
		//region EMT
		areaEMT = new HashMap<>();
		areaEMT.put(Trade.T1$2,    0.304);
		areaEMT.put(Trade.T3$4,    0.533);
		areaEMT.put(Trade.T1,      0.864);
		areaEMT.put(Trade.T1_1$4,  1.496);
		areaEMT.put(Trade.T1_1$2,  2.036);
		areaEMT.put(Trade.T2,      3.356);
		areaEMT.put(Trade.T2_1$2,  5.858);
		areaEMT.put(Trade.T3,      8.846);
		areaEMT.put(Trade.T3_1$2, 11.545);
		areaEMT.put(Trade.T4,     14.753);
		//endregion

		//region ENT
		areaENT = new HashMap<>();
		areaENT.put(Trade.T1$2,   0.285);
		areaENT.put(Trade.T3$4,   0.508);
		areaENT.put(Trade.T1,     0.832);
		areaENT.put(Trade.T1_1$4, 1.453);
		areaENT.put(Trade.T1_1$2, 1.986);
		areaENT.put(Trade.T2,     3.291);
		//endregion

		//region FMT
		areaFMT = new HashMap<>();
		areaFMT.put(Trade.T3$8,   0.116);
		areaFMT.put(Trade.T1$2,   0.317);
		areaFMT.put(Trade.T3$4,   0.533);
		areaFMT.put(Trade.T1,     0.817);
		areaFMT.put(Trade.T1_1$4, 1.277);
		areaFMT.put(Trade.T1_1$2, 1.858);
		areaFMT.put(Trade.T2,     3.269);
		areaFMT.put(Trade.T2_1$2, 4.909);
		areaFMT.put(Trade.T3,     7.069);
		areaFMT.put(Trade.T3_1$2, 9.621);
		areaFMT.put(Trade.T4,    12.566);
		//endregion

		//region IMC
		areaIMC = new HashMap<>();
		areaIMC.put(Trade.T1$2,    0.342);
		areaIMC.put(Trade.T3$4,    0.586);
		areaIMC.put(Trade.T1,      0.959);
		areaIMC.put(Trade.T1_1$4,  1.647);
		areaIMC.put(Trade.T1_1$2,  2.225);
		areaIMC.put(Trade.T2,       3.63);
		areaIMC.put(Trade.T2_1$2,  5.135);
		areaIMC.put(Trade.T3,      7.922);
		areaIMC.put(Trade.T3_1$2, 10.584);
		areaIMC.put(Trade.T4,     13.631);
		//endregion

		//region LFNCA
		areaLFNCA = new HashMap<>();
		areaLFNCA.put(Trade.T3$8,   0.192);
		areaLFNCA.put(Trade.T1$2,   0.312);
		areaLFNCA.put(Trade.T3$4,   0.535);
		areaLFNCA.put(Trade.T1,     0.854);
		areaLFNCA.put(Trade.T1_1$4, 1.502);
		areaLFNCA.put(Trade.T1_1$2, 2.018);
		areaLFNCA.put(Trade.T2,     3.343);
		//endregion

		//region LFNCB
		areaLFNCB = new HashMap<>();
		areaLFNCB.put(Trade.T3$8,   0.192);
		areaLFNCB.put(Trade.T1$2,   0.314);
		areaLFNCB.put(Trade.T3$4,   0.541);
		areaLFNCB.put(Trade.T1,     0.873);
		areaLFNCB.put(Trade.T1_1$4, 1.528);
		areaLFNCB.put(Trade.T1_1$2, 1.981);
		areaLFNCB.put(Trade.T2,     3.246);
		//endregion

		//region LFMC
		areaLFMC = new HashMap<>();
		areaLFMC.put(Trade.T3$8,   0.192);
		areaLFMC.put(Trade.T1$2,   0.314);
		areaLFMC.put(Trade.T3$4,   0.541);
		areaLFMC.put(Trade.T1,     0.873);
		areaLFMC.put(Trade.T1_1$4, 1.528);
		areaLFMC.put(Trade.T1_1$2, 1.981);
		areaLFMC.put(Trade.T2,     3.246);
		areaLFMC.put(Trade.T2_1$2, 4.881);
		areaLFMC.put(Trade.T3,     7.475);
		areaLFMC.put(Trade.T3_1$2, 9.731);
		areaLFMC.put(Trade.T4,    12.692);
		//endregion

		//region RMC
		areaRMC = new HashMap<>();
		areaRMC.put(Trade.T1$2,   0.314);
		areaRMC.put(Trade.T3$4,   0.549);
		areaRMC.put(Trade.T1,     0.887);
		areaRMC.put(Trade.T1_1$4, 1.526);
		areaRMC.put(Trade.T1_1$2, 2.071);
		areaRMC.put(Trade.T2,     3.408);
		areaRMC.put(Trade.T2_1$2, 4.866);
		areaRMC.put(Trade.T3,     7.499);
		areaRMC.put(Trade.T3_1$2, 10.01);
		areaRMC.put(Trade.T4,    12.882);
		areaRMC.put(Trade.T5,    20.212);
		areaRMC.put(Trade.T6,    29.158);
		//endregion

		//region PVC80
		areaPVC80 = new HashMap<>();
		areaPVC80.put(Trade.T1$2,   0.217);
		areaPVC80.put(Trade.T3$4,   0.409);
		areaPVC80.put(Trade.T1,     0.688);
		areaPVC80.put(Trade.T1_1$4, 1.237);
		areaPVC80.put(Trade.T1_1$2, 1.711);
		areaPVC80.put(Trade.T2,     2.874);
		areaPVC80.put(Trade.T2_1$2, 4.119);
		areaPVC80.put(Trade.T3,     6.442);
		areaPVC80.put(Trade.T3_1$2, 8.688);
		areaPVC80.put(Trade.T4,    11.258);
		areaPVC80.put(Trade.T5,    17.855);
		areaPVC80.put(Trade.T6,    25.598);
		//endregion

		//region PVC40
		areaPVC40 = new HashMap<>();
		areaPVC40.put(Trade.T1$2,   0.285);
		areaPVC40.put(Trade.T3$4,   0.508);
		areaPVC40.put(Trade.T1,     0.832);
		areaPVC40.put(Trade.T1_1$4, 1.453);
		areaPVC40.put(Trade.T1_1$2, 1.986);
		areaPVC40.put(Trade.T2,     3.291);
		areaPVC40.put(Trade.T2_1$2, 4.695);
		areaPVC40.put(Trade.T3,     7.268);
		areaPVC40.put(Trade.T3_1$2, 9.737);
		areaPVC40.put(Trade.T4,    12.554);
		areaPVC40.put(Trade.T5,    19.761);
		areaPVC40.put(Trade.T6,    28.567);
		//endregion

		//region HDPE
		areaHDPE = new HashMap<>();
		areaHDPE.put(Trade.T1$2,   0.285);
		areaHDPE.put(Trade.T3$4,   0.508);
		areaHDPE.put(Trade.T1,     0.832);
		areaHDPE.put(Trade.T1_1$4, 1.453);
		areaHDPE.put(Trade.T1_1$2, 1.986);
		areaHDPE.put(Trade.T2,     3.291);
		areaHDPE.put(Trade.T2_1$2, 4.695);
		areaHDPE.put(Trade.T3,     7.268);
		areaHDPE.put(Trade.T3_1$2, 9.737);
		areaHDPE.put(Trade.T4,    12.554);
		areaHDPE.put(Trade.T5,    19.761);
		areaHDPE.put(Trade.T6,    28.567);
		//endregion

		//region PVCA
		areaPVCA = new HashMap<>();
		areaPVCA.put(Trade.T1$2,    0.385);
		areaPVCA.put(Trade.T3$4,     0.65);
		areaPVCA.put(Trade.T1,      1.084);
		areaPVCA.put(Trade.T1_1$4,  1.767);
		areaPVCA.put(Trade.T1_1$2,  2.324);
		areaPVCA.put(Trade.T2,      3.647);
		areaPVCA.put(Trade.T2_1$2,  5.453);
		areaPVCA.put(Trade.T3,      8.194);
		areaPVCA.put(Trade.T3_1$2, 10.694);
		areaPVCA.put(Trade.T4,     13.723);
		//endregion

		//region PVCEB
		areaPVCEB = new HashMap<>();
		areaPVCEB.put(Trade.T2,      3.874);
		areaPVCEB.put(Trade.T3,      8.709);
		areaPVCEB.put(Trade.T3_1$2, 11.365);
		areaPVCEB.put(Trade.T4,     14.448);
		areaPVCEB.put(Trade.T5,     22.195);
		areaPVCEB.put(Trade.T6,      31.53);
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

	/**
	 Returns the conduit material corresponding to the given index.

	 @param conduitTypeIndex The index of the conduit material.
	 <br> 0 = PVC, 1 = aluminum and other value = steel.

	 @return The requested material if the given index correspond to a material,
	 null otherwise.
	 @see Material
	 */
	public static Material getConduitMaterialPerIndex(int conduitTypeIndex){
		if(conduitTypeIndex < Material.values().length)
			return Material.values()[conduitTypeIndex];
		return null;
	}

	/**
	 Asks if the given conduit string type is valid, that is, if it's registered
	 in the conduit type string list.

	 @param conduitType The requested conduit string type.
	 @return True if it's a valid conduit type, false otherwise.
	 */
	public static boolean isValidType(String conduitType){
		return getTypeByString(conduitType) != null;
	}

	/**
	 Asks if the giving string name correspond to a valid trade size. Proper
	 string names are described in the {@link eecalcs.conductors.Size Size}
	 enum.

	 @param tradeSize The requested string name.
	 @return True if the given string correspond to a valid trade size; false
	 otherwise.
	 */
	public static boolean isValidTrade(String tradeSize){
		return getTradeSizeByString(tradeSize) != null;
	}

	/**
	 Asks if the given conduit type and trade size has an internal area, that
	 is, if the type and trade size have an entry in NEC 2014 Table 4.

	 @param conduitType The type of conduit.
	 @param tradeSize The trade size of the conduit.
	 @return True if the requested conduit type and trade size have an internal
	 area in table 4.
	 */
	public static boolean hasArea(Type conduitType, Trade tradeSize){
		if(conduitType == null || tradeSize == null)
			return false;
		return dimensions.get(conduitType).containsKey(tradeSize);
	}

	/**
	 Gets the area of the given conduit type and trade size.

	 @param conduitType  The type of conduit.
	 @param tradeSize The size of the conduit.
	 @return The area in square inches of the conduit or zero if not in NEC 2014
	 table 4.
	 */
	public static double getArea(Type conduitType, Trade tradeSize){
		if(hasArea(conduitType, tradeSize))
			return dimensions.get(conduitType).get(tradeSize);
		return 0;
	}

	/**
	 @return The map of trade-size-and-area pair values corresponding to the
	 given conduit type.
	 @param conduitType The conduit type for which the areas are requested.
	 */
	public static Map<Trade, Double> getAreasForType(Type conduitType){
		return dimensions.get(conduitType);
	}

	/**
	 Returns the conduit type of the given conduit string type.

	 @param conduitTypeS The conduit string type.
	 @return The conduit type if the given string is valid, null otherwise.
	 @see Type
	 */
	public static Type getTypeByString(String conduitTypeS){
		conduitTypeS = conduitTypeS.trim();
		for(Type type: Type.values()){
			if(type.getName().equals(conduitTypeS))
				return type;
		}
		return null;
	}

	/**
	 Returns the conduit trade size of the given conduit string trade size.

	 @param tradeSizeS The conduit string trade size.
	 @return The trade size. If the given string is not valid, the returned
	 value is Trade.INVALID.
	 */
	public static Trade getTradeSizeByString(String tradeSizeS){
		tradeSizeS = tradeSizeS.trim();
		for(Trade trade: Trade.values()){
			if(trade.getName().equals(tradeSizeS))
				return trade;
		}
		return null;
	}

	/**
	 * Asks is the given conduit material is magnetic.
	 * @param material The material of the conduit.
	 * @return True if the material is magnetic, false otherwise.
	 * @see Material
	 */
	public static boolean isMagnetic(Material material){
		if(material == Material.PVC | material == Material.ALUMINUM) return nonMagnetic;
		return Magnetic;
	}

	/**
	 * Asks is the given conduit type is magnetic.
	 * @param conduitType The material of the conduit.
	 * @return True if the material is magnetic, false otherwise.
	 * @see Type
	 */
	public static boolean isMagnetic(Type conduitType){
		if(conduitType == Type.EMT | conduitType == Type.FMC | conduitType == Type.IMC | conduitType == Type.LFMC | conduitType == Type.RMC) return Magnetic;
		return nonMagnetic;
	}

	/**
	 * Returns the material (aluminum, steel or pvc) of the given conduit type.
	 * @param type The conduit type as defined in {@link Type} for which the material is requested.
	 * @return The requested material.
	 */
	public static Material getMaterial(Type type){
		if(type == Type.EMTAL | type == Type.FMCAL | type == Type.LFMCAL | type == Type.RMCAL)
			return Material.ALUMINUM;
		else if(type == Type.EMT | type == Type.FMC | type == Type.IMC | type == Type.LFMC | type == Type.RMC)
			return Material.STEEL;
		return Material.PVC;
	}

	/**
	 @return The smaller trade size whose internal area is bigger than the
	 given area and tha is equal or bigger than the given minimum trade size.
	 Returns null if the given area is too big for the specified type.
	 @param area The area for which a trade size is requested.
	 @param type The type of the conduit.
	 @param minimumTradeSize The minimum trade size desired. If this value is
	 null, the minimum trade size is the smallest trade size (T3$8).
	 */
	public static Trade  getTradeSizeForArea(double area, Type type,
	                                         Trade minimumTradeSize){
		Map<Trade, Double> areasForType =
				ConduitProperties.getAreasForType(type);
		int bound = minimumTradeSize == null? 0: minimumTradeSize.ordinal();
		for (int i = bound; i < Trade.values().length; i++)
			if (ConduitProperties.hasArea(type, Trade.values()[i])) {
				if (areasForType.get(Trade.values()[i]) >= area)
					return Trade.values()[i];
			}
		return null;
	}
}
