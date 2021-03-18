package test.java;

import eecalcs.conductors.*;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Type;
import eecalcs.systems.TempRating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConductorTest {
    Conductor conductor;
    Bundle bundle;
    Conduit conduit;

    @Test
    void testClone() {
        Conductor cond1 = new Conductor(Size.AWG_8, Metal.ALUMINUM, Insul.XHHW, 123);
        Conduit conduit = new Conduit(Type.EMT, true);
        conduit.add(cond1);
        cond1.setAmbientTemperatureF(105);
        cond1.setCopperCoated(Coating.COATED);
        cond1.setRole(Conductor.Role.GND);
        String cond1S = cond1.getConduit()+", "+cond1.getCopperCoating()+", "+cond1.getCurrentCarryingCount()+", "+cond1.getDescription()
                +", "+cond1.getInsulatedAreaIn2()+", "+cond1.getInsulation().getName()+", "+cond1.getLength()+", "+cond1.getMetal().getSymbol()+", "+ cond1.getRole()+", "+cond1.getSize().getName()+", "+cond1.getTemperatureRating();
        //--cloning
        Conductor cond2 = cond1.clone();
        String cond2S = cond2.getConduit()+", "+cond2.getCopperCoating()+", "+cond2.getCurrentCarryingCount()+", "+cond2.getDescription()
                +", "+cond2.getInsulatedAreaIn2()+", "+cond2.getInsulation().getName()+", "+cond2.getLength()+", "+cond2.getMetal().getSymbol()+", "+ cond2.getRole()+", "+cond2.getSize().getName()+", "+cond2.getTemperatureRating();

        cond2.setAmbientTemperatureF(155);
        cond2.setCopperCoated(Coating.UNCOATED);
        cond2.setRole(Conductor.Role.HOT);
        cond2.setSize(Size.AWG_4$0);
        cond2.setMetal(Metal.COPPER);
        cond2.setInsulation(Insul.TW);
        cond2.setLength(78);
        String cond1SS = cond1.getConduit()+", "+cond1.getCopperCoating()+", "+cond1.getCurrentCarryingCount()+", "+cond1.getDescription()
                +", "+cond1.getInsulatedAreaIn2()+", "+cond1.getInsulation().getName()+", "+cond1.getLength()+", "+cond1.getMetal().getSymbol()+", "+ cond1.getRole()+", "+cond1.getSize().getName()+", "+cond1.getTemperatureRating();
        String cond2SS = cond2.getConduit()+", "+cond2.getCopperCoating()+", "+cond2.getCurrentCarryingCount()+", "+cond2.getDescription()
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
        Bundle bundle1 = new Bundle();//new Bundle(null, 0, 30);
        Cable cable1 = new Cable();
        Cable cable2 = new Cable();
        Cable cable3 = new Cable();
        bundle1.setBundlingLength(30);
        bundle1.add(cable1);
        bundle1.add(cable2);
        bundle1.add(cable3);
        assertEquals(86, cable3.getAmbientTemperatureF());

        cable1.setAmbientTemperatureF(100);
        assertEquals(100, cable1.getAmbientTemperatureF());
        assertEquals(100, cable2.getAmbientTemperatureF());
        assertEquals(100, cable3.getAmbientTemperatureF());

        cable2.leaveBundle();
        cable2.setAmbientTemperatureF(95);
        assertEquals(100, cable1.getAmbientTemperatureF());
        assertEquals(95, cable2.getAmbientTemperatureF());
        assertEquals(100, cable3.getAmbientTemperatureF());

    }

    @Test
    void getAmpacity() {
        conductor = new Conductor(Size.AWG_12, Metal.COPPER, Insul.THHN, 125);
        conduit = new Conduit(Type.PVC80, false);
        conduit.add(conductor);
        conduit.add(conductor.clone());
        conduit.add(conductor.clone());
        conduit.add(conductor.clone());
        conductor.setAmbientTemperatureF(100);
        assertEquals(30*0.91*0.8, conductor.getCorrectedAndAdjustedAmpacity());

        bundle = new Bundle(/*null, 0, 25*/);
        bundle.setBundlingLength(25);
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        assertEquals(30*0.91*0.7, conductor.getCorrectedAndAdjustedAmpacity());
    }

    @Test
    void getCorrectionFactor() {
        conductor = new Conductor(Size.KCMIL_250, Metal.ALUMINUM, Insul.TW, 125);
        assertEquals(1, conductor.getCorrectionFactor());

        conductor.setAmbientTemperatureF(100);
        assertEquals(0.82, conductor.getCorrectionFactor(), 0.001);

        conduit = new Conduit(Type.PVC80, false);
        conduit.add(conductor);
        conduit.setRoofTopDistance(10);
        assertEquals(0.41, conductor.getCorrectionFactor(), 0.001);

        conductor.setInsulation(Insul.XHHW2);
        assertEquals(0.91, conductor.getCorrectionFactor(), 0.001);
    }

    @Test
    void getAdjustmentFactor() {
        conductor = new Conductor(Size.AWG_4, Metal.COPPER, Insul.THW, 70);
        conduit = new Conduit(Type.EMT, false);
        conduit.add(conductor);
        conduit.add(conductor.clone());
        conduit.add(conductor.clone());
        conduit.add(conductor.clone());
        assertEquals(0.8, conductor.getAdjustmentFactor());
        assertEquals(0.8, conduit.getConduitables().get(2).getAdjustmentFactor());

        conduit.setNipple(true);
        assertEquals(1, conductor.getAdjustmentFactor());

        conduit.setNipple(false);
        assertEquals(0.8, conductor.getAdjustmentFactor());

        conduit.getConduitables().get(2).leaveConduit();
        assertEquals(1, conductor.getAdjustmentFactor());
        assertEquals(3, conduit.getCurrentCarryingCount());

        Conductor conductor1 = conductor.clone();
        conductor1.setConduit(conduit);
        conduit.add(conductor1.clone());
        conduit.add(conductor1.clone());
        conduit.add(conductor1.clone());
        assertEquals(7, conduit.getCurrentCarryingCount());
        assertEquals(0.7, conductor.getAdjustmentFactor());

        conductor.leaveConduit();
        assertEquals(6, conduit.getCurrentCarryingCount());
        assertEquals(0.8, conductor1.getAdjustmentFactor());

        conduit.empty();
        assertEquals(1, conductor.getAdjustmentFactor());
        assertEquals(1, conductor1.getAdjustmentFactor());
        assertFalse(conductor.hasConduit());
        assertFalse(conductor1.hasConduit());

        bundle = new Bundle(/*null, 0, 25*/);
        bundle.setBundlingLength(25);
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        assertEquals(25, bundle.getBundlingLength());
        assertEquals(0.8, conductor.getAdjustmentFactor());

        conductor.leaveBundle();
        assertEquals(3, bundle.getCurrentCarryingCount());
        assertEquals(1, conductor.getAdjustmentFactor());

        bundle.setBundlingLength(24);
        assertEquals(1, conductor.getAdjustmentFactor());

        conduit.add(conductor1);
        assertTrue(conductor1.hasConduit());
        assertFalse(conductor1.hasBundle());
    }

    @Test
    void illustrateGuideExample1Page101(){
        Conductor conductor2 = new Conductor(Size.AWG_2, Metal.COPPER, Insul.THWN2, 10);
        Conduit raceway = new Conduit(Type.PVC40, false);
        raceway.setRoofTopDistance(4);
        raceway.add(conductor2);
        raceway.add(conductor2.clone());
        conductor2.setAmbientTemperatureF(110);
        assertEquals(1.0,conductor2.getAdjustmentFactor());
        assertEquals(0.71,conductor2.getCorrectionFactor());
        assertEquals(92.3,conductor2.getCorrectedAndAdjustedAmpacity());
    }

    @Test
    void illustrateGuideExample2Page101(){
        Conductor conductor2 = new Conductor(Size.AWG_2, Metal.COPPER, Insul.XHHW2, 10);
        Conduit raceway = new Conduit(Type.PVC40, false);
        raceway.setRoofTopDistance(4);
        raceway.add(conductor2);
        raceway.add(conductor2.clone());
        conductor2.setAmbientTemperatureF(110);
        assertEquals(1.0,conductor2.getAdjustmentFactor());
        assertEquals(0.87,conductor2.getCorrectionFactor());
        assertEquals(113.1,conductor2.getCorrectedAndAdjustedAmpacity());
    }

    @Test
    void illustrateGuideContinuousLoadPage102_case1(){
        Conductor conductor2 = new Conductor(Size.AWG_12, Metal.COPPER, Insul.THHW, 10);
        Conduit raceway = new Conduit(Type.PVC40, false);
        conductor2.setAmbientTemperatureF(TempRating.getFahrenheit(43));
        raceway.add(conductor2);
        raceway.add(conductor2.clone());
        raceway.add(conductor2.clone());
        raceway.add(conductor2.clone());
        raceway.add(conductor2.clone());
        raceway.add(conductor2.clone());
    }

    @Test
    void illustrateGuideContinuousLoadPage102_case2(){
        Conductor conductor2 = new Conductor(Size.AWG_12, Metal.COPPER, Insul.THHW, 10);
        Conduit raceway = new Conduit(Type.PVC40, false);
        conductor2.setAmbientTemperatureF(TempRating.getFahrenheit(43));
        raceway.add(conductor2);
        raceway.add(conductor2.clone());
        raceway.add(conductor2.clone());
        raceway.add(conductor2.clone());
        raceway.add(conductor2.clone());
        raceway.add(conductor2.clone());

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
        Conductor conductor2 = new Conductor(Size.KCMIL_350, Metal.ALUMINUM, Insul.TBS, 1.1234);
        conductor2.setAmbientTemperatureF(321);
        conductor2.setCopperCoated(Coating.COATED);
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
    void listenToSpeaker(){
        Conductor phaseA = new Conductor();
        phaseA.setRole(Conductor.Role.HOT);
        Conductor phaseB = phaseA.clone();
        phaseA.setInsulation(Insul.TBS);
        assertEquals(Insul.THW, phaseB.getInsulation());

        phaseA.getNotifier().addListener(speaker -> {
            if(speaker instanceof Conductor)
                phaseB.copyFrom((Conductor) speaker);
        });

        phaseA.setSize(Size.KCMIL_2000);
        assertEquals(Size.KCMIL_2000, phaseB.getSize());
        assertEquals(Insul.TBS, phaseB.getInsulation());

    }
}