package eecalcs.conductors.raceways;

import com.fasterxml.jackson.annotation.JsonProperty;
import eecalcs.conductors.*;
import eecalcs.conductors.TempRating;
import tools.JSONTools;
import tools.ROResultMessages;
import tools.ResultMessage;
import tools.ResultMessages;

/**
 Encapsulates the properties and methods for a single conductor in the context
 of its ambient temperature, its length, its {@link Conduit conduit} or
 {@link Bundle bundle} and its {@link Role role} or usage.
 <p>Generally, conductors are installed in raceways, that is, in conduits,
 boxes, wireways and cellular concrete floor raceways; busways and cablebuses
 are special cases of conductor-raceway combos. The raceway used in this class
 is the conduit. Boxes and wireways are treated by its corresponding classes.
 <p>In some rare conditions, conductor are installed grouped in bundles; that
 condition is accounted for by this class.

 The default values for a conductor are:
 <p>- Size: 12 AWG
 <p>- Metal: Copper
 <p>- Insulation: THW
 <p>- Length: 100 feet.
 <p>- Ambient temperature = 86°F.
 <p>- Coating = no coating (meaningful for CU conductors only).
 <p>- Conduit = null (conductor is in free air).
 <p>- Bundle = null (conductor not grouped or bundled).
 <p>- Role = HOT.

 The constructor will not accept inconsistent parameters (nullity, zero,
 negative, out of valid range, etc.); however, an object will always be
 created. The caller must check for the result messages property for any
 error or warning messages.
 */
public class Conductor implements Conduitable {
	//region params
	private Size size = Size.AWG_12;
	private Metal metal  = Metal.COPPER;
	private Insul insulation  = Insul.THW;
	private double length = 100;
	private int ambientTemperatureF = 86;
	private Coating copperCoating = Coating.UNCOATED;
	private Role role = Role.HOT;
	private Conduit conduit = null;
	private Bundle bundle = null;
	//endregion

	private final ResultMessages resultMessages = new ResultMessages();

	//region Predefined messages
	public static final ResultMessage ERROR050 = new ResultMessage("Size " +
		"parameter cannot be null.",-50);
	public static final ResultMessage ERROR051 = new ResultMessage("Metal " +
		"parameter for phases and neutral conductors cannot be null.",-51);
	public static final ResultMessage ERROR052 = new ResultMessage(
		"Insulation parameter cannot be null.",-52);
	public static final ResultMessage ERROR053 = new ResultMessage("Length " +
		"parameter cannot be <=0.",-53);
	public static final ResultMessage ERROR054 = new ResultMessage("Ambient " +
		"temperature parameter must be >= 5°F and <= 185°F.",-54);
	public static final ResultMessage ERROR055 = new ResultMessage("Coating " +
		"parameter cannot be null.",-55);
	public static final ResultMessage ERROR056 = new ResultMessage("Role " +
		"parameter cannot be null.",-56);
	public static final ResultMessage ERROR057 = new ResultMessage("Conduit " +
		"parameter cannot be null.",-57);
	public static final ResultMessage ERROR058 = new ResultMessage("Metal " +
		"parameter for grounding conductor cannot be null.", -58);
	public static final ResultMessage ERROR059 = new ResultMessage("Cable " +
		"type parameter cannot be null.",-59);
	public static final ResultMessage ERROR061 = new ResultMessage("Bundle " +
		"parameter cannot be null.",-61);
	public static final ResultMessage ERROR062 = new ResultMessage(
		"Phase size parameter cannot be null.",-62);
	public static final ResultMessage ERROR063 = new ResultMessage(
		"Neutral size parameter cannot be null.",-63);
	public static final ResultMessage ERROR064 = new ResultMessage(
		"Ground size parameter cannot be null.",-64);
	//endregion

