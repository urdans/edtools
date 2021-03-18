package eecalcs.loads;
/**
 Enums to represent horsepower sizes for electrical motors and their string
 representation.
 <br>
 <ul>
 <li><b>HP_1$6</b>: "1/6 HP"</li>
 <li><b>HP_1$4</b>: "1/4 HP"</li>
 <li><b>HP_1$3</b>: "1/3 HP"</li>
 <li><b>HP_1$2</b>: "1/2 HP"</li>
 <li><b>HP_3$4</b>: "3/4 HP"</li>
 <li><b>HP_1</b>: "1 HP"</li>
 <li><b>HP_1_1$2</b>: "1-1/2 HP"</li>
 <li><b>HP_2</b>: "2 HP"</li>
 <li><b>HP_3</b>: "1 HP"</li>
 <li><b>HP_5</b>: "5 HP"</li>
 <li><b>HP_7_1$2</b>: "7-1/2 HP"</li>
 <li><b>HP_10</b>: "10 HP"</li>
 <li><b>HP_15</b>: "15 HP"</li>
 <li><b>HP_20</b>: "20 HP"</li>
 <li><b>HP_25</b>: "25 HP"</li>
 <li><b>HP_30</b>: "30 HP"</li>
 <li><b>HP_40</b>: "40 HP"</li>
 <li><b>HP_50</b>: "50 HP"</li>
 <li><b>HP_60</b>: "60 HP"</li>
 <li><b>HP_75</b>: "75 HP"</li>
 <li><b>HP_100</b>: "100 HP"</li>
 <li><b>HP_125</b>: "125 HP"</li>
 <li><b>HP_150</b>: "150 HP"</li>
 <li><b>HP_200</b>: "200 HP"</li>
 <li><b>HP_250</b>: "250 HP"</li>
 <li><b>HP_300</b>: "300 HP"</li>
 <li><b>HP_350</b>: "350 HP"</li>
 <li><b>HP_400</b>: "400 HP"</li>
 </ul>
 */
public enum Horsepower {
		HP_1$6("1/6"),
		HP_1$4("1/4"),
		HP_1$3("1/3"),
		HP_1$2("1/2"),
		HP_3$4("3/4"),
		HP_1("1"),
		HP_1_1$2("1-1/2"),
		HP_2("2"),
		HP_3("3"),
		HP_5("5"),
		HP_7_1$2("7-1/2"),
		HP_10("10"),
		HP_15("15"),
		HP_20("20"),
		HP_25("25"),
		HP_30("30"),
		HP_40("40"),
		HP_50("50"),
		HP_60("60"),
		HP_75("75"),
		HP_100("100"),
		HP_125("125"),
		HP_150("150"),
		HP_200("200"),
		HP_250("250"),
		HP_300("300"),
		HP_350("350"),
		HP_400("400"),
		HP_450("450"),
		HP_500("500");
		private final String name;
		private static final String[] names;

		static{
			names = new String[values().length];
			for(int i=0; i<values().length; i++)
				names[i] = values()[i].getName();
		}

	Horsepower(String name){
			this.name = name;
		}

		/**
		 Returns the string name that this enum represents.

		 @return The string name.
		 */
		public String getName() {
			return name;
		}

		/**
		 Returns an array of the string names that the enum values represent.

		 @return An array of strings
		 */
		public static String[] getNames(){
			return names;
		}
}
