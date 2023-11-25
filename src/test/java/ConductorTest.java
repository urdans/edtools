package test.java;

import eecalcs.conductors.*;
import eecalcs.conductors.raceways.Bundle;
import eecalcs.conductors.raceways.Cable;
import eecalcs.conductors.raceways.Conductor;
import eecalcs.conductors.raceways.Conduit;
import eecalcs.conduits.Type;
import eecalcs.conductors.TempRating;
import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConductorTest {
    Conductor conductor;
    Bundle bundle;
    Conduit conduit;

    @Test
    void testCopy() {
        Conductor cond1 = new Conductor()
                .setSize(Size.AWG_8)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.XHHW)
                .setLength(123);
        Conduit conduit = new Conduit(105)
                .setType(Type.EMT)
                .setNipple();
        conduit.add(cond1);
        cond1.setCopperCoating(Coating.COATED);
        cond1.setRole(Conductor.Role.GND);
        String cond1S = cond1.hasConduit()+", "+cond1.getCopperCoating()+
                ", "+cond1.getCurrentCarryingCount()+", "+cond1.getDescription()
                +", "+cond1.getInsulatedAreaIn2()+", "+cond1.getInsulation().getName()+", "+cond1.getLength()+", "+cond1.getMetal().getSymbol()+", "+ cond1.getRole()+", "+cond1.getSize().getName()+", "+cond1.getTemperatureRating();
        //--cloning
        Conductor cond2 = cond1.copy();
        String cond2S = cond2.hasConduit()+", "+cond2.getCopperCoating()+", "+cond2.getCurrentCarryingCount()+", "+cond2.getDescription()
                +", "+cond2.getInsulatedAreaIn2()+", "+cond2.getInsulation().getName()+", "+cond2.getLength()+", "+cond2.getMetal().getSymbol()+", "+ cond2.getRole()+", "+cond2.getSize().getName()+", "+cond2.getTemperatureRating();
        cond2.setAmbientTemperatureF(155);
        cond2.setCopperCoating(Coating.UNCOATED);
        cond2.setRole(Conductor.Role.HOT);
        cond2.setSize(Size.AWG_4$0);
        cond2.setMetal(Metal.COPPER);
        cond2.setInsulation(Insul.TW);
        cond2.setLength(78);
        String cond1SS = cond1.hasConduit()+", "+cond1.getCopperCoating()+", "+cond1.getCurrentCarryingCount()+", "+cond1.getDescription()
                +", "+cond1.getInsulatedAreaIn2()+", "+cond1.getInsulation().getName()+", "+cond1.getLength()+", "+cond1.getMetal().getSymbol()+", "+ cond1.getRole()+", "+cond1.getSize().getName()+", "+cond1.getTemperatureRating();
        String cond2SS = cond2.hasConduit()+", "+cond2.getCopperCoating()+", "+cond2.getCurrentCarryingCount()+", "+cond2.getDescription()
                +", "+cond2.getInsulatedAreaIn2()+", "+cond2.getInsulation().getName()+", "+cond2.getLength()+", "+cond2.getMetal().getSymbol()+", "+ cond2.getRole()+", "+cond2.getSize().getName()+", "+cond2.getTemperatureRating();
        assertEquals(cond1S, cond1SS);
        assertNotEquals(cond2S, cond2SS);
    }

    @Test
    void getTemperatureRating() {
        Conductor conductor = new Conductor();
        assertSame(conductor.getTemperatureRating(), TempRating.T75);

        conductor.setInsulation(Insul.XHHW2);
        assertSame(conductor.getTemperatureRating(), TempRating.T90);

        conductor.setInsulation(Insul.TW);
        assertSame(conductor.getTemperatureRating(), TempRating.T60);
    }

    @Test
    void setAmbientTemperatureF() {
        Bundle bundle1 = new Bundle(86);
        Cable cable1 = new Cable(VoltageAC.v120_1ph_2w);
        Cable cable2 = new Cable(VoltageAC.v120_1ph_2w);
        Cable cable3 = new Cable(VoltageAC.v120_1ph_2w);
        bundle1.setBundlingLength(30);
        bundle1.add(cable1);
        bundle1.add(cable2);
        bundle1.add(cable3);
        assertEquals(86, cable1.getAmbientTemperatureF());
        assertEquals(86, cable2.getAmbientTemperatureF());
        assertEquals(86, cable3.getAmbientTemperatureF());

        cable2 = cable2.copy().setAmbientTemperatureF(95);
        assertEquals(86, cable1.getAmbientTemperatureF());
        assertEquals(95, cable2.getAmbientTemperatureF());
        assertEquals(86, cable3.getAmbientTemperatureF());
    }

    @Test
    void getAmpacity() {
        conductor = new Conductor()
                .setSize(Size.AWG_12)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THHN)
                .setLength(125);
        conduit = new Conduit(100)
                .setType(Type.PVC80)
                .setNonNipple();
        conduit.add(conductor);
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        assertEquals(30*0.91*0.8, conductor.getCorrectedAndAdjustedAmpacity());

        bundle = new Bundle(100);
        bundle.setBundlingLength(25);
        conductor = conductor.copy();
        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        assertEquals(30*0.91*0.7, conductor.getCorrectedAndAdjustedAmpacity());
    }

    @Test
    void getCorrectionFactor() {
        conductor = new Conductor()
                .setSize(Size.KCMIL_250)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.TW)
                .setLength(125);
        assertEquals(1, conductor.getCorrectionFactor());

        conductor.setAmbientTemperatureF(100);
        assertEquals(0.82, conductor.getCorrectionFactor(), 0.001);

        conduit = new Conduit(100)
                .setType(Type.PVC80)
                .setNonNipple();
        conduit.add(conductor);
        conduit.setRoofTopDistance(10);
        assertEquals(0.41, conductor.getCorrectionFactor(), 0.001);

        conductor.setInsulation(Insul.XHHW2);
        assertEquals(0.91, conductor.getCorrectionFactor(), 0.001);
    }

    @Test
    void getAdjustmentFactor01() {
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        conduit = new Conduit(86)
                .setType(Type.EMT)
                .setNonNipple();
        conduit.add(conductor);
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        assertEquals(0.8, conductor.getAdjustmentFactor());
        assertEquals(0.8, conduit.getConduitables().get(2).getAdjustmentFactor());

        conduit.setNipple();
        assertEquals(1, conductor.getAdjustmentFactor());

        conduit.setNonNipple();
        assertEquals(0.8, conductor.getAdjustmentFactor());
    }

    //conduit.getConduitables().get(2).leaveConduit();
    @Test
    void getAdjustmentFactor02() {
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        conduit = new Conduit(86)
                .setType(Type.EMT)
                .setNonNipple();
        conduit.add(conductor);
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        assertEquals(1, conductor.getAdjustmentFactor());
        assertEquals(3, conduit.getCurrentCarryingCount());
    }

    @Test
    void getAdjustmentFactor03() {
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        conduit = new Conduit(86)
                .setType(Type.EMT)
                .setNonNipple();
        conduit.add(conductor);
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        Conductor conductor1 = conductor.copy();
        conduit.add(conductor1);
        conduit.add(conductor1.copy());
        conduit.add(conductor1.copy());
        conduit.add(conductor1.copy());
        assertEquals(7, conduit.getCurrentCarryingCount());
        assertEquals(0.7, conductor.getAdjustmentFactor());
    }

    @Test
    void getAdjustmentFactor04() {
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        conduit = new Conduit(86)
                .setType(Type.EMT)
                .setNonNipple();
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        Conductor conductor1 = conductor.copy();
        conduit.add(conductor1);
        conduit.add(conductor1.copy());
        conduit.add(conductor1.copy());
        conduit.add(conductor1.copy());
        assertEquals(6, conduit.getCurrentCarryingCount());
        assertEquals(0.8, conductor1.getAdjustmentFactor());
    }

    @Test
    void getAdjustmentFactor05() {
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        conduit = new Conduit(86)
                .setType(Type.EMT)
                .setNonNipple();
        Conductor conductor1 = conductor.copy();
        assertEquals(1, conductor.getAdjustmentFactor());
        assertEquals(1, conductor1.getAdjustmentFactor());
        assertFalse(conductor.hasConduit());
        assertFalse(conductor1.hasConduit());
    }

    @Test
    void getAdjustmentFactor06() {
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        bundle = new Bundle(86);
        bundle.setBundlingLength(25);
        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        assertEquals(25, bundle.getBundlingLength());
        assertEquals(0.8, conductor.getAdjustmentFactor());
    }

    @Test
    void getAdjustmentFactor07() {
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        Conductor conductor1 = conductor.copy();
        bundle = new Bundle(86);
        bundle.setBundlingLength(25);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        assertEquals(3, bundle.getCurrentCarryingCount());
        assertEquals(1, conductor.getAdjustmentFactor());

        bundle.setBundlingLength(24);
        assertEquals(1, conductor.getAdjustmentFactor());
        assertFalse(conductor1.hasConduit());
        assertFalse(conductor1.hasBundle());
    }

    @Test
    void illustrateGuideExample1Page101(){
        Conductor conductor2 =
        new Conductor().setSize(Size.AWG_2)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THWN2)
                .setLength(10);
        Conduit raceway = new Conduit(110)
                .setType(Type.PVC40)
                .setNonNipple();
        raceway.setRoofTopDistance(4);
        raceway.add(conductor2);
        raceway.add(conductor2.copy());
        assertEquals(1.0,conductor2.getAdjustmentFactor());
        assertEquals(0.71,conductor2.getCorrectionFactor());
        assertEquals(92.3,conductor2.getCorrectedAndAdjustedAmpacity());
    }

    @Test
    void illustrateGuideExample2Page101(){
        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_2)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.XHHW2)
                .setLength(10);
        Conduit raceway = new Conduit(110)
                .setType(Type.PVC40)
                .setNonNipple();
        raceway.setRoofTopDistance(4);
        raceway.add(conductor2);
        raceway.add(conductor2.copy());
        assertEquals(1.0,conductor2.getAdjustmentFactor());
        assertEquals(0.87,conductor2.getCorrectionFactor());
        assertEquals(113.1,conductor2.getCorrectedAndAdjustedAmpacity());
    }

    @Test
    void illustrateGuideContinuousLoadPage102_case1(){
        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_12)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THHW)
                .setLength(10);
        Conduit raceway = new Conduit(TempRating.getFahrenheit(43))
                .setType(Type.PVC40)
                .setNonNipple();
        raceway.add(conductor2);
        raceway.add(conductor2.copy());
        raceway.add(conductor2.copy());
        raceway.add(conductor2.copy());
        raceway.add(conductor2.copy());
        raceway.add(conductor2.copy());
        assertEquals(6, raceway.getCurrentCarryingCount());
        assertEquals(0.87, conductor2.getCorrectionFactor());
        assertEquals(0.8, conductor2.getAdjustmentFactor());
        assertEquals(0.696, conductor2.getCompoundFactor(), 0.0001);
        assertEquals(0.568, conductor2.getCompoundFactor(TempRating.T60));
        assertEquals(0.656, conductor2.getCompoundFactor(TempRating.T75));
        assertEquals(0.696, conductor2.getCompoundFactor(TempRating.T90), 0.0001);
        assertEquals(20.880, conductor2.getCorrectedAndAdjustedAmpacity(), 0.0001);
    }

    @Test
    void copyFrom(){
        Conductor conductor2 = new Conductor()
                .setSize(Size.KCMIL_350)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.TBS)
                .setLength(1.1234);
        conductor2.setAmbientTemperatureF(321);
        conductor2.setCopperCoating(Coating.COATED);
        conductor2.setRole(Conductor.Role.NCONC);
        conductor = new Conductor();
        assertEquals(Size.AWG_12, conductor.getSize());

        conductor.copyFrom(conductor2);
        assertEquals(Size.KCMIL_350, conductor.getSize());
        assertEquals(Metal.ALUMINUM, conductor.getMetal());
        assertEquals(Insul.TBS, conductor.getInsulation());
        assertEquals(1.1234, conductor.getLength());
        assertEquals(321, conductor.getAmbientTemperatureF());
        assertEquals(Coating.COATED, conductor.getCopperCoating());
        assertEquals(Conductor.Role.NCONC, conductor.getRole());
    }

    @Test
    void copyAndClone(){
        Conductor phaseA = new Conductor();
        phaseA.setRole(Conductor.Role.HOT);
        Conductor phaseB = phaseA.copy();
        phaseA.setInsulation(Insul.TBS);
        assertEquals(Insul.THW, phaseB.getInsulation());

        phaseA.setSize(Size.KCMIL_2000);
        phaseB.copyFrom(phaseA);
        assertEquals(Size.KCMIL_2000, phaseB.getSize());
        assertEquals(Insul.TBS, phaseB.getInsulation());
    }

    @Test
    void errorMessages(){
        conductor = new Conductor();

        conductor.setSize(null);
        assertTrue(conductor.getResultMessages().containsMessage(Conductor.ERROR050));
        assertNull(conductor.getSize());

        conductor.setSize(Size.AWG_8);
        assertFalse(conductor.getResultMessages().hasErrors());
        assertEquals(Size.AWG_8, conductor.getSize());
//--
        conductor.setMetal(null);
        assertTrue(conductor.getResultMessages().containsMessage(Conductor.ERROR051));
        assertNull(conductor.getMetal());

        conductor.setMetal(Metal.ALUMINUM);
        assertFalse(conductor.getResultMessages().hasErrors());
        assertEquals(Metal.ALUMINUM, conductor.getMetal());
//--
        conductor.setInsulation(null);
        assertTrue(conductor.getResultMessages().containsMessage(Conductor.ERROR052));
        assertNull(conductor.getInsulation());

        conductor.setInsulation(Insul.THWN2);
        assertFalse(conductor.getResultMessages().hasErrors());
        assertEquals(Insul.THWN2, conductor.getInsulation());
//--
        conductor.setLength(0);
        assertTrue(conductor.getResultMessages().containsMessage(Conductor.ERROR053));
        assertEquals(0, conductor.getLength());

        conductor.setLength(12345);
        assertFalse(conductor.getResultMessages().hasErrors());
        assertEquals(12345, conductor.getLength());
//--
        conductor.setAmbientTemperatureF(186);
        assertTrue(conductor.getResultMessages().containsMessage(Conductor.ERROR054));
        assertEquals(186, conductor.getAmbientTemperatureF());

        conductor.setAmbientTemperatureF(154);
        assertFalse(conductor.getResultMessages().hasErrors());
        assertEquals(154, conductor.getAmbientTemperatureF());
//--
        conductor.setCopperCoating(null);
        assertTrue(conductor.getResultMessages().containsMessage(Conductor.ERROR055));
        assertNull(conductor.getCopperCoating());

        conductor.setCopperCoating(Coating.COATED);
        assertFalse(conductor.getResultMessages().hasErrors());
        assertEquals(Coating.COATED, conductor.getCopperCoating());
//--
        conductor.setRole(null);
        assertTrue(conductor.getResultMessages().containsMessage(Conductor.ERROR056));
        assertNull(conductor.getRole());

        conductor.setRole(Conductor.Role.GND);
        assertFalse(conductor.getResultMessages().hasErrors());
        assertEquals(Conductor.Role.GND, conductor.getRole());
//--
        assertNotEquals("", conductor.getRole().getDescription());
        assertEquals(5, Conductor.Role.getDescriptions().length);
        assertEquals(1, conductor.getCompoundFactor(null));
        conductor.setRole(null);
        assertEquals(0, conductor.getInsulatedAreaIn2());
        assertEquals(0, conductor.getCorrectedAndAdjustedAmpacity());
        assertEquals(0, conductor.getCorrectionFactor());
        assertEquals(0, conductor.getAdjustmentFactor());
        assertEquals(0, conductor.getCompoundFactor());
        assertEquals(0, conductor.getCompoundFactor(TempRating.T60));
        assertNull(conductor.getTemperatureRating());
        assertEquals(0, conductor.getCurrentCarryingCount());
        assertEquals("", conductor.getDescription());
        assertNotEquals("", conductor.toString());
//--
        conduit = new Conduit(85);
        conduit.add(conductor);
        assertThrows(IllegalArgumentException.class, ()->
                conductor.setAmbientTemperatureF(99));
        assertNotEquals("", conductor.toJSON());

        System.out.println(conductor.toJSON());
    }

}