package eecalcs.conductors;

import com.fasterxml.jackson.annotation.JsonProperty;
import eecalcs.bundle.Bundle;
import eecalcs.conduits.Conduit;
import eecalcs.systems.NEC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.JSONTools;
import tools.Tools;

/**
 Encapsulates the properties and methods for a single conductor in the context
 of its ambient temperature, its length, its {@link Conduit conduit} or
 {@link Bundle bundle} and its {@link Role role} or usage.
 <p>Generally, conductors are installed in raceways, that is, in conduits,
 boxes, wireways and cellular concrete floor raceways; busways and cablebuses
 are special cases of conductor-raceway combos. The raceway used in this class
 is the conduit.
 <p>In some rare conditions, conductor are installed grouped in bundles; that
 condition is accounted for by this class.

 The default values for a conductor are:
 <p>- Size: 12 AWG
 <p>- ConductiveMaterial: Copper
 <p>- Insulation: THW
 <p>- Length: 100 feet (always given in feet)
 <p>- Ambient temperature = 86 °F (Always given in degrees Fahrenheit).
 <p>- Coating = no coating (meaningful for CU conductors only).
 <p>- Conduit = null (conductor is in free air).
 <p>- Bundle = null (conductor not grouped or bundled).
 <p>- Role = HOT.
 */
public class Conductor implements Conduitable, RWConduitable {
	//region params
	private @NotNull Size size = Size.AWG_12;
	private @NotNull ConductiveMaterial conductiveMaterial = ConductiveMaterial.COPPER;
	private @NotNull Insulation insulation  = Insulation.THW;
	private double length = 100;
	private int ambientTemperatureF = 86;
	private @NotNull Role role = Role.HOT;
	private @Nullable Conduit conduit = null;
	private @Nullable Bundle bundle = null;
	//endregion

	/**
	 Defines the role of a conductor.
	 <ul>
	 <li><b>HOT</b>: means the conductor is a phase conductor (an ungrounded
	 conductor as defined by the NEC)</li>
	 <li><b>NEUCC</b>: means the conductor is a neutral conductor (a grounded
	 conductor as defined by the NEC) that is also a current-carrying
	 conductor per NEC rules 2014,2017:310.15(B)(5); 2020:310.15(E) </li>
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
	 conductor, the system voltages can be considered as follows:
	 <ol>
	 <li>120v 1Ø: always 2w. Neutral is CCC.</li>
	 <li>208v 1Ø: 2w:no neutral; 3w: always CCC.</li>
	 <li>208v 3Ø: 3w:no neutral; 4w:neutral present but no CCC unless more
	 than 50% of the load is non-linear (harmonics).</li>
	 <li>240v 1Ø: 2w:no neutral; 3w: neutral present, non CCC.</li>
	 <li>240v 3Ø: 3w:no neutral; 4w: neutral present, non CCC unless more
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
		NCONC("Hot, ungrounded non concurrent conductor"),
		SPARE("Spare conductors for future use");

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

	/** Construct a Conductor object with all the default values.*/
	public Conductor() {}

	/**
	 @return The role of this conductor.
	 */
	public @NotNull Role getRole() {
		return role;
	}

	/**
	 Copy all the properties of the given conductor to this conductor, except for the conduit and bundle parameters
	 @param conductor The conductor to copy from. Cannot be null.
	 @return This conductor.
	 */
	public Conductor copyFrom(@NotNull Conductor conductor){
		size = conductor.size;
		conductiveMaterial = conductor.conductiveMaterial;
		insulation =conductor.insulation;
		length = conductor.length;
		ambientTemperatureF = conductor.ambientTemperatureF;
		role = conductor.role;
		return this;
	}

	/**
	 Sets the size of this conductor.
	 @param size The new size for this conductor. Cannot be null.
	 @return This conductor.
	 */
	public Conductor setSize(@NotNull Size size){
		this.size = size;
		return this;
	}

	/**
	 Sets the conductiveMaterial of this conductor.
	 @param conductiveMaterial The new conductiveMaterial type for this conductor. Cannot be null.
	 @return This conductor.
	 */
	public Conductor setMetal(@NotNull ConductiveMaterial conductiveMaterial){
		this.conductiveMaterial = conductiveMaterial;
		return this;
	}

