package eecalcs.conductors;

/**
 This interface defines the read-only properties of the {@link Bundle}
 class.
 */
public interface ROBundle {

	/**
	 Asks if all the cables/conductor in the bundle comply with the the
	 conditions prescribed in <b>310.15(B)(3)(a)(4)</b>, as follow*:
	 <ol type="a">
	 <li>The cables are MC or AC type.</li>
	 <li>The cables do not have an overall outer jacket.</li>
	 <li>Each cable has not more than three current-carrying conductors.</li>
	 <li>The conductors are 12 AWG copper.</li>
	 <li>Not more than 20 current-carrying conductors are bundled.</li>
	 </ol>
	 Since the bundle can have different types of cables and even other single
	 conductors, the conditions must be interpreted to account or/and ignore
	 the presence of other conduitables in the bundle, as follow:
	 <ol type="a">
	 <li>Single conductors are ignored. All the cables in the bundle are
	 evaluated to comply with a.</li>
	 <li>Ignore other type of cables and all single conductors, as the only
	 ones
	 that can have an outer jacket are MC and AC cables.</li>
	 <li>Account for all other cable types, but ignore single conductors.</li>
	 <li>Account for all single conductors and conductors forming all cables
	 .</li>
	 <li>Account for all single conductors and conductors forming all cables
	 .</li>
	 </ol>
	 @return True if all above conditions are met, false otherwise.
	 */
	boolean complyWith310_15_B_3_a_4();

	/**
	 Asks if all the cables in the bundle comply with the the conditions
	 prescribed in <b>310.15(B)(3)(a)(5)</b>, as follow*:
	 <ol type="a">
	 <li>The cables are MC or AC type.</li>
	 <li>The cables do not have an overall outer jacket.</li>
	 <li>The number of current carrying conductors exceeds 20.</li>
	 <li>The bundle is longer than 24 inches.</li>
	 </ol>
	 Since the bundle can have different types of cables and even other single
	 conductors, the conditions must be interpreted to account or/and ignore
	 the
	 presence of those other conduitables in the bundle, as follow:
	 <ol type="a">
	 <li>Single conductors are ignored. All the cables in the bundle are
	 evaluated to comply with a.</li>
	 <li>Ignore other type of cables and all single conductors, as the only
	 ones
	 that can have an outer jacket are MC and AC cables.</li>
	 <li>Account for all single conductors and conductors forming all cables
	 .</li>
	 <li>Ignore all conduitables in the bundle.</li>
	 </ol>
	 @return True if all above conditions are met, false otherwise.
	 */
	boolean complyWith310_15_B_3_a_5();

	/**
	 @return The number of current-carrying conductors inside this bundle.
	 */
	int getCurrentCarryingCount();

	/**@return The number of conductors inside this bundle.*/
	int getConductorCount();

	/**
	 @return The distance or length of the bundle.
	 */
	double getBundlingLength();

	/**
	 Asks if this bundle already contains the given conduitable.
	 @param conduitable The conduitable to check if it is already contained by
	 this bundle.
	 @return True if this bundle contains it, false otherwise.
	 @see Conduitable
	 */
	boolean hasConduitable(Conduitable conduitable);

	/**
	 @return True if this bundle is empty (contains no conduitable), false
	 otherwise
	 */
	boolean isEmpty();
}