	/**
	 Defines the role of a conductor.
	 <ul>
	 <li><b>HOT</b>: means the conductor is a phase conductor (an ungrounded
	 conductor as defined by the NEC)</li>
	 <li><b>NEUCC</b>: means the conductor is a neutral conductor (a grounded
	 conductor as defined by the NEC) that is also a current-carrying
	 conductor per NEC rule 310.15(B)(5)</li>
	 <li><b>NEUNCC</b>: means the conductor is a neutral conductor that is also
	 a non current-carrying conductor</li>
	 <li><b>GND</b>: means the conductor is used for grounding and bonding (EGC,
	 GEC, bonding jumpers, etc.)</li>
	 <li><b>NCONC</b>: means the conductor is a phase conductor that is not used
	 concurrently with other conductors, like the travelers in a 3-way circuit
	 for illumination.</li>
	 </ul>
	 <p>Before assigning it, the role of a conductor must be determined, by
	 evaluating the load, and the voltage system.<br>
	 For example, to determine if a neutral conductor is a current-carrying
	 conductor, the system voltages can be considered as follow:
	 <ol>
	 <li>120v 1Ø: always 2w. Neutral is CCC.</li>
	 <li>208v 1Ø: 2w:no neutral; 3w: always CCC.</li>
	 <li>208v 3Ø: 3w:no neutral; 4w:neutral present but no CCC unless more
	 than 50% of the load is non-linear (harmonics).</li>
	 <li>240v 1Ø: 2w:no neutral; 3w: always CCC.</li>
	 <li>240v 3Ø: 3w:no neutral; 4w:neutral present but no CCC unless more
	 than 50% of the load is non-linear (harmonics).</li>
	 <li>277v 1Ø: always 2w. Neutral is CCC.</li>
	 <li>480v 1Ø: 2w:no neutral; 3w: always CCC.</li>
	 <li>480v 3Ø: 3w:no neutral; 4w:neutral present but no CCC unless more
	 than 50% of the load is non-linear (harmonics).</li>
	 </ol>
	 */
	public enum Role{
		HOT("Hot, ungrounded conductor"),
		NEUCC("Neutral, grounded current-carrying conductor"),
		NEUNCC("Neutral, grounded non current-carrying conductor"),
		GND("Grounding and bonding conductor"),
		NCONC("Hot, ungrounded non concurrent conductor");

		private final String description;
		private static final String[] descriptions;

		static{
			descriptions = new String[values().length];
			for(int i=0; i<values().length; i++)
				descriptions[i] = values()[i].getDescription();
		}

		Role(String description){
			this.description = description;
		}

		/**
		 @return The string description of the role this enum represents.
		 */
		public String getDescription(){
			return description;
		}

		/**
		 @return An array with the role description strings that the enum values
		 represent.
		 */
		public static String[] getDescriptions(){
			return descriptions;
		}
	}

	/**
	 @return The role of this conductor.
	 */
	public Role getRole() {
		return role;
	}

	/**
	 @return A read-only version of the error and warning messages of this object.
	 */

	public ROResultMessages getResultMessages() {
		return resultMessages;
	}

	/**
	 Copy all the properties of the given conductor to this conductor.
	 @param conductor The conductor to copy from.
	 @return This conductor.
	 */
	public Conductor copyFrom(Conductor conductor){
		size = conductor.size;
		metal = conductor.metal;
		insulation =conductor.insulation;
		length = conductor.length;
		ambientTemperatureF = conductor.ambientTemperatureF;
		copperCoating = conductor.copperCoating;
		role = conductor.role;
		return this;
	}

	/**
	 Sets the size of this conductor.
	 @param size The given size.
	 @return This conductor. If the given size is null an error #50 is stored.
	 */
	public Conductor setSize(Size size){
		if(size == null)
			resultMessages.add(ERROR050);
		else
			resultMessages.remove(ERROR050);
		this.size = size;
		return this;
	}

	/**
	 Sets the metal of this conductor.
	 @param metal The given metal type.
	 @return This conductor. If the given metal is null an error #51 is stored.
	 */
	public Conductor setMetal(Metal metal){
		if(metal == null)
			resultMessages.add(ERROR051);
		else
			resultMessages.remove(ERROR051);
		this.metal = metal;
		return this;
	}

