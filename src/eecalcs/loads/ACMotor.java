package eecalcs.loads;

import eecalcs.circuits.CircuitType;
import eecalcs.circuits.DSProperties;
import eecalcs.circuits.OCPD;
import eecalcs.loads.MotorProperties.DesignLetter;
import eecalcs.systems.VoltageAC;
import org.jetbrains.annotations.Nullable;

import static eecalcs.circuits.OCPD.Type.INVERSE_TIME_BREAKER;
import static eecalcs.loads.MotorProperties.DesignLetter.DESIGN_A;


/**
 <p>This class represents a motor load.
 */
public class ACMotor extends BaseLoad /*implements Load*/{
	private /*final*/ Horsepower horsepower;
	private CircuitType circuitType;
	private /*final*/ MotorProperties.Type motorType;
	private /*final*/ int motorVoltage;
	private OCPD.Type ocdpType = INVERSE_TIME_BREAKER;
	private DesignLetter designLetter = DESIGN_A;

	@Override
	public Load getACopy() {
		return null;
	}

	//delete later
/*	public ACMotor(){
		super(VoltageAC.v120_1ph_2w, 10);
	}*/


	/**
	 @return The standard voltage system that correspond to the voltage
	 rating of this motor. A motor can have a rating of 200 volts but since
	 that's not a standard voltage as defined in {@link VoltageAC} that
	 motor will be fed from a 208 volt system; that's the voltage allowed by
	 the code on each title of the respective motor FLC table.
	 @param motorType The type of motor. Could be any type except DC (in which
	 case the returned value is null).
	 @param motorVoltage The standard voltage rating of he motor as recognized by
	 any of the FLC tables in the NEC. The voltage must be a standard value
	 and also be compatible with the standard system voltages.
	 @param voltageAC The proposed voltage system that will feed this
	 motor.
	 */
	private VoltageAC prepareVoltageSystemAC(MotorProperties.Type motorType,
	                                         int motorVoltage, VoltageAC voltageAC){
		if(motorType == MotorProperties.Type.AC1P)
			return getVoltageSystemACForAC1P(motorVoltage, voltageAC);
		else if(motorType == MotorProperties.Type.AC2P || motorType == MotorProperties.Type.AC2P_WR)
			return getVoltageSystemACForAC2P(motorVoltage, voltageAC);
		else if(motorType == MotorProperties.Type.AC3P || motorType == MotorProperties.Type.AC3P_WR)
			return getVoltageSystemACForAC3P(motorVoltage, voltageAC);
		else //motorType == MotorProperties.Type.AC3PS
			return getVoltageSystemACForAC3PS(motorVoltage, voltageAC);
	}

	/**
	 Checks if a 3φ synchronous AC motor with the given voltage rating can be
	 fed from the given voltage system AC. If that is the case the given
	 voltage system AC is returned, otherwise null is returned.
	 */
	@Nullable
	private VoltageAC getVoltageSystemACForAC3PS(int motorVoltage,
	                                             VoltageAC voltageAC) {
		int voltageRating = voltageAC.getVoltage();
		if (voltageAC.getPhases() == 3) {
			if ((voltageRating == motorVoltage) ||
				(voltageRating >= 220 && voltageRating <= 240 && motorVoltage == 230) ||
				(voltageRating >= 440 && voltageRating <= 480 && motorVoltage == 460) ||
				(voltageRating >= 550 && voltageRating <= 1000 && motorVoltage == 575))
				return voltageAC;
		}
		return null;
	}

	/**
	 Checks if a 3φ induction-type squirrel cage motor with the given voltage
	 rating can be fed from the given voltage system AC. If that is the case
	 the given voltage system AC is returned, otherwise null is returned.
	 */
	@Nullable
	private VoltageAC getVoltageSystemACForAC3P(int motorVoltage,
	                                            VoltageAC voltageAC) {
		int voltageRating = voltageAC.getVoltage();
		if (voltageAC.getPhases() == 3){
			if ((voltageRating == motorVoltage) ||
				(voltageRating >= 110 && voltageRating <= 120 && motorVoltage == 115) ||
				(voltageRating >= 220 && voltageRating <= 240 && motorVoltage == 230) ||
				(voltageRating >= 440 && voltageRating <= 480 && motorVoltage == 460) ||
				(voltageRating >= 550 && voltageRating <= 1000 && motorVoltage == 575))
				return voltageAC;
		}
		return null;
	}

	/**
	 Checks if a 1φ induction-type squirrel cage motor with the given voltage
	 rating can be fed from the given voltage system AC. If that is the case
	 the given voltage system AC is returned, otherwise null is returned.
	 */
	@Nullable
	private VoltageAC getVoltageSystemACForAC2P(int motorVoltage,
	                                            VoltageAC voltageAC) {
		int voltageRating = voltageAC.getVoltage();
		if(voltageAC.getPhases() == 1){
			if ((voltageRating == motorVoltage) ||
				(voltageRating >= 110 && voltageRating <= 120 && motorVoltage == 115) ||
				(voltageRating >= 220 && voltageRating <= 240 && motorVoltage == 230) ||
				(voltageRating >= 440 && voltageRating <= 480 && motorVoltage == 460) ||
				(voltageRating >= 550 && voltageRating <= 1000 && motorVoltage == 575))
				return voltageAC;
		}
		return null;
	}

