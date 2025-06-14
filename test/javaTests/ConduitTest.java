package javaTests;

import eecalcs.conductors.*;
import eecalcs.conductors.Cable;
import eecalcs.conductors.Conductor;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.TradeSize;
import eecalcs.conduits.Type;
//import eecalcs.loads.GeneralLoad;
import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConduitTest {
    @Test
    void getConduitables() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        assertEquals(0, conduit.getConduitables().size());

        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(2, conduit.getConduitables().size());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(ConductiveMaterial.ALUMINUM)
                .setInsulation(Insulation.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.copy());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(5, conduit.getConduitables().size());
    }

    @Test
    void hasConduitable() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        conduit.add(cable);
        assertTrue(conduit.hasConduitable(conductor));
        assertTrue(conduit.hasConduitable(cable));
    }

    @Test
    void hasConduitable01() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        assertFalse(conduit.hasConduitable(conductor));
        assertFalse(conduit.hasConduitable(cable));

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(ConductiveMaterial.ALUMINUM)
                .setInsulation(Insulation.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.copy());
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
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(2, conduit.getFillingConductorCount());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(ConductiveMaterial.ALUMINUM)
                .setInsulation(Insulation.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.copy());
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
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(3, conduit.getCurrentCarryingCount());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(ConductiveMaterial.ALUMINUM)
                .setInsulation(Insulation.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.copy());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(8, conduit.getCurrentCarryingCount());

        cable2.setNeutralAsCurrentCarrying();
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
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(0.21444954084936207, conduit.getConduitablesArea(), 0.0001);

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(ConductiveMaterial.ALUMINUM)
                .setInsulation(Insulation.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.copy());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(1.0993477042468105, conduit.getConduitablesArea(), 0.0001);
    }

    @Test
    void getTradeSize() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        assertEquals(TradeSize.T1$2, conduit.getTradeSize());

        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(TradeSize.T1, conduit.getTradeSize());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(ConductiveMaterial.ALUMINUM)
                .setInsulation(Insulation.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.copy());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(TradeSize.T2, conduit.getTradeSize());

        conduit.add(conductor.copy());
        conduit.add(conductor2.copy());
        conduit.add(cable2.copy());
        conduit.setType(Type.PVC40);
        assertEquals(1.9842458676442587, conduit.getConduitablesArea(), 0.0001);
        assertEquals(TradeSize.T3, conduit.getTradeSize());

        conduit.setType(Type.PVC80);
        assertEquals(TradeSize.T3, conduit.getTradeSize());

        conduit.setType(Type.EMT);
        assertEquals(TradeSize.T2_1$2, conduit.getTradeSize());

        cable.setOuterDiameter(1.25);
        assertEquals(3.015080957, conduit.getConduitablesArea(), 0.0001);

        conduit.setType(Type.RMC);
        assertEquals(TradeSize.T3_1$2, conduit.getTradeSize());

        conduit.setMinimumTradeSize(TradeSize.T4);
        assertEquals(TradeSize.T4, conduit.getTradeSize());

        conduit.setMinimumTradeSize(TradeSize.T1$2);
        assertEquals(TradeSize.T3_1$2, conduit.getTradeSize());

        conduit.setType(Type.ENT);
        assertNull(conduit.getTradeSize());

        assertThrows(IllegalArgumentException.class, () -> conduit.setType(null));

        assertThrows(IllegalArgumentException.class, () -> conduit.setMinimumTradeSize(null));
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
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(31, conduit.getMaxAllowedFillPercentage());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(ConductiveMaterial.ALUMINUM)
                .setInsulation(Insulation.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.copy());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(40, conduit.getMaxAllowedFillPercentage());
    }

    @Test
    void addingConductors(){
        Conductor conductor1 = new Conductor();
        conductor1.setSize(Size.AWG_1).setMetal(ConductiveMaterial.COPPER).setInsulation(Insulation.TW);

        Conductor conductor2 = new Conductor();
        conductor2.setSize(Size.AWG_2).setMetal(ConductiveMaterial.ALUMINUM).setInsulation(Insulation.THW);

        Conduit conduit = new Conduit(100);
        conduit.add(conductor1);
        conduit.add(conductor2);
        assertEquals(2, conduit.getCurrentCarryingCount());

        Conduit conduit2 = new Conduit(110);
        assertThrows(IllegalArgumentException.class, () -> conduit2.add(conductor2));
    }

    @Test
    void updatingAmbientTemperature(){
        Conductor conductor1 = new Conductor();
        conductor1.setSize(Size.AWG_1).setMetal(ConductiveMaterial.COPPER).setInsulation(Insulation.TW);
        conductor1.setAmbientTemperatureF(81);

        Conductor conductor2 = new Conductor();
        conductor2.setSize(Size.AWG_2).setMetal(ConductiveMaterial.ALUMINUM).setInsulation(Insulation.THW);
        conductor2.setAmbientTemperatureF(82);

        Conductor conductor3 = new Conductor();
        conductor3.setSize(Size.AWG_3).setMetal(ConductiveMaterial.COPPERCOATED).setInsulation(Insulation.THHW);
        conductor3.setAmbientTemperatureF(83);

        Conduit conduit = new Conduit(100);

        //that the ambient temperature of a conductor changes to the one of the conduit when added to the conduit
        assertEquals(81, conductor1.getAmbientTemperatureF());
        conduit.add(conductor1);
        assertEquals(100, conductor1.getAmbientTemperatureF());

        /*that the ambient temperature of the conductor changes to the one of the conduit when added to the conduit,
        but also that this addition does not affect the ambient temperature of the other conductors already in the
        conduit*/
        assertEquals(82, conductor2.getAmbientTemperatureF());
        conduit.add(conductor2);
        assertEquals(100, conductor2.getAmbientTemperatureF());
        assertEquals(100, conductor1.getAmbientTemperatureF());

        /*that the ambient temperature of the conductor changes to the one of the conduit when added to the conduit,
        but also that this addition does not affect the ambient temperature of the other conductors already in the
        conduit*/
        assertEquals(83, conductor3.getAmbientTemperatureF());
        conduit.add(conductor3);
        assertEquals(100, conductor3.getAmbientTemperatureF());
        assertEquals(100, conductor2.getAmbientTemperatureF());
        assertEquals(100, conductor1.getAmbientTemperatureF());

        /*That the ambient temperature of the conductor inside the conduit cannot be changed*/
        assertThrows(IllegalArgumentException.class, () -> conductor1.setAmbientTemperatureF(81));
        assertThrows(IllegalArgumentException.class, () -> conductor2.setAmbientTemperatureF(82));
        assertThrows(IllegalArgumentException.class, () -> conductor3.setAmbientTemperatureF(83));

        /*That despite this attempt, the temperature of the conductors remains the same as of the conduit*/
        assertEquals(conduit.getAmbientTemperatureF(), conductor1.getAmbientTemperatureF());
        assertEquals(conduit.getAmbientTemperatureF(), conductor2.getAmbientTemperatureF());
        assertEquals(conduit.getAmbientTemperatureF(), conductor3.getAmbientTemperatureF());

        /*That the ambient temperature of the conduit can be set and that the same is propagated to the contained
        conductor*/
        conduit.setAmbientTemperatureF(99);
        assertEquals(conduit.getAmbientTemperatureF(), conductor1.getAmbientTemperatureF());
        assertEquals(conduit.getAmbientTemperatureF(), conductor2.getAmbientTemperatureF());
        assertEquals(conduit.getAmbientTemperatureF(), conductor3.getAmbientTemperatureF());

        /*That the conductors are still inside the conduit*/
        assertTrue(conduit.hasConduitable(conductor1));
        assertTrue(conduit.hasConduitable(conductor2));
        assertTrue(conduit.hasConduitable(conductor3));
        assertTrue(conductor1.hasConduit());
        assertTrue(conductor2.hasConduit());
        assertTrue(conductor3.hasConduit());
        assertFalse(conductor1.hasBundle());
        assertFalse(conductor2.hasBundle());
        assertFalse(conductor3.hasBundle());
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
        assertEquals(TradeSize.T1$2, conduit.getTradeSizeForOneEGC());

        conduit.empty();
        conduit.add(ground2);
        conduit.add(ground3);
        conduit.add(ground1);
        conduit.add(ground3.copy());
        assertEquals(Size.AWG_8, conduit.getBiggestEGC().getSize());
        assertEquals(TradeSize.T1$2, conduit.getTradeSizeForOneEGC());

        GeneralLoad generalLoad = new GeneralLoad();
        CircuitAll circuit = new CircuitAll(generalLoad);
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
        assertEquals(TradeSize.T2, conduit.getTradeSize());
        assertEquals(Insulation.THW,circuit.getPhaseConductor().getInsulation());
        assertEquals(40, conduit.getMaxAllowedFillPercentage());
        assertEquals(Type.EMT, conduit.getType());*/

        /*for this scenario, the conduit is assumed to contain only 3
        conductors: 2x3/0+1x6 (1 hot + 1neutral + EGC).
        Total area of these conductors is 0.696 and the required area is 0
        .696/0.4 = 1.74; For an EMT conduit, the trade size is 1-1/2" which
        is 2.036
        */
        /*assertEquals(Size.AWG_6, conduit.getBiggestEGC().getSize());
        assertEquals(TradeSize.T1_1$2, conduit.getTradeSizeForOneEGC());

    }*/
}