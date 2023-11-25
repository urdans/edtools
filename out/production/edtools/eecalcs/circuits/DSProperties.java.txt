package eecalcs.circuits;

import java.util.Arrays;
import java.util.List;

public class DSProperties {
	private static final List<Integer> standardRatings2 = Arrays.asList(30, 60
			, 100, 200, 400, 600, 800, 1200);

	/**
	 @param current The current for which a DS rating is requested.
	 @return The standard size that is immediately bigger that the provided
	 current. If the current is higher than 1200, the return value is zero,
	 meaning there is no standard rating to that value of current.
	 */
	public static int getRating(double current){
		double cur = Math.abs(current);
		return standardRatings2.stream()
				.filter((sr)-> sr >= cur)
				.findFirst().orElse(0);
	}
}