	/**
	 Checks if a 1φ AC motor with the given voltage rating can be fed from
	 the given voltage system AC. If that is the case the given voltage
	 system AC is returned, otherwise null is returned.
	 */
	@Nullable
	private VoltageAC getVoltageSystemACForAC1P(int motorVoltage,
	                                            VoltageAC voltageAC) {
		int voltageRating = voltageAC.getVoltage();
		if(voltageAC.getPhases() == 1){
			if ((voltageRating == motorVoltage) ||
				(voltageRating >= 110 && voltageRating <= 120 && motorVoltage == 115) ||
				(voltageRating >= 220 && voltageRating <= 240 && motorVoltage == 230))
				return voltageAC;
		}
		return null;
	}

	/**
	 Constructs an AC motor load for the specified voltage system. Notice
	 that voltage system must be compatible with the motor voltage rating.
	 Refer to {@link VoltageAC} for typical and custom values.
	 Notice also that DC motor type is not supported.
	 @param motorType The type of motor.
	 @param motorVoltageRating The voltage rating of the motor.
	 @param motorHorsepower The horsepower of the motor as defined in
	 @param voltageSystemAC The voltage system that will feed this motor.
	 {@link Horsepower}.
	 */
	public ACMotor(MotorProperties.Type motorType,
	               int motorVoltageRating, Horsepower motorHorsepower,
	               VoltageAC voltageSystemAC){
		checkNull(motorType, motorHorsepower, voltageSystemAC);
		checkDC(motorType);
		motorVoltage = MotorProperties.getNormalizedVoltage(motorVoltageRating);
		checkMotorVoltage();
		nominalCurrent = MotorProperties.getFlc(motorType, motorVoltage,
				motorHorsepower);
		this.voltageSource = prepareVoltageSystemAC(motorType, motorVoltage,
				voltageSystemAC);
		checkValidParamSet();
		this.motorType = motorType;
		this.horsepower = motorHorsepower;
		this.circuitType = CircuitType.DEDICATED_BRANCH;
		type = LoadType.CONTINUOUS;
		powerFactor = 0.8;
		MCA = 1.25 * nominalCurrent;
	}

	/**
	 Checks that the nominal current and voltage system for this motor (as
	 determined during object construction) are valid.
	 */
	private void checkValidParamSet() {
		if(nominalCurrent == 0 || voltageSource == null)
			throw new IllegalArgumentException("ACMotor: invalid parameter's set.");
	}

	/**
	 Checks that the motor voltage is valid (as determined durig object
	 construction).
	 */
	private void checkMotorVoltage() {
		if(motorVoltage == 0)
			throw new IllegalArgumentException("ACMotor: voltage " +
					"parameter is not valid.");
	}

	/**
	 Check that the motor type specified for the constructor is not a DC motor.
	 */
	private void checkDC(MotorProperties.Type motorType) {
		if(motorType == MotorProperties.Type.DC)
			throw new IllegalArgumentException("ACMotor: DC motor Type " +
					"parameter not supported.");
	}

	/**
	 Check that none of the parameters passed to the constructor is null.
	 */
	private void checkNull(MotorProperties.Type motorType,
	                       Horsepower motorHorsepower,
	                       VoltageAC voltageAC) {
		if(motorType == null || motorHorsepower == null || voltageAC == null)
			throw new IllegalArgumentException("ACMotor: parameters " +
					"cannot be null.");
	}

	/**
	 @return This motor horsepower.
	 */
	public Horsepower getHorsepower() {
		return horsepower;
	}

	/**
	 @return
	 */
	public MotorProperties.Type getMotorType() {
		return motorType;
	}

	/**
	 @return The type of {@link OCPD.Type OCPD} used by this motor.
	 */
	public OCPD.Type getOcdpType() {
		return ocdpType;
	}

	/**
	 Sets the type of {@link OCPD.Type OCPD} to be used by this motor.
	 @param ocdpType The OCPD type. If this is null, nothing is set.
	 */
	public void setOcdpType(OCPD.Type ocdpType) {
		if(ocdpType == null)
			return;
		this.ocdpType = ocdpType;
	}

	/**
	 @return This motor voltage rating.
	 */
	public int getMotorVoltage() {
		return motorVoltage;
	}

	/**
	 @return
	 */
	public DesignLetter getDesignLetter() {
		return designLetter;
	}

	/**
	 @param designLetter
	 */
	public void setDesignLetter(DesignLetter designLetter) {
		if(designLetter == this.designLetter || designLetter == null)
			return;
		this.designLetter = designLetter;
	}

	/**
	 @param circuitType
	 */
	public void setCircuitType(CircuitType circuitType) {
		this.circuitType = circuitType;
	}

	/**
	 @return The minimum circuit ampacity for the conductor feeding this motor.
	 */
	@Override
	public double getMCA() {
		return MCA;
	}

	@Override
	public double getNominalCurrent() {
		return nominalCurrent;
	}

	@Override
	public CircuitType getRequiredCircuitType() {
		return circuitType;
	}

	@Override
	public double getMaxOCPDRating() {
		return OCPD.getClosestMatch(nominalCurrent *
				MotorProperties.getMaxOCPDRatingFactorPerType(ocdpType,
				motorType, designLetter));
	}

	@Override
	public double getMinDSRating() {
		return DSProperties.getRating(1.15 * nominalCurrent);
	}

	@Override
	public boolean NHSRRuleApplies() {
		return true;
	}

	@Override
	public double getMaxOLPDRating() {
		//todo implement
		return 0;
	}


/*quedé aquí:
   implement getMaxOLPDRating()
   java doc all the new classes and methods
   remove excess comments
   commit
*  */

	@Override
	public boolean isNonLinear() {
		return false;
	}
}