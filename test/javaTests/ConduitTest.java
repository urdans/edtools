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
                .setMetal(ConductiveMetal.ALUMINUM)
                .setInsulation(Insulation.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.copy());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertEquals(5, conduit.getConduitables().size());
    }

/*    @Test
    void hasConduitable() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        Conductor conductor = new Conductor();
        conduit.add(conductor);
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        conduit.add(cable);
        assertTrue(conduit.hasConduitable(conductor));
        assertTrue(conduit.hasConduitable(cable));
    }*/

/*    @Test
    void hasConduitable01() {
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNonNipple();
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        assertFalse(conduit.hasConduitable(conductor));
        assertFalse(conduit.hasConduitable(cable));

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(ConductiveMetal.ALUMINUM)
                .setInsulation(Insulation.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.copy());
        conduit.add(conductor2);
        conduit.add(cable2);
        assertTrue(conduit.hasConduitable(conductor2));
        assertTrue(conduit.hasConduitable(cable2));
    }*/

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
                .setMetal(ConductiveMetal.ALUMINUM)
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
        //getting the copy
        conductor = (Conductor) conduit.getConduitables().get(0);
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        conduit.add(cable);
        assertEquals(3, conduit.getCurrentCarryingCount());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(ConductiveMetal.ALUMINUM)
                .setInsulation(Insulation.XHHW2)
                .setLength(125);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_4w)
                .setOuterDiameter(1.0);
        conduit.add(conductor.copy());
        conduit.add(conductor2);
        //getting the copy
        conductor2 = (Conductor) conduit.getConduitables().get(3);
        conduit.add(cable2);
        //getting the copy
        cable2 = (Cable) conduit.getConduitables().get(4);
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
                .setMetal(ConductiveMetal.ALUMINUM)
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
        cable = (Cable) conduit.getConduitables().get(1);
        assertEquals(TradeSize.T1, conduit.getTradeSize());

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(ConductiveMetal.ALUMINUM)
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
                .setMetal(ConductiveMetal.ALUMINUM)
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
        conductor1.setSize(Size.AWG_1).setMetal(ConductiveMetal.COPPER).setInsulation(Insulation.TW);

        Conductor conductor2 = new Conductor();
        conductor2.setSize(Size.AWG_2).setMetal(ConductiveMetal.ALUMINUM).setInsulation(Insulation.THW);

        Conduit conduit = new Conduit(100);
        conduit.add(conductor1);
        conduit.add(conductor2);
        assertEquals(2, conduit.getCurrentCarryingCount());

/*        Conduit conduit2 = new Conduit(110);
        assertThrows(IllegalArgumentException.class, () -> conduit2.add(conductor2));*/
    }

    @Test
    void updatingAmbientTemperature(){
        Conductor conductor1 = new Conductor();
        conductor1.setSize(Size.AWG_1).setMetal(ConductiveMetal.COPPER).setInsulation(Insulation.TW);
        conductor1.setAmbientTemperatureF(81);

        Conductor conductor2 = new Conductor();
        conductor2.setSize(Size.AWG_2).setMetal(ConductiveMetal.ALUMINUM).setInsulation(Insulation.THW);
        conductor2.setAmbientTemperatureF(82);

        Conductor conductor3 = new Conductor();
        conductor3.setSize(Size.AWG_3).setMetal(ConductiveMetal.COPPERCOATED).setInsulation(Insulation.THHW);
        conductor3.setAmbientTemperatureF(83);

        Conduit conduit = new Conduit(100);

        //that the ambient temperature of a conductor changes to the one of the conduit when added to the conduit
        assertEquals(81, conductor1.getAmbientTemperatureF());
        conduit.add(conductor1);
        Conductor cond1 = (Conductor) conduit.getConduitables().get(0);
        assertEquals(100, cond1.getAmbientTemperatureF());

        /*that the ambient temperature of the conductor changes to the one of the conduit when added to the conduit,
        but also that this addition does not affect the ambient temperature of the other conductors already in the
        conduit*/
        assertEquals(82, conductor2.getAmbientTemperatureF());
        conduit.add(conductor2);
        Conductor cond2 = (Conductor) conduit.getConduitables().get(1);
        assertEquals(100, cond2.getAmbientTemperatureF());
        assertEquals(100, cond1.getAmbientTemperatureF());

        /*that the ambient temperature of the conductor changes to the one of the conduit when added to the conduit,
        but also that this addition does not affect the ambient temperature of the other conductors already in the
        conduit*/
        assertEquals(83, conductor3.getAmbientTemperatureF());
        conduit.add(conductor3);
        Conductor cond3 = (Conductor) conduit.getConduitables().get(2);
        assertEquals(100, cond3.getAmbientTemperatureF());
        assertEquals(100, cond2.getAmbientTemperatureF());
        assertEquals(100, cond1.getAmbientTemperatureF());

        /*That despite this attempt, the temperature of the conductors remains the same as of the conduit*/
        assertEquals(conduit.getAmbientTemperatureF(), cond1.getAmbientTemperatureF());
        assertEquals(conduit.getAmbientTemperatureF(), cond2.getAmbientTemperatureF());
        assertEquals(conduit.getAmbientTemperatureF(), cond3.getAmbientTemperatureF());

        /*That the ambient temperature of the conduit can be set and that the same is propagated to the contained
        conductor*/
        conduit.setAmbientTemperatureF(99);
        assertEquals(conduit.getAmbientTemperatureF(), cond1.getAmbientTemperatureF());
        assertEquals(conduit.getAmbientTemperatureF(), cond2.getAmbientTemperatureF());
        assertEquals(conduit.getAmbientTemperatureF(), cond3.getAmbientTemperatureF());

        /*That the conductors are still inside the conduit*/
/*        assertTrue(conduit.hasConduitable(cond1));
        assertTrue(conduit.hasConduitable(cond2));
        assertTrue(conduit.hasConduitable(cond3));*/
        assertTrue(cond1.hasConduit());
        assertTrue(cond2.hasConduit());
        assertTrue(cond3.hasConduit());
        assertFalse(cond1.hasBundle());
        assertFalse(cond2.hasBundle());
        assertFalse(cond3.hasBundle());
    }
}