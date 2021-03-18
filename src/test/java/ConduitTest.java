package test.java;

import eecalcs.circuits.Circuit;
import eecalcs.conductors.*;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Trade;
import eecalcs.conduits.Type;
import eecalcs.loads.GeneralLoad;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConduitTest {
    Conduit conduit = new Conduit(Type.EMT, false);
    Conductor conductor = new Conductor();
    Conductor conductor2;
    Cable cable = new Cable();
    Cable cable2;


    void change1(){
        conduit.add(conductor);
        cable.setConduit(conduit);
    }

    void change2(){
        conductor2 = new Conductor(Size.AWG_4, Metal.ALUMINUM, Insul.XHHW2, 125);
        cable2 = new Cable(VoltageSystemAC.v480_3ph_4w, 1.0);
        conduit.add(conductor.clone());
        conduit.add(conductor2);
        conduit.add(cable2);
    }

    @Test
    void getConduitables() {
        assertEquals(0, conduit.getConduitables().size());

        change1();
        assertEquals(2, conduit.getConduitables().size());

        change2();
        assertEquals(5, conduit.getConduitables().size());
    }

    @Test
    void remove() {
        change1();
        conduit.remove(conductor);
        assertEquals(1, conduit.getConduitables().size());

        change2();
        conduit.remove(conductor2);
        assertEquals(3, conduit.getConduitables().size());
    }

    @Test
    void empty() {
        assertEquals(0, conduit.getConduitables().size());

        change1();
        conduit.empty();
        assertEquals(0, conduit.getConduitables().size());

        change2();
        conduit.empty();
        assertEquals(0, conduit.getConduitables().size());
    }

    @Test
    void hasConduitable() {
        change1();
        assertTrue(conduit.hasConduitable(conductor));
        assertTrue(conduit.hasConduitable(cable));

        conduit.empty();
        assertFalse(conduit.hasConduitable(conductor));
        assertFalse(conduit.hasConduitable(cable));

        change2();
        assertTrue(conduit.hasConduitable(conductor2));
        assertTrue(conduit.hasConduitable(cable2));

        conduit.empty();
        assertFalse(conduit.hasConduitable(conductor2));
        assertFalse(conduit.hasConduitable(cable2));
        assertEquals(0, conduit.getConduitables().size());
    }

    @Test
    void getFillingConductorCount() {
        assertEquals(0, conduit.getFillingConductorCount());

        change1();
        assertEquals(2, conduit.getFillingConductorCount());

        change2();
        assertEquals(5, conduit.getFillingConductorCount());
    }

    @Test
    void getCurrentCarryingNumber() {
        assertEquals(0, conduit.getCurrentCarryingCount());

        change1();
        assertEquals(3, conduit.getCurrentCarryingCount());

        change2();
        assertEquals(8, conduit.getCurrentCarryingCount());

        cable2.setNeutralCarryingConductor(true);
        assertEquals(9, conduit.getCurrentCarryingCount());

        conductor.setRole(Conductor.Role.GND);
        conductor2.setRole(Conductor.Role.GND);
        assertEquals(7, conduit.getCurrentCarryingCount());
    }

    @Test
    void getConduitablesArea() {
        assertEquals(0, conduit.getConduitablesArea());

        change1();
        assertEquals(0.21444954084936207, conduit.getConduitablesArea(), 0.0001);

        change2();
        assertEquals(1.0993477042468105, conduit.getConduitablesArea(), 0.0001);
    }

    @Test
    void getTradeSize() {
        assertEquals(Trade.T1$2, conduit.getTradeSize());

        change1();
        assertEquals(Trade.T1, conduit.getTradeSize());

        change2();
        assertEquals(Trade.T2, conduit.getTradeSize());

        conduit.add(conductor.clone());
        conduit.add(conductor2.clone());
        conduit.add(cable2.clone());
        conduit.setType(Type.PVC40);
        assertEquals(1.9842458676442587, conduit.getConduitablesArea(), 0.0001);
        assertEquals(Trade.T3, conduit.getTradeSize());

        conduit.setType(Type.PVC80);
        assertEquals(Trade.T3, conduit.getTradeSize());

        conduit.setType(Type.EMT);
        assertEquals(Trade.T2_1$2, conduit.getTradeSize());

        cable.setOuterDiameter(1.25);
        assertEquals(3.015080957, conduit.getConduitablesArea(), 0.0001);

        conduit.setType(Type.RMC);
        assertEquals(Trade.T3_1$2, conduit.getTradeSize());

        conduit.setMinimumTrade(Trade.T4);
        assertEquals(Trade.T4, conduit.getTradeSize());

        conduit.setMinimumTrade(Trade.T1$2);
        assertEquals(Trade.T3_1$2, conduit.getTradeSize());

        conduit.setType(Type.ENT);
        assertNull(conduit.getTradeSize());
        assertTrue(conduit.getResultMessages().containsMessage(-100));

        conduit.setType(null);
        assertNull(conduit.getTradeSize());
        assertTrue(conduit.getResultMessages().containsMessage(-120));

        conduit.setMinimumTrade(null);
        assertNull(conduit.getTradeSize());
        assertTrue(conduit.getResultMessages().containsMessage(-110));
    }

    @Test
    void getAllowedFillPercentage() {
        assertFalse(conduit.isNipple());

        conduit.setNipple(true);
        assertTrue(conduit.isNipple());
        assertEquals(60, conduit.getMaxAllowedFillPercentage());

        conduit.setNipple(false);
        assertEquals(53, conduit.getMaxAllowedFillPercentage());

        change1();
        assertEquals(31, conduit.getMaxAllowedFillPercentage());

        change2();
        assertEquals(40, conduit.getMaxAllowedFillPercentage());
    }

    @Test
    void getBiggestOneEGC(){
        Conductor ground1 = new Conductor().setSize(Size.AWG_12).setRole(Conductor.Role.GND);
        Conductor ground2 = new Conductor().setSize(Size.AWG_10).setRole(Conductor.Role.GND);
        Conductor ground3 = new Conductor().setSize(Size.AWG_8).setRole(Conductor.Role.GND);
        conduit.add(ground1);
        conduit.add(ground2);
        conduit.add(ground3);
        assertEquals(Size.AWG_8, conduit.getBiggestEGC().getSize());
        assertEquals(Trade.T1$2, conduit.getTradeSizeForOneEGC());


        conduit.empty();
        conduit.add(ground2);
        conduit.add(ground3);
        conduit.add(ground1);
        conduit.add(ground3.clone());
        assertEquals(Size.AWG_8, conduit.getBiggestEGC().getSize());
        assertEquals(Trade.T1$2, conduit.getTradeSizeForOneEGC());

        GeneralLoad generalLoad = new GeneralLoad();
        Circuit circuit = new Circuit(generalLoad);
        circuit.setConduitMode(conduit);
        assertEquals(Size.AWG_8, conduit.getBiggestEGC().getSize());

        generalLoad.setNominalCurrent(200);

        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());
        assertEquals(Size.AWG_6, circuit.getGroundingConductor().getSize());
        assertEquals(200, circuit.getOCPDRating());

        /*the conduit contains 7 conductors: 1x12+1x10+2x8+2x3/0+1x6, out f
        which 5 are EGC, where the biggest one is the #6.
        Total area of these conductors is 0.8258 and the required area is 0
        .8258/0.4 = 2.0645; For an EMT conduit, the trade size is 2" which
        is 3.356
        */
        assertEquals(0.8258, conduit.getConduitablesArea());
        assertEquals(Trade.T2, conduit.getTradeSize());
        assertEquals(Insul.THW,circuit.getPhaseConductor().getInsulation());
        assertEquals(40, conduit.getMaxAllowedFillPercentage());
        assertEquals(Type.EMT, conduit.getType());

        /*for this scenario, the conduit is assumed to contain only 3
        conductors: 2x3/0+1x6 (1 hot + 1neutral + EGC).
        Total area of these conductors is 0.696 and the required area is 0
        .696/0.4 = 1.74; For an EMT conduit, the trade size is 1-1/2" which
        is 2.036
        */
        assertEquals(Size.AWG_6, conduit.getBiggestEGC().getSize());
        assertEquals(Trade.T1_1$2, conduit.getTradeSizeForOneEGC());

    }
}