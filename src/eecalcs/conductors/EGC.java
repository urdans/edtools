package eecalcs.conductors;

import org.jetbrains.annotations.NotNull;

public class EGC {
	static class TableItem {
		int ocpdRating;
		Size copperSize;
		Size aluminiumSize;
		TableItem(int ocpdRating, @NotNull Size copperSize, @NotNull Size aluminiumSize){
			this.ocpdRating = ocpdRating;
			this.copperSize = copperSize;
			this.aluminiumSize = aluminiumSize;
		}
	};
	/** Table 250.122 sizing of the copper and aluminum EGC. The non-standard size (1200) which is repeated twice for
	 aluminum conductor is replaced with 1250.*/
	private static final TableItem[] table = {
			new TableItem(15, Size.AWG_14, Size.AWG_12),
			new TableItem(20, Size.AWG_12, Size.AWG_10),
			new TableItem(60, Size.AWG_10, Size.AWG_8),
			new TableItem(100, Size.AWG_8, Size.AWG_6),
			new TableItem(200, Size.AWG_6, Size.AWG_4),
			new TableItem(300, Size.AWG_4, Size.AWG_2),
			new TableItem(400, Size.AWG_3, Size.AWG_1),
			new TableItem(500, Size.AWG_2, Size.AWG_1$0),
			new TableItem(600, Size.AWG_1, Size.AWG_2$0),
			new TableItem(800, Size.AWG_1$0, Size.AWG_3$0),
			new TableItem(1000, Size.AWG_2$0, Size.AWG_4$0),
			new TableItem(1200, Size.AWG_3$0, Size.KCMIL_250),
			new TableItem(1600, Size.AWG_4$0, Size.KCMIL_350),
			new TableItem(2000, Size.KCMIL_250, Size.KCMIL_400),
			new TableItem(2500, Size.KCMIL_350, Size.KCMIL_600),
			new TableItem(3000, Size.KCMIL_400, Size.KCMIL_600),
			new TableItem(4000, Size.KCMIL_500, Size.KCMIL_750),
			new TableItem(5000, Size.KCMIL_700, Size.KCMIL_1250),
			new TableItem(6000, Size.KCMIL_800, Size.KCMIL_1250)
	};

	/**
	 @return The size of the EGC per table NEC-250.122, for the given OCPD rating and metal.
	 @param ocpdRating Rating of the OCPD. Must be in the range [15, 6000] (minimum and maximum standard OCPD
	 ratings), otherwise an {@link IllegalArgumentException} is thrown.
	 @param metal The metal of the conductor.
	 */
	public static Size getEGCSize(int ocpdRating, @NotNull Metal metal){
		if(ocpdRating < 15 || ocpdRating > 6000)
			throw new IllegalArgumentException("The OCPD rating must be in the range [15, 6000]");
		int index = 0;
		for (int i = table.length-1; i > 0 ; i--) {
			if (ocpdRating >= table[i].ocpdRating) {
				index = i;
				break;
			}

		}
		if(metal == Metal.COPPER)
			return table[index].copperSize;
		return table[index].aluminiumSize;
	}
}