	/**
	 Sets the insulation for this conductor.
	 @param insulation The given insulation.
	 @return This conductor. If the given insulation is ull an error #52 is
	 stored.
	 */
	public Conductor setInsulation(Insul insulation){
		if(insulation == null)
			resultMessages.add(ERROR052);
		else
			resultMessages.remove(ERROR052);
		this.insulation = insulation;
		return this;
	}

	/**
	 Sets the length of this conductor.
	 @param length the given length
	 @return This conductor. If the given length is less or equal to zero an
	 error #53 is stored.
	 */
	public Conductor setLength(double length){
		if(length <= 0)
			resultMessages.add(ERROR053);
		else
			resultMessages.remove(ERROR053);
		this.length = length;
		return this;
	}

	/**
	 Sets the ambient temperature of this conductor. This parameter can only
	 be assigned if the conductor does not belong to a conduit or bundle,
	 otherwise an IllegalArgumentException is thrown. Keep in mind that a
	 conduit/bundle object can assign this temperature when this conductor is
	 added to the conduit/bundle.
	 @param ambientTemperatureF The ambient temperature in degrees Fahrenheits.
	 @return This conductor. If the given temperature is less than 5 or
	 greater than 185, an error #54 is stored.
	 */
	public Conductor setAmbientTemperatureF(int ambientTemperatureF){
		if(hasConduit() || hasBundle())
			throw new IllegalArgumentException("Ambient temperature cannot be" +
					" assigned to a conductor that belongs to a conduit or " +
					"to a bundle. Use the conduit or bundle to set the " +
					"temperature of this conductor.");
		if(ambientTemperatureF < 5 || ambientTemperatureF >185)
			resultMessages.add(ERROR054);
		else
			resultMessages.remove(ERROR054);
		this.ambientTemperatureF = ambientTemperatureF;
		return this;
	}

	/**
	 Sets the copper coating for this conductor.
	 @param coating A flag indicating this conductor is copper coated or not.
	 @return This conductor. If the parameter is null, an error #55 is stored.
	 */
	public Conductor setCopperCoating(Coating coating){
		if(coating == null)
			resultMessages.add(ERROR055);
		else
			resultMessages.remove(ERROR055);
		this.copperCoating = coating;
		return this;
	}

	/**
	 Sets the role for this conductor.
	 @param role The given role
	 @return This conductor. If the given role is null an error # 56 is stored.
	 */
	public Conductor setRole(Role role){
		if(role == null)
			resultMessages.add(ERROR056);
		else
			resultMessages.remove(ERROR056);
		this.role = role;
		return this;
	}

	/**
	 Sets the conduit for this conductor. This method can only be called from
	 the Conduit class.
	 @param conduit The conduit to which this conductor will belong to.
	 */
	void setConduit(Conduit conduit){
		this.conduit = conduit;
	}

	/**
	 Sets the bundle for this conductor. This method can only be called from
	 the Bundle class.
	 @param bundle The bundle to which this conductor will belong to.
	 */
	void setBundle(Bundle bundle){
		this.bundle = bundle;
	}

	/**
	 Returns a deep and convenient copy of this Conductor object. The new copy
	 is exactly the same as this conductor, except: (convenience)
	 <p>- it does not copy the conduit property, that is, the new copy is
	 assumed in free air (not in a conduit).
	 <p>- it does not copy the bundle property, that is, the new copy is
	 assumed not grouped with other conductors.
	 */
	public Conductor copy(){
		return new Conductor().copyFrom(this);
	}

	@Override
	public Size getSize() {
		return size;
	}

	@Override
	public Metal getMetal() {
		return metal;
	}

	@Override
	public Insul getInsulation() {
		return insulation;
	}

	@Override
	public double getLength() {
		return length;
	}

	@Override
	public double getInsulatedAreaIn2() {
		if(resultMessages.hasErrors())
			return 0;
		return ConductorProperties.getInsulatedConductorAreaIn2(size, insulation);
	}

