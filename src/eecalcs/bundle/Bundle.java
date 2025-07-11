package eecalcs.bundle;

import eecalcs.conductors.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 This class represents a bundle. A bundle is a group of cables or a group of insulated conductors, or a group made
 of a mix of both, that are installed in free air (not in a conduit) next to each other (paralleled, but
 not connected) without maintaining space, so they are staked, bundled, supported on bridled rings or simply tied
 together, along a defined distance.<p>

 When cables or conductors are bundled, the heat produced by the current (joule effect) does not dissipate as easy as
 when they are separated. For this reason, the ampacity of the conductors must be adjusted. The procedure to adjust the
 ampacity is described in <b>NEC-2014-2017 310.15(B)(3), NEC-2020 310.15(C)</b>.<p>

 A bundle of insulated conductors are not common. The NEC does not prohibit it and rules NEC-2014-2017 310.15(B)(3)
 (a) and NEC-2020 310.15(C) mention conductors as possible members of a bundle, therefore recognizing they can also
 form bundles. But, because of its rareness (insulated conductors not in raceway), it is subject to AHJ approval.
 Having insulated conductors in free air would be considered a bad practice but since it is not forbidden by the
 code, it is considered in this software. Note that some jurisdictions might prohibit insulated conductors in free
 air, bundled or not.<p>

 The default parameters are: <p>
 - Ambient temperature: 86 °F.<br>
 - bundling length: 24 inches.<p>

 These parameters can be changed through setters.
 */
public class Bundle implements ROBundle {
	public static double BUNDLE_CRITICAL_LENGTH = 24;
	/** Distance in inches of the bundling (not the length of the cable/conductors). Originally zero, which does not
	 have any meaning (a 0-length bundle is not physically possible). Length of bundle is critical above 24 inches
	 */
	private double bundlingLength = BUNDLE_CRITICAL_LENGTH;
	private int ambientTemperatureF = 86;

	private final List<Conduitable> conduitables = new ArrayList<>();

	public Bundle(int ambientTemperatureF){
		setAmbientTemperatureF(ambientTemperatureF);
	}

	/**
	 * Sets the ambient temperature of this bundle. If the given ambient temperature is out of the range
	 * [{@link Factors#MIN_TEMP_F}, {@link Factors#MAX_TEMP_F}] an IllegalArgumentException is thrown.
	 * @param ambientTemperatureF The ambient temperature in degrees Fahrenheit.
	 * @return This Bundle object.
	 */
	public Bundle setAmbientTemperatureF(int ambientTemperatureF) {
		if(ambientTemperatureF < Factors.MIN_TEMP_F || ambientTemperatureF > Factors.MAX_TEMP_F)
			throw new IllegalArgumentException("Ambient temperature must be " +
					"in the [" + Factors.MIN_TEMP_F + "," + Factors.MAX_TEMP_F + "] °F range.");
		this.ambientTemperatureF = ambientTemperatureF;
		updateAmbientTemperature();
		return this;
	}

	/** Sets the given ambient temperature (in degrees Fahrenheit) to all the conduitables in the bundle.*/
	private void updateAmbientTemperature() {
		for (var c: conduitables) {
			if (c instanceof RWConduitable) {
				((RWConduitable) c).setAmbientTemperatureF(ambientTemperatureF);
			}
		}
	}

	@Override
	public double getBundlingLength() {
		return bundlingLength;
	}

	@Override
	public int getCurrentCarryingCount() {
		int currentCarrying = 0;
		for (Conduitable conduitable : conduitables)
			currentCarrying += conduitable.getCurrentCarryingCount();
		return currentCarrying;
	}

	@Override
	public boolean isEmpty() {
		return conduitables.isEmpty();
	}

	@Override
	public int getConductorCount() {
		return conduitables.size();
	}

	/**
	 Adds a conduitable to this bundle by making a copy of it.
	 * @param conduitable The conduitable from which a copy will be added to this bundle. Cannot be null.
	 * @return This bundle.
	 */
	public Bundle add(@NotNull Conduitable conduitable){
		Conduitable c = conduitable.copy(this);
		if (c instanceof RWConduitable) {
			((RWConduitable) c).setAmbientTemperatureF(ambientTemperatureF);
		}
		conduitables.add(c);
		return this;
	}

	/**
	 Sets the length of the bundling (not the length of the cable/conductors).
	 @param bundlingLength The length in inches. Cannot be <0.
	 */
	public Bundle setBundlingLength(double bundlingLength){
		if(bundlingLength < 0)
			throw new IllegalArgumentException("The bundling length cannot be < 0.");
		this.bundlingLength = bundlingLength;
		return this;
	}

	/**
	 Asks if this bundle already contains the given conduitable.
	 @param conduitable The conduitable to check if it is already contained by
	 this bundle.
	 @return True if this bundle contains it, false otherwise.
	 @see Conduitable
	 */
	@Override
	public boolean hasConduitable(Conduitable conduitable) {
		return conduitables.contains(conduitable);
	}

	/**
	 @return A copy of the list of all conduitable objects that are part of this
	 bundle.
	 @see Conduitable
	 */
	@Deprecated
	public List<Conduitable> getConduitables() {
		return new ArrayList<>(conduitables);
	}

	@Override
	public int getAmbientTemperatureF() {
		return ambientTemperatureF;
	}
}
