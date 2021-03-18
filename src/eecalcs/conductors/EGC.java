package eecalcs.conductors;

public class EGC {
	/**Table 250.122, rating of OCPD for sizing EGC*/
	private static final int[] ocpdEGC = {
			15, 20, 60, 100, 200,
			300, 400, 500, 600, 800,
			1000, 1200, 1600, 2000,
			2500, 3000, 4000, 5000,
			6000
	};
	/**Table 250.122 sizing of the copper EGC. It omits 300, 600 and 750.*/
	private static final Size[] copperEGC = {
			Size.AWG_14, Size.AWG_12, Size.AWG_10, Size.AWG_8, Size.AWG_6,
			Size.AWG_4, Size.AWG_3, Size.AWG_2, Size.AWG_1, Size.AWG_1$0,
			Size.AWG_2$0, Size.AWG_3$0, Size.AWG_4$0, Size.KCMIL_250,
			Size.KCMIL_350,	Size.KCMIL_400,	Size.KCMIL_500,	Size.KCMIL_700,
			Size.KCMIL_800
	};
	/**Table 250.122 sizing of the aluminum EGC. It omits 14, 3, 300, 500,
	800, 900, 1000; it repeats 600 and includes a non standard size (1200)
	twice, which is replaced in this software with 1250 and 1500.*/
	private static final Size[] aluminumEGC = {
			Size.AWG_12, Size.AWG_10, Size.AWG_8, Size.AWG_6, Size.AWG_4,
			Size.AWG_2, Size.AWG_1, Size.AWG_1$0, Size.AWG_2$0, Size.AWG_3$0,
			Size.AWG_4$0, Size.KCMIL_250, Size.KCMIL_350, Size.KCMIL_400,
			Size.KCMIL_600, Size.KCMIL_600, Size.KCMIL_750, Size.KCMIL_1250,
			Size.KCMIL_1500
	};

	/**
	 @return The index of the OCPD in table 250.122 that is equal or less
	 than the rating passed in the parameter and that if bigger than the
	 previous element in the table. Returns -1 if the rating passed exceeds
	 6000A.
	 For example, if the ocpd rating is 55, it returns the index of the ocpd
	 corresponding to 60.
	 @param ocpdRating the rating to search for.
	 */
	private static int indexOfOCPDInTable250_122(int ocpdRating){
		ocpdRating = Math.abs(ocpdRating);
		if(ocpdRating <= 15)
			return 0;
		for(int index = 0; index < ocpdEGC.length - 1; index++){
			int prev = ocpdEGC[index];
			int next = ocpdEGC[index + 1];
			if(ocpdRating == prev)
				return index;
			if(ocpdRating > prev && ocpdRating <= next)
				return index + 1;
		}
		return -1;
	}

	/**
	 @return The size of the EGC per table NEC-250.122. If any of the
	 parameters is invalid the return value is null.
	 @param ocpdRating Rating of the OCPD.
	 @param metal The metal of the conductor (Cu or Al)
	 */
	public static Size getEGCSize(int ocpdRating, Metal metal){
		if(metal == null)
			return null;
		if(ocpdRating == 0)
			return null;
		int index = indexOfOCPDInTable250_122(ocpdRating);
		if(index == -1)
			return null;
		if(metal == Metal.COPPER)
			return copperEGC[index];
		return aluminumEGC[index];
	}
}