	/**
	 Sets the insulation for this conductor.
	 @param insulation The new insulation for this conductor. Cannot be null.
	 @return This conductor.
	 */
	public Conductor setInsulation(@NotNull Insulation insulation){
		this.insulation = insulation;
		return this;
	}

	/**
	 Sets the length of this conductor.
	 @param length the new length of this conductor, in feet. Must be >=0.
	 @return This conductor.
	 */
	public Conductor setLength(double length){
		if(length <= 0)
			throw new IllegalArgumentException("Length of a conductor cannot be <=0");
		this.length = length;
		return this;
	}

	/**
	 Sets the ambient temperature of this conductor. This parameter can only be assigned if the conductor does not
	 belong to a conduit or to a bundle, otherwise an IllegalArgumentException is thrown. Keep in mind that a
	 conduit/bundle object can assign this temperature when this conductor belongs to that conduit/bundle.
	 <p> {@link #hasConduit()} and {@link #hasBundle()} can be used to test the condition of this conductor.

	 @param ambientTemperatureF The ambient temperature in degrees Fahrenheits. The ambient temperature must be in
	 the [{@link Factors#MIN_TEMP_F}, {@link Factors#MAX_TEMP_F}] °F range.
	 @return This Conductor.
	 */
	public Conductor setAmbientTemperatureF(int ambientTemperatureF){
		setAmbientTemperatureF2(ambientTemperatureF);
		return this;
	}

	/**
	 Same as {@link #setAmbientTemperatureF(int)} except that this method does not return anything.
	 */
	@Override
	public void setAmbientTemperatureF2(int ambientTemperatureF){
		if(hasConduit() || hasBundle())
			throw new IllegalArgumentException("Ambient temperature cannot be" +
					" assigned to a conductor that belongs to a conduit or " +
					"to a bundle. Use the conduit or bundle to set the " +
					"temperature of this conductor.");
		if(ambientTemperatureF < Factors.MIN_TEMP_F || ambientTemperatureF > Factors.MAX_TEMP_F)
			throw new IllegalArgumentException("Ambient temperature must be " +
					"in the [" + Factors.MIN_TEMP_F + "," + Factors.MAX_TEMP_F + "] °F range.");
		this.ambientTemperatureF = ambientTemperatureF;
	}

	/**
	 Sets the role for this conductor.
	 @param role The new role for this conductor. Cannot be null.
	 @return This conductor.
	 */
	public Conductor setRole(@NotNull Role role){
		this.role = role;
		return this;
	}

	/**
	 Sets the conduit for this conductor. This method can only be called from the Conduit object that will
	 contain this conductor; a call from other objects will throw an IllegalCallerException.
	 Once this conductor is set in a conduit, it cannot be changed.
	 @param conduit The conduit this conductor will belong to.
	 */
	public Conductor setConduit(@Nullable Conduit conduit){
		if(Tools.getClassName(Thread.currentThread().getStackTrace()[2].getClassName()).equals("Conduit"))
			this.conduit = conduit;
		else
			throw new IllegalCallerException("setConduit method cannot be called from outside of a Conduit object.");
		return this;
	}

	@Override
	public void setConduit2(@Nullable Conduit conduit) {
		if(Tools.getClassName(Thread.currentThread().getStackTrace()[2].getClassName()).equals("Conduit"))
			this.conduit = conduit;
		else
			throw new IllegalCallerException("setConduit2 method cannot be called from outside of a Conduit object.");
	}

	/**
	 Sets the bundle for this conductor. This method can only be called from the Bundle object that will
	 contain this conductor; a call from other objects will throw an IllegalCallerException.
	 Once this conductor is set in a bundle, it cannot be changed.
	 @param bundle The bundle this conductor will belong to.
	 */
	public Conductor setBundle(@NotNull Bundle bundle){
		if(Tools.getClassName(Thread.currentThread().getStackTrace()[2].getClassName()).equals("Bundle"))
			this.bundle = bundle;
		else
			throw new IllegalCallerException("setBundle method cannot be called from outside of a Bundle object.");
		return this;
	}

	@Override
	public void setBundle2(@Nullable Bundle bundle) {
		if(Tools.getClassName(Thread.currentThread().getStackTrace()[2].getClassName()).equals("Bundle"))
			this.bundle = bundle;
		else
			throw new IllegalCallerException("setBundle2 method cannot be called from outside of a Bundle object.");
	}

