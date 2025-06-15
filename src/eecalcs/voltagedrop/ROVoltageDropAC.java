package eecalcs.voltagedrop;

import eecalcs.conductors.ConductiveMetal;
import eecalcs.conductors.Size;
import eecalcs.conduits.OuterMaterial;
import eecalcs.loads.PowerFactorType;
import eecalcs.systems.VoltageAC;
import org.jetbrains.annotations.Nullable;

public interface ROVoltageDropAC {

	VoltageAC getVoltageAC();

	double getLoadCurrent();

	double getPowerFactor();

	PowerFactorType getPowerFactorType();

	Size getConductorSize();

	double getConductorLength();

	int getNumberOfSets();

	ConductiveMetal getConductorMetal();

	OuterMaterial getConduitMaterial();

	double getMaxVDropPercent();

	/**
	 * @return The line-to-line voltage drop for this object under the current conditions.
	 */
	double getVoltageDropPercent();

	/**
	 * @return The minimum conductor size having the given characteristics and used under the current conditions, whose
	 * voltage drop is equal or less than the current line-to-line voltage drop percentage.
	 */
	@Nullable Size getMinSizeForMaxVD();

	/**
	 * @return The maximum length of the current conductor size, conduit material and conditions, for the current
	 * voltage drop percentage.
	 */
	double getMaxLengthForVD();


}
