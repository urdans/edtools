package test.java;

import eecalcs.circuits.Circuit;
import eecalcs.conductors.*;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Trade;
import eecalcs.conduits.Type;
//import eecalcs.loads.GeneralLoad;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConduitTest {
    @Test
    void getConduitables() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        assertEquals(0, conduit.getConduitables().size());

        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageSystemAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(2, conduit.getConduitables().size());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageSystemAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.clone());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(5, conduit.getConduitables().size());
    }

    @Test
    void hasConduitable() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageSystemAC.v120_1ph_2w);
        conduit.add(cable);
        assertTrue(conduit.hasConduitable(conductor));
        assertTrue(conduit.hasConduitable(cable));
    }

    @Test
    void hasConduitable01() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageSystemAC.v120_1ph_2w);
        assertFalse(conduit.hasConduitable(conductor));
        assertFalse(conduit.hasConduitable(cable));

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageSystemAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.clone());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertTrue(conduit.hasConduitable(conductor2));
        assertTrue(conduit.hasConduitable(cable2));
    }

    @Test
    void getFillingConductorCount() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        assertEquals(0, conduit.getFillingConductorCount());

        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageSystemAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(2, conduit.getFillingConductorCount());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageSystemAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.clone());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(5, conduit.getFillingConductorCount());
    }

    @Test
    void getCurrentCarryingNumber() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        assertEquals(0, conduit.getCurrentCarryingCount());

        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageSystemAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(3, conduit.getCurrentCarryingCount());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageSystemAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.clone());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(8, conduit.getCurrentCarryingCount());

        cable2.setNeutralCarryingConductor();
        assertEquals(9, conduit.getCurrentCarryingCount());

        conductor.setRole(Conductor.Role.GND);
        conductor2.setRole(Conductor.Role.GND);
        assertEquals(7, conduit.getCurrentCarryingCount());
    }

    @Test
    void getConduitablesArea() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        assertEquals(0, conduit.getConduitablesArea());

        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageSystemAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(0.21444954084936207, conduit.getConduitablesArea(), 0.0001);

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageSystemAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.clone());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(1.0993477042468105, conduit.getConduitablesArea(), 0.0001);
    }

    @Test
    void getTradeSize() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        assertEquals(Trade.T1$2, conduit.getTradeSize());

        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageSystemAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(Trade.T1, conduit.getTradeSize());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageSystemAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.clone());
        conduit.add(conductor2);
        conduit.add(cable2);
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
        assertTrue(conduit.getResultMessages().containsMessage(-104));

        conduit.setType(null);
        assertNull(conduit.getTradeSize());
        assertTrue(conduit.getResultMessages().containsMessage(-102));

        conduit.setMinimumTrade(null);
        assertNull(conduit.getTradeSize());
        assertTrue(conduit.getResultMessages().containsMessage(-101));
    }

    @Test
    void getAllowedFillPercentage() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        assertFalse(conduit.isNipple());

        conduit.setNipple();
        assertTrue(conduit.isNipple());
        assertEquals(60, conduit.getMaxAllowedFillPercentage());

        conduit.setNonNipple();
        assertEquals(53, conduit.getMaxAllowedFillPercentage());

        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageSystemAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(31, conduit.getMaxAllowedFillPercentage());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageSystemAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.clone());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(40, conduit.getMaxAllowedFillPercentage());
    }

    /*@Test
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
        assertEquals(200, circuit.getOCPDRating());*/

        /*the conduit contains 7 conductors: 1x12+1x10+2x8+2x3/0+1x6, out f
        which 5 are EGC, where the biggest one is the #6.
        Total area of these conductors is 0.8258 and the required area is 0
        .8258/0.4 = 2.0645; For an EMT conduit, the trade size is 2" which
        is 3.356
        */
        /*assertEquals(0.8258, conduit.getConduitablesArea());
        assertEquals(Trade.T2, conduit.getTradeSize());
        assertEquals(Insul.THW,circuit.getPhaseConductor().getInsulation());
        assertEquals(40, conduit.getMaxAllowedFillPercentage());
        assertEquals(Type.EMT, conduit.getType());*/

        /*for this scenario, the conduit is assumed to contain only 3
        conductors: 2x3/0+1x6 (1 hot + 1neutral + EGC).
        Total area of these conductors is 0.696 and the required area is 0
        .696/0.4 = 1.74; For an EMT conduit, the trade size is 1-1/2" which
        is 2.036
        */
        /*assertEquals(Size.AWG_6, conduit.getBiggestEGC().getSize());
        assertEquals(Trade.T1_1$2, conduit.getTradeSizeForOneEGC());

    }*/
}