	/**
	 Returns a deep and convenient copy of this Conductor object. The new copy
	 is exactly the same as this conductor, except: (convenience)
	 <p>- it does not copy the conduit property, that is, the new copy is
	 assumed in free air (not in a conduit).
	 <p>- it does not copy the bundle property, that is, the new copy is
	 assumed not to be grouped with other conductors.
	 */
	public Conductor copy(){
		return new Conductor().copyFrom(this);
	}

	@Override
	public @NotNull Size getSize() {
		return size;
	}

	@Override
	public @NotNull ConductiveMaterial getMetal() {
		return conductiveMaterial;
	}

	@Override
	public @NotNull Insulation getInsulation() {
		return insulation;
	}

	@Override
	public double getLength() {
		return length;
	}

	@Override
	public double getInsulatedAreaIn2() {
		return ConductorProperties.getInsulatedConductorAreaIn2(size, insulation);
	}

	@Override
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public double getCorrectedAndAdjustedAmpacity(){
		return ConductorProperties.getStandardAmpacity(size, conductiveMaterial,
				ConductorProperties.getTempRating(insulation)) * getCompoundFactor();
	}

	@Override
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public double getCorrectionFactor(){
		return getCorrectionFactor(insulation);
	}

	/**
	 @return The correction factor for this conductor when using the given
	 insulation.
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	private double getCorrectionFactor(@NotNull Insulation insulation){
		int adjustedTemp = 0;
		if(insulation != Insulation.XHHW2) {
			if(hasConduit())
				adjustedTemp = Factors.getRoofTopTempAdder(conduit.getRooftopDistance());
		}
		return Factors.getTemperatureCorrectionF(getAmbientTemperatureF() + adjustedTemp,
				getTemperatureRating(insulation));
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public double getAdjustmentFactor() {
		if(hasConduit()) {
			if (!conduit.isNipple()) {
				return Factors.getAdjustmentFactor(conduit.getCurrentCarryingCount());
			}
		}
		if(hasBundle()){
			if(bundle.getBundlingLength() > Bundle.BUNDLE_CRITICAL_LENGTH) {
				return Factors.getAdjustmentFactor(bundle.getCurrentCarryingCount());
			}
		}
		return 1;
	}

	@Override
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public double getCompoundFactor() {
		return getCorrectionFactor() * getAdjustmentFactor();
	}

	@Override
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public double getCompoundFactor(@NotNull TempRating tempRating) {
		Insulation anInsulation;
		if(tempRating == TempRating.T60)
			anInsulation = Insulation.TW;
		else if(tempRating == TempRating.T75)
			anInsulation = Insulation.THW;
		else
			anInsulation = Insulation.THHW;

		return getCorrectionFactor(anInsulation) * getAdjustmentFactor();
	}

	@Override
	public int getAmbientTemperatureF() {
		return ambientTemperatureF;
	}

	@Override
	@JsonProperty("hasConduit")
	public boolean hasConduit() {
		return conduit != null;
	}

	@Override
	public @NotNull TempRating getTemperatureRating() {
		return getTemperatureRating(insulation);
	}

	/**
	 @return The temperature rating of this conductor as if it was using the given
	 insulation.
	 */
	private @NotNull TempRating getTemperatureRating(Insulation insulation) {
		return ConductorProperties.getTempRating(insulation);
	}

	@Override
	public int getCurrentCarryingCount() {
		if(role == Role.GND | role == Role.NEUNCC | role == Role.NCONC)
			return 0;
		return 1; //HOT & NEUCC
	}

	@Override
	public String getDescription() {
		//Example: "#12 AWG THW (CU)(HOT)"
		//Todo: make it return a better description that does not use #
		return "#" + size.getName() + " " + insulation.getName() +
				" (" + getMetal().getName() + ")(" + role + ")";
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
		//Todo: is this really needed?
		return JSONTools.toJSON(this);
	}

	@Override
	public String toString() {
		return "Conductor {" + "size=" + size + ", conductiveMaterial=" + conductiveMaterial + ", " +
				"insulation=" + insulation + ", length=" + length + ", " +
				"ambientTemperatureF=" + ambientTemperatureF + ", " +
				", role=" + role + ", " +
				"conduit=" + conduit + ", bundle=" + bundle + '}';
	}
}
