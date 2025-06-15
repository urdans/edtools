package javaTests;

import eecalcs.circuits.CircuitStandard;
import eecalcs.circuits.CircuitType;
import eecalcs.conductors.ConductiveMetal;
import eecalcs.conductors.Insulation;
import eecalcs.conductors.Size;
import eecalcs.conductors.TempRating;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.TradeSize;
import eecalcs.conduits.Type;
import eecalcs.loads.GenericLoad;
import eecalcs.loads.Load;
import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class CircuitStandardTest {

    @Test
    void correct_conduit_setup_tes01() {
        /*That the setup of the conduit is correct hots=1, neutral = 1, gnd= 1*/
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v120_1ph_2w, 1.0, 10.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(86)
                .setConduitType(Type.EMT)
                .setEGCMetal(ConductiveMetal.COPPER)
                .setFullPercentRated(false)
                .setInsulation(Insulation.THW)
                .setLength(90)
                .setMaxVDropPercent(3.0)
                .setMinimumTradeSize(TradeSize.T1$2)
                .setMetal(ConductiveMetal.COPPER)
                .setRooftopDistance(-1)
                .setTerminationTempRating(TempRating.UNKNOWN);

        assertEquals(86, circuit.getConduit().getAmbientTemperatureF());
        assertEquals(Type.EMT, circuit.getConduit().getType());
        assertEquals(3, ((Conduit)circuit.getConduit()).getConduitables().size());
        assertEquals(2, circuit.getConduit().getCurrentCarryingCount());
        assertEquals(3, circuit.getConduit().getFillingConductorCount());
    }

    @Test
    void correct_conduit_setup_tes02() {
        /*That the setup of the conduit is correct hots=2, neutral = 1 -CCC, gnd= 1*/
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v208_1ph_3w, 1.0, 10.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(90)
                .setConduitType(Type.ENT)
                .setEGCMetal(ConductiveMetal.ALUMINUM)
                .setFullPercentRated(true)
                .setInsulation(Insulation.TW)
                .setLength(60)
                .setMaxVDropPercent(3.5)
                .setMinimumTradeSize(TradeSize.T1)
                .setMetal(ConductiveMetal.ALUMINUM)
                .setRooftopDistance(1)
                .setTerminationTempRating(TempRating.T75);

        assertEquals(90, circuit.getConduit().getAmbientTemperatureF());
        assertEquals(Type.ENT, circuit.getConduit().getType());
        assertEquals(4, ((Conduit)circuit.getConduit()).getConduitables().size());
        assertEquals(3, circuit.getConduit().getCurrentCarryingCount());
        assertEquals(4, circuit.getConduit().getFillingConductorCount());
    }

    @Test
    void correct_conduit_setup_tes03() {
        /*That the setup of the conduit is correct hots=2, neutral = 1 -CCC, gnd= 1*/
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v240_1ph_3w, 1.0, 10.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(75)
                .setConduitType(Type.PVCA)
                .setEGCMetal(ConductiveMetal.COPPERCOATED)
                .setFullPercentRated(false)
                .setInsulation(Insulation.THHN)
                .setLength(80)
                .setMaxVDropPercent(5.0)
                .setMinimumTradeSize(TradeSize.T1_1$2)
                .setMetal(ConductiveMetal.COPPERCOATED)
                .setRooftopDistance(2)
                .setTerminationTempRating(TempRating.T60);

        assertEquals(75, circuit.getConduit().getAmbientTemperatureF());
        assertEquals(Type.PVCA, circuit.getConduit().getType());
        assertEquals(4, ((Conduit)circuit.getConduit()).getConduitables().size());
        assertEquals(2, circuit.getConduit().getCurrentCarryingCount());
        assertEquals(4, circuit.getConduit().getFillingConductorCount());
    }

    @Test
    void correct_conduit_setup_tes04() {
        /*That the setup of the conduit is correct hots=3, neutral = 0, gnd= 1*/
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v208_3ph_3w, 1.0, 10.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(65)
                .setConduitType(Type.LFMC)
                .setEGCMetal(ConductiveMetal.COPPER)
                .setFullPercentRated(true)
                .setInsulation(Insulation.RHH)
                .setLength(9)
                .setMaxVDropPercent(2.0)
                .setMinimumTradeSize(TradeSize.T3$4)
                .setMetal(ConductiveMetal.COPPER)
                .setRooftopDistance(-1)
                .setTerminationTempRating(TempRating.UNKNOWN);

        assertEquals(65, circuit.getConduit().getAmbientTemperatureF());
        assertEquals(Type.LFMC, circuit.getConduit().getType());
        assertEquals(4, ((Conduit)circuit.getConduit()).getConduitables().size());
        assertEquals(3, circuit.getConduit().getCurrentCarryingCount());
        assertEquals(4, circuit.getConduit().getFillingConductorCount());
    }

    @Test
    void correct_conduit_setup_tes05() {
        /*That the setup of the conduit is correct hots=3, neutral = 1 -NCCC, gnd= 1*/
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v480_3ph_4w, 1.0, 10.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(62)
                .setConduitType(Type.HDPE)
                .setEGCMetal(ConductiveMetal.ALUMINUM)
                .setFullPercentRated(false)
                .setInsulation(Insulation.TBS)
                .setLength(19)
                .setMaxVDropPercent(2.5)
                .setMinimumTradeSize(TradeSize.T1$2)
                .setMetal(ConductiveMetal.COPPER)
                .setRooftopDistance(-1)
                .setTerminationTempRating(TempRating.T60)
                .setCircuitType(CircuitType.DEDICATED_BRANCH);

        assertEquals(62, circuit.getConduit().getAmbientTemperatureF());
        assertEquals(Type.HDPE, circuit.getConduit().getType());
        assertEquals(5, ((Conduit)circuit.getConduit()).getConduitables().size());
        assertEquals(3, circuit.getConduit().getCurrentCarryingCount());
        assertEquals(5, circuit.getConduit().getFillingConductorCount());
    }


    @Test
    void correct_side_per_voltage_and_ampacity_tes01() {// 10Amps
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v120_1ph_2w, 1.0, 10.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(86)
                .setConduitType(Type.EMT)
                .setEGCMetal(ConductiveMetal.COPPER)
                .setFullPercentRated(false)
                .setInsulation(Insulation.THW)
                .setLength(90)
                .setMaxVDropPercent(3.0)
                .setMinimumTradeSize(TradeSize.T1$2)
                .setMetal(ConductiveMetal.COPPER)
                .setRooftopDistance(-1)
                .setTerminationTempRating(TempRating.UNKNOWN)
                .setCircuitType(CircuitType.DEDICATED_BRANCH);

        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 10 Amps, copper, 75°C, TR=60°C*/
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 10 Amps, copper, 75°C, TR=75°C*/
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 10 Amps, copper, 75°C, TR=90°C*/
        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());
        //----------------------------------------------------------------------------

        /*Correct conductor size for 10 Amps, copper, 60°C, TR=unknown*/
        circuit.setInsulation(Insulation.TW);
        circuit.setTerminationTempRating(TempRating.UNKNOWN);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 10 Amps, copper, 60°C, TR=60°C*/
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 10 Amps, copper, 60°C, TR=75°C*/
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 10 Amps, copper, 60°C, TR=90°C*/
        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());
        //----------------------------------------------------------------------------

        /*Correct conductor size for 10 Amps, copper, 90°C, TR=unknown*/
        circuit.setInsulation(Insulation.XHHW2);
        circuit.setTerminationTempRating(TempRating.UNKNOWN);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 10 Amps, copper, 90°C, TR=60°C*/
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 10 Amps, copper, 90°C, TR=75°C*/
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 10 Amps, copper, 90°C, TR=90°C*/
        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.AWG_12, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_12, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_14, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_14, circuit.getNeutralConductorSizePerAmpacity());
    }

    @Test
    void correct_side_per_voltage_and_ampacity_tes02() { //100 Amps
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v120_1ph_2w, 1.0, 100.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(86)
                .setConduitType(Type.EMT)
                .setEGCMetal(ConductiveMetal.COPPER)
                .setFullPercentRated(false)
                .setInsulation(Insulation.THW)
                .setLength(90)
                .setMaxVDropPercent(3.0)
                .setMinimumTradeSize(TradeSize.T1$2)
                .setMetal(ConductiveMetal.COPPER)
                .setRooftopDistance(-1)
                .setTerminationTempRating(TempRating.UNKNOWN)
                .setCircuitType(CircuitType.DEDICATED_BRANCH);

        /*Correct conductor size per voltage drop for 100 Amps, copper*/
        assertEquals(Size.AWG_2, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_2, circuit.getNeutralConductorSizePerVoltageDrop());

        /*Correct conductor size for 100 Amps, copper, 75°C, TR=unknown*/
        assertEquals(Size.AWG_1, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_1, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 100 Amps, copper, 75°C, TR=60°C*/
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.AWG_1, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_1, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 100 Amps, copper, 75°C, TR=75°C*/
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_3, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_3, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 100 Amps, copper, 75°C, TR=90°C*/
        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.AWG_3, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_3, circuit.getNeutralConductorSizePerAmpacity());
        //----------------------------------------------------------------------------

        /*Correct conductor size for 100 Amps, copper, 60°C, TR=unknown*/
        circuit.setInsulation(Insulation.TW);
        circuit.setTerminationTempRating(TempRating.UNKNOWN);
        assertEquals(Size.AWG_1, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_1, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 100 Amps, copper, 60°C, TR=60°C*/
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.AWG_1, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_1, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 100 Amps, copper, 60°C, TR=75°C*/
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_1, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_1, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 100 Amps, copper, 60°C, TR=90°C*/
        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.AWG_1, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_1, circuit.getNeutralConductorSizePerAmpacity());
        //----------------------------------------------------------------------------

        /*Correct conductor size for 100 Amps, copper, 90°C, TR=unknown*/
        circuit.setInsulation(Insulation.XHHW2);
        circuit.setTerminationTempRating(TempRating.UNKNOWN);
        assertEquals(Size.AWG_1, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_1, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 100 Amps, copper, 90°C, TR=60°C*/
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.AWG_1, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_1, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 100 Amps, copper, 90°C, TR=75°C*/
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_3, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_3, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 100 Amps, copper, 90°C, TR=90°C*/
        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.AWG_3, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_3, circuit.getNeutralConductorSizePerAmpacity());
    }

    @Test
    void correct_side_per_voltage_and_ampacity_tes03() { //200 Amps
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v120_1ph_2w, 1.0, 200.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(86)
                .setConduitType(Type.EMT)
                .setEGCMetal(ConductiveMetal.COPPER)
                .setFullPercentRated(false)
                .setInsulation(Insulation.THW)
                .setLength(90)
                .setMaxVDropPercent(3.0)
                .setMinimumTradeSize(TradeSize.T1$2)
                .setMetal(ConductiveMetal.COPPER)
                .setRooftopDistance(-1)
                .setTerminationTempRating(TempRating.UNKNOWN)
                .setCircuitType(CircuitType.DEDICATED_BRANCH);

        /*Correct conductor size per voltage drop for 200 Amps, copper*/
        assertEquals(Size.AWG_2$0, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_2$0, circuit.getNeutralConductorSizePerVoltageDrop());

        /*Correct conductor size for 200 Amps, copper, 75°C, TR=unknown*/
        assertEquals(Size.AWG_3$0, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_3$0, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 200 Amps, copper, 75°C, TR=60°C*/
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.KCMIL_250, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.KCMIL_250, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 200 Amps, copper, 75°C, TR=75°C*/
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_3$0, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_3$0, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 200 Amps, copper, 75°C, TR=90°C*/
        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.AWG_3$0, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_3$0, circuit.getNeutralConductorSizePerAmpacity());
        //----------------------------------------------------------------------------

        /*Correct conductor size for 200 Amps, copper, 60°C, TR=unknown*/
        circuit.setInsulation(Insulation.TW);
        circuit.setTerminationTempRating(TempRating.UNKNOWN);
        assertEquals(Size.KCMIL_250, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.KCMIL_250, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 200 Amps, copper, 60°C, TR=60°C*/
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.KCMIL_250, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.KCMIL_250, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 200 Amps, copper, 60°C, TR=75°C*/
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.KCMIL_250, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.KCMIL_250, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 200 Amps, copper, 60°C, TR=90°C*/
        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.KCMIL_250, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.KCMIL_250, circuit.getNeutralConductorSizePerAmpacity());
        //----------------------------------------------------------------------------

        /*Correct conductor size for 200 Amps, copper, 90°C, TR=unknown*/
        circuit.setInsulation(Insulation.XHHW2);
        circuit.setTerminationTempRating(TempRating.UNKNOWN);
        assertEquals(Size.AWG_3$0, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_3$0, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 200 Amps, copper, 90°C, TR=60°C*/
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.KCMIL_250, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.KCMIL_250, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 200 Amps, copper, 90°C, TR=75°C*/
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_3$0, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_3$0, circuit.getNeutralConductorSizePerAmpacity());

        /*Correct conductor size for 200 Amps, copper, 90°C, TR=90°C*/
        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.AWG_3$0, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_3$0, circuit.getNeutralConductorSizePerAmpacity());
    }

    @Test
    void correct_side_per_voltage_and_ampacity_tes04() {
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v208_3ph_4w, 0.8, 200.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(96)
                .setConduitType(Type.PVC80)
                .setEGCMetal(ConductiveMetal.COPPER)
                .setFullPercentRated(true)
                .setInsulation(Insulation.THWN2)
                .setLength(400)
                .setMaxVDropPercent(1.8)
                .setMinimumTradeSize(TradeSize.T1)
                .setMetal(ConductiveMetal.COPPER)
                .setRooftopDistance(-1)
                .setTerminationTempRating(TempRating.T75)
                .setCircuitType(CircuitType.FEEDER);

        /*Correct conductor size per voltage drop*/
        assertEquals(Size.KCMIL_1750, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.KCMIL_1750, circuit.getNeutralConductorSizePerVoltageDrop());

        /*Correct conductor size per ampacity*/
        assertEquals(Size.AWG_4$0, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_4$0, circuit.getNeutralConductorSizePerAmpacity());

        circuit.setMaxVDropPercent(2.0);
        assertEquals(Size.KCMIL_1500, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_4$0, circuit.getPhaseConductorSizePerAmpacity());

        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.KCMIL_300, circuit.getPhaseConductorSizePerAmpacity());

        circuit.setTerminationTempRating(TempRating.UNKNOWN);
        assertEquals(Size.AWG_4$0, circuit.getPhaseConductorSizePerAmpacity());

        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.AWG_3$0, circuit.getPhaseConductorSizePerAmpacity());

        circuit.setMetal(ConductiveMetal.ALUMINUM).setMaxVDropPercent(2.0).setTerminationTempRating(TempRating.T75);
        assertEquals(Size.KCMIL_2000, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.KCMIL_300, circuit.getPhaseConductorSizePerAmpacity());

        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.KCMIL_400, circuit.getPhaseConductorSizePerAmpacity());

        circuit.setTerminationTempRating(TempRating.UNKNOWN);
        assertEquals(Size.KCMIL_300, circuit.getPhaseConductorSizePerAmpacity());

        circuit.setTerminationTempRating(TempRating.T90);
        assertEquals(Size.KCMIL_250, circuit.getPhaseConductorSizePerAmpacity());
    }

    @Test
    void correct_side_per_voltage_and_ampacity_tes05() {
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v208_3ph_3w, 0.8, 1000.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(96)
                .setConduitType(Type.PVC80)
                .setEGCMetal(ConductiveMetal.COPPER)
                .setFullPercentRated(true)
                .setInsulation(Insulation.THWN2)
                .setLength(400)
                .setMaxVDropPercent(1.8)
                .setMinimumTradeSize(TradeSize.T1)
                .setMetal(ConductiveMetal.COPPER)
                .setRooftopDistance(-1)
                .setTerminationTempRating(TempRating.T75)
                .setCircuitType(CircuitType.FEEDER);

        /*Correct conductor size per voltage drop*/
        assertNull(circuit.getPhaseConductorSizePerVoltageDrop());
        assertThrows(IllegalStateException.class, circuit::getNeutralConductorSizePerVoltageDrop);

        /*Correct conductor size per ampacity*/
        assertNull(circuit.getPhaseConductorSizePerAmpacity());
        assertThrows(IllegalStateException.class, circuit::getNeutralConductorSizePerAmpacity);
    }

    @Test
    void testing_all_functionalities_tes01() {
        Load load = GenericLoad.fromNominalCurrent(VoltageAC.v240_3ph_4w, 0.8, 100.0);
        CircuitStandard circuit = new CircuitStandard(load);

        //setup
        circuit
                .setAmbientTemperature(107)
                .setConduitType(Type.EMT)
                .setEGCMetal(ConductiveMetal.COPPER)
                .setFullPercentRated(false)
                .setInsulation(Insulation.XHHW2)
                .setLength(100)
                .setMaxVDropPercent(3)
                .setMinimumTradeSize(TradeSize.T1)
                .setMetal(ConductiveMetal.COPPER)
                .setRooftopDistance(-1)
                .setTerminationTempRating(TempRating.T75)
                .setCircuitType(CircuitType.DEDICATED_BRANCH);

        assertEquals(Size.AWG_4, circuit.getPhaseConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_2, circuit.getPhaseConductorSizePerAmpacity());
        assertEquals(Size.AWG_4, circuit.getNeutralConductorSizePerVoltageDrop());
        assertEquals(Size.AWG_2, circuit.getNeutralConductorSizePerAmpacity());

        assertEquals(Size.AWG_2, circuit.getPhaseConductorSize());
        assertEquals(Size.AWG_2, circuit.getNeutralConductorSize());

        assertEquals(1.4, circuit.getActualVoltageDrop());
        assertEquals(113.1, circuit.getConductorsAmpacity());

        assertEquals(125, circuit.getOCPDRating());
        //this is one of the two test
        assertEquals(Size.KCMIL_1750, circuit.getEGCConductorSize());

        assertEquals(5, circuit.getConduit().getFillingConductorCount());
        assertEquals(3, circuit.getConduit().getCurrentCarryingCount());
        //assertEquals(TradeSize.T1, circuit.getConduit().getTradeSize());

        //assertEquals(3, circuit.getConduit().getArea());
        //assertEquals(3, circuit.getConduit().getConduitablesArea());
        assertEquals(40, circuit.getConduit().getMaxAllowedFillPercentage());
        //assertEquals(3, circuit.getConduit().getFillPercentage());

    }

}