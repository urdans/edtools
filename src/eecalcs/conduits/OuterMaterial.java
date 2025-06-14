package eecalcs.conduits;

/**
 Enum structure for the conduit materials.
 <br>
 <ul>
 <li><b>PVC</b></li>
 <li><b>STEEL</b></li>
 <li><b>ALUMINUM</b></li>
 </ul>
 */
public enum OuterMaterial {
	PVC, STEEL, ALUMINUM;

	/**
	 * @return True if the outer material is magnetic.
	 */
	public boolean isMagnetic() {
		return this != PVC && this != ALUMINUM;
	}
}
