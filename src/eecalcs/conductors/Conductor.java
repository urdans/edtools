package eecalcs.conductors;


/*todo
   -make this class immutable
   -implement param/result/builder pattern
   -where param validates input and builder throws an exception in not valid
   -that way, everytime I use a conductor object the only verification is if
   it is null.
   -the conductor and cable objects should implement conduitable. Circuit
   class should only use conduitable? think about this, maybe yes, maybe no.


*/
import eecalcs.conduits.Conduit;
import eecalcs.systems.TempRating;
import tools.NotifierDelegate;

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
 <p>This class uses the NotifierDelegate class which allows it to forecast
 messages to its registered Listeners.
 */
public class Conductor implements Conduitable, RoConductor {

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
	 The default role of a new created conductor is <b>HOT</b>, but this class
	 provides method to change it later, where required.
	 <p>The role of a conductor is determined outside of this class, by
	 evaluating other conditions. For example, to determine if a neutral
	 conductor is a current-carrying conductor, the system voltages can be
	 considered as follow:
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
		 Returns the string description of the role this enum represents.

		 @return The description string.
		 */
		public String getDescription(){
			return description;
		}

		/**
		 Returns an array with the role description strings that the enum values
		 represent.

		 @return An array of description strings.
		 */
		public static String[] getDescriptions(){
			return descriptions;
		}
	}
	private Size size = Size.AWG_12;
	private Metal metal = Metal.COPPER;
	private Insul insulation = Insul.THW;
	private double length = 100;
	private int ambientTemperatureF = 86;
	private Coating copperCoated = Coating.UNCOATED;
	private Role role = Role.HOT;
	private Conduit conduit;
	private Bundle bundle;
	private final NotifierDelegate notifier = new NotifierDelegate(this);

	/**
	 Constructs a conductor with the given characteristics. The other properties
	 values default to:
	 <p>- Ambient temperature = 86°F.
	 <p>- Coating = no coating (meaningful for CU conductors only).
	 <p>- Conduit = null (conductor is in free air).
	 <p>- Bundle = null (conductor not grouped or bundled).
	 <p>- Role = HOT.

	 @param size The size of the conductor as defined by {@link Size}
	 @param metal The conductor metal as defined by {@link Metal}
	 @param insulation The conductor's insulation type as defined by
	 {@link Insul}
	 @param length The length of the conductor in feet (from the source to the
	 load, one way length).
	 */
	public Conductor(Size size, Metal metal, Insul insulation, double length) {
		this.size = size;
		this.metal = metal;
		this.insulation = insulation;
		this.length = Math.abs(length);
	}

	/**
	 Returns a deep and convenient copy of this Conductor object. The new copy
	 is exactly the same as this conductor, except: (convenience)
	 <p>- it does not copy the conduit property, that is, the new clone is
	 assumed in free air (not in a conduit).
	 <p>-it does not copy the listener list of this Conductor.
	 */
	@Override
	public Conductor clone(){
		Conductor conductorClone = new Conductor();
		conductorClone.size = this.size;
		conductorClone.metal = this.metal;
		conductorClone.insulation =this.insulation;
		conductorClone.length = this.length;
		conductorClone.ambientTemperatureF = this.ambientTemperatureF;
		conductorClone.copperCoated = this.copperCoated;
		conductorClone.role = this.role;
		return conductorClone;
	}

	/**
	 Copies the state of the given conductor into this conductor.
	 The conduit, bundle and notifier delegates are not copied.
	 @param conductor The passed Conductor to copy from.
	 */
	public void copyFrom(Conductor conductor){
		if(conductor == null)
			throw new IllegalArgumentException("conductor parameter cannot be null");
		size = conductor.size;
		metal = conductor.metal;
		insulation =conductor.insulation;
		length = conductor.length;
		ambientTemperatureF = conductor.ambientTemperatureF;
		copperCoated = conductor.copperCoated;
		role = conductor.role;
	}

	/**
	 Constructs a default conductor object:
	 <p>- Size: 12 AWG
	 <p>- Metal: Copper
	 <p>- Insulation: THW
	 <p>- Length: 100 feet.
	 <p>- Ambient temperature = 86°F.
	 <p>- Coating = no coating (meaningful for CU conductors only).
	 <p>- Conduit = null (conductor is in free air).
	 <p>- Bundle = null (conductor not grouped or bundled).
	 <p>- Role = HOT.
	 */
	public Conductor(){}

	@Override
	public Size getSize() {
		return size;
	}

	/**
	 Sets the size of this conductor.
	 @param size The size of the conductor as defined by {@link Size}
	*/
	public Conductor setSize(Size size) {
		if(this.size == size)
			return this;
		this.size = size;
		notifier.notifyAllListeners();
		return this;
	}

	@Override
	public Metal getMetal() {
		return metal;
	}

	/**
	 Sets the metal to this conductor.
	 @param metal The conductor metal as defined by {@link Metal}
	 */
	public void setMetal(Metal metal) {
		if(this.metal == metal)
			return;
		this.metal = metal;
		notifier.notifyAllListeners();
	}

	@Override
	public Insul getInsulation() {
		return insulation;
	}

	@Override
	public void setInsulation(Insul insulation) {
		if(this.insulation == insulation)
			return;
		this.insulation = insulation;
		notifier.notifyAllListeners();
	}

	@Override
	public double getLength() {
		return length;
	}

	@Override
	public void setLength(double length) {
		length = Math.abs(length);
		if(this.length == length)
			return;
		this.length = length;
		notifier.notifyAllListeners();
	}

	@Override
	public double getInsulatedAreaIn2() {
		return ConductorProperties.getInsulatedAreaIn2(size, insulation);
	}

	@Override
	public double getCorrectedAndAdjustedAmpacity(){
		return ConductorProperties.getStandardAmpacity(size, metal,
				ConductorProperties.getTempRating(insulation)) * getCompoundFactor();
	}

	@Override
	public double getCorrectionFactor(){
		int adjustedTemp = 0;
		if(hasConduit())
			adjustedTemp = Factors.getRoofTopTempAdjustment(conduit.getRoofTopDistance());
		if(insulation == Insul.XHHW2)
			adjustedTemp = 0;
		return Factors.getTemperatureCorrectionF(getAmbientTemperatureF() + adjustedTemp,
				getTemperatureRating());
	}

	@Override
	public double getAdjustmentFactor() {
		if(hasConduit())
			return Factors.getAdjustmentFactor(conduit.getCurrentCarryingCount(), conduit.isNipple());
		if(hasBundle()){
			return Factors.getAdjustmentFactor(bundle.getCurrentCarryingCount(), bundle.getBundlingLength());
		}
		return 1;
	}

	@Override
	public double getCompoundFactor() {
		return getCorrectionFactor() * getAdjustmentFactor();
	}

	@Override
	public double getCompoundFactor(TempRating tempRating) {
		if(tempRating == null)
			return 1;
		Insul temp_insul;
		if(tempRating == TempRating.T60)
			temp_insul = Insul.TW;
		else if(tempRating == TempRating.T75)
			temp_insul = Insul.THW;
		else
			temp_insul = Insul.THHW;

		Insul old_insul = insulation;
		boolean _enabled = notifier.isEnable();
		notifier.enable(false);
		insulation = temp_insul;

		double compoundFactor = getCorrectionFactor() * getAdjustmentFactor();

		insulation = old_insul;
		notifier.enable(_enabled);

		return compoundFactor;
	}

	@Override
	public int getAmbientTemperatureF() {
		return ambientTemperatureF;
	}

	@Override
	public void setAmbientTemperatureF(int ambientTemperatureF) {
		if(conduit != null)
			conduit.getConduitables().forEach(conduitable -> {
				conduitable.notifierEnabled(false);
				conduitable.setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
				conduitable.notifierEnabled(true);
			});
		else if(bundle != null)
			bundle.getConduitables().forEach(conduitable -> {
				conduitable.notifierEnabled(false);
				conduitable.setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
				conduitable.notifierEnabled(true);
			});
		else
			setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
	}

	@Override
	public void setAmbientTemperatureWithoutPropagation(int ambientTemperatureF) {
		if(this.ambientTemperatureF == ambientTemperatureF)
			return;
		this.ambientTemperatureF = ambientTemperatureF;
		notifier.notifyAllListeners();
	}

	/**
	 Asks if this copper conductor is coated.
	 Notice that this property has no meaning when this conductor metal is
	 aluminum.

	 @return True if coated, false otherwise.
	 */
	public boolean isCopperCoated(){
		if(metal == Metal.ALUMINUM)
			return false;
		return copperCoated.isCoated();
	}

	/**
	 Sets the coating to this copper conductor.
	 Setting this property does not have any effect if this conductor metal is
	 aluminum.

	 @param copperCoated Indicates if the conductor is coated
	 @return this Conductor object
	 */
	public Conductor setCopperCoated(Coating copperCoated) {
		if(this.copperCoated == copperCoated)
			return this;
		this.copperCoated = copperCoated;
		notifier.notifyAllListeners();
		return this;
	}

	@Override
	public Coating getCopperCoating(){
		return copperCoated;
	}

	@Override
	public void setConduit(Conduit conduit) {
		if(conduit == null)
			return;
		if(conduit == this.conduit)
			return;
		leaveBundle();
		leaveConduit();
		conduit.add(this);
		this.conduit = conduit;
	}

	@Override
	public void leaveConduit(){
		if(conduit == null)
			return;
		conduit.remove(this);
		conduit = null;
	}

	@Override
	public Conduit getConduit() {
		return conduit;
	}

	@Override
	public boolean hasConduit() {
		return conduit != null;
	}

	@Override
	public TempRating getTemperatureRating() {
		return ConductorProperties.getTempRating(insulation);
	}

	/**
	 Returns the role of this conductor as defined by {@link Role}.

	 @return The role of this conductor.
	 */
	public Role getRole() {
		return role;
	}

	/**
	 Sets the role of this conductor as defined by {@link Role}.

	 @param role The role of this conductor. Notice the default role if HOT.
	 @return this Conductor object
	 */
	public Conductor setRole(Role role) {
		if(this.role == role)
			return this;
		this.role = role;
		notifier.notifyAllListeners();
		return this;
	}

	@Override
	public int getCurrentCarryingCount() {
		if(role == Role.GND | role == Role.NEUNCC | role == Role.NCONC)
			return 0;
		return 1; //this considers hot and neutral as current carrying conductor.
	}

	@Override
	public String getDescription() {
		//"#12 AWG THW (CU)(HOT)"
		return "#" + size.getName() + " " + insulation.getName()+ " (" + getMetal().getSymbol() + ")(" + role + ")";
	}

	@Override
	public void notifierEnabled(boolean flag) {
		notifier.enable(flag);
	}

	@Override
	public void setBundle(Bundle bundle) {
		if(bundle == null)
			return;
		if(bundle == this.bundle)
			return;
		leaveConduit();
		leaveBundle();
		bundle.add(this);
		this.bundle = bundle;
	}

	@Override
	public void leaveBundle() {
		if(bundle == null)
			return;
		bundle.remove(this);
		bundle = null;
	}

	@Override
	public Bundle getBundle() {
		return bundle;
	}

	@Override
	public boolean hasBundle() {
		return bundle != null;
	}

	@Override
	public NotifierDelegate getNotifier() {
		return notifier;
	}
}
