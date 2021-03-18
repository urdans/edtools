package tools;

/**
 Class with static methods to help working with arrays
 */
public class ArrayTools {

	public static  <E, T> int getIndexOf(T[] array, E element){
		for(int i = 0; i < array.length; i++){
			if(array[i].equals(element))
				return i;
		}
		return -1;
	}

	public static int getIndexOf(int[] array, int element){
		for(int i = 0; i < array.length; i++){
			if(array[i] == element)
				return i;
		}
		return -1;
	}
}