	@Override
	public double getCorrectedAndAdjustedAmpacity(){
		if(resultMessages.hasErrors())
			return 0;
		return ConductorProperties.getStandardAmpacity(size, metal,
				ConductorProperties.getTempRating(insulation)) * getCompoundFactor();
	}

	@Override
	public double getCorrectionFactor(){
		if(resultMessages.hasErrors())
			return 0;
		return getCorrectionFactor(insulation);
	}

	/**
	 @return The correction factor for this conductor when using the given
	 insulation.
	 */
	private double getCorrectionFactor(Insul insulation){
		int adjustedTemp = 0;
		if(hasConduit()) {
			adjustedTemp =
					Factors.getRoofTopTempAdjustment(conduit.getRoofTopDistance());
		}
		if(insulation == Insul.XHHW2)
			adjustedTemp = 0;
		return Factors.getTemperatureCorrectionF(getAmbientTemperatureF() + adjustedTemp,
				getTemperatureRating(insulation));
	}

	@Override
	public double getAdjustmentFactor() {
		if(resultMessages.hasErrors())
			return 0;
		if(hasConduit())
			return Factors.getAdjustmentFactor(conduit.getCurrentCarryingCount(),
					conduit.isNipple());
		if(hasBundle()){
			return Factors.getAdjustmentFactor(bundle.getCurrentCarryingCount(),
					bundle.getBundlingLength());
		}
		return 1;
	}

	@Override
	public double getCompoundFactor() {
		if(resultMessages.hasErrors())
			return 0;
		return getCorrectionFactor() * getAdjustmentFactor();
	}

	@Override
	public double getCompoundFactor(TempRating tempRating) {
		if(resultMessages.hasErrors())
			return 0;
		if(tempRating == null)
			return 1;
		Insul temp_insul;
		if(tempRating == TempRating.T60)
			temp_insul = Insul.TW;
		else if(tempRating == TempRating.T75)
			temp_insul = Insul.THW;
		else
			temp_insul = Insul.THHW;

		return getCorrectionFactor(temp_insul) * getAdjustmentFactor();
	}

	@Override
	public int getAmbientTemperatureF() {
		return ambientTemperatureF;
	}

	@Override
	public Coating getCopperCoating(){
		return copperCoating;
	}

	@Override
	@JsonProperty("hasConduit")
	public boolean hasConduit() {
		return conduit != null;
	}

	@Override
	public TempRating getTemperatureRating() {
		if(resultMessages.hasErrors())
			return null;
		return getTemperatureRating(insulation);
	}

	/**
	 @return The temperature rating of this conductor as it was using the given
	 insulation.
	 */
	private TempRating getTemperatureRating(Insul insulation) {
		return ConductorProperties.getTempRating(insulation);
	}

	@Override
	public int getCurrentCarryingCount() {
		if(resultMessages.hasErrors())
			return 0;
		if(role == Role.GND | role == Role.NEUNCC | role == Role.NCONC)
			return 0;
		return 1; //this considers hot and neutral as current carrying conductor.
	}

	@Override
	public String getDescription() {
		if(resultMessages.hasErrors())
			return "";
		//"#12 AWG THW (CU)(HOT)"
		return "#" + size.getName() + " " + insulation.getName() +
				" (" + getMetal().getSymbol() + ")(" + role + ")";
	}

	@Override
	@JsonProperty("hasBundle")
	public boolean hasBundle() {
		return bundle != null;
	}

	/**
	 @return A JSON string representing the class state plus all the results
	 of the calculations performed by this class.
	 */
	public String toJSON(){
		return JSONTools.toJSON(this);
	}

	@Override
	public String toString() {
		return "Conductor{" + "size=" + size + ", metal=" + metal + ", " +
				"insulation=" + insulation + ", length=" + length + ", " +
				"ambientTemperatureF=" + ambientTemperatureF + ", " +
				"copperCoating=" + copperCoating + ", role=" + role + ", " +
				"conduit=" + conduit + ", bundle=" + bundle + ", " +
				"resultMessages=" + resultMessages + '}';
	}
}
