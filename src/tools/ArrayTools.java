package tools;

import java.util.stream.IntStream;

/**
 Class with static methods to help working with arrays
 */
public class ArrayTools {

	public static  <E, T> int getIndexOf(T[] array, E element){
		return IntStream.range(0, array.length).filter(i -> array[i].equals(element)).findFirst().orElse(-1);
	}

	public static int getIndexOf(int[] array, int element){
		return IntStream.range(0, array.length).filter(i -> array[i] == element).findFirst().orElse(-1);
	}
}
