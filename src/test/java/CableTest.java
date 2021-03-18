package test.java;

import eecalcs.conductors.*;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Type;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CableTest {
    private Cable cable;

    @Test
    void getInsulatedAreaIn2() {
        double diameter = 2;
        cable = new Cable(VoltageSystemAC.v277_1ph_2w, diameter);
        assertEquals(diameter*diameter*0.25*Math.PI, cable.getInsulatedAreaIn2());
        diameter = 0.5; cable.setOuterDiameter(diameter);
        assertEquals(diameter*diameter*0.25*Math.PI, cable.getInsulatedAreaIn2());
        diameter = 0; cable.setOuterDiameter(diameter);
        assertEquals(0.25*0.5*0.5*Math.PI, cable.getInsulatedAreaIn2());
    }

    @Test
    void getCurrentCarryingCount() {
        Cable cable2 = new Cable();
        assertEquals(2, cable2.getCurrentCarryingCount());

        cable = new Cable(VoltageSystemAC.v480_3ph_4w, 1);
        assertEquals(3, cable.getCurrentCarryingCount());

        cable.setNeutralCarryingConductor(true);
        assertEquals(4, cable.getCurrentCarryingCount());

        cable.setNeutralCarryingConductor(false);
        assertEquals(3, cable.getCurrentCarryingCount());

        cable.setNeutralCarryingConductor(true);
        cable.setSystem(VoltageSystemAC.v240_3ph_3w);
        assertEquals(3, cable.getCurrentCarryingCount());

        cable.setSystem(VoltageSystemAC.v208_3ph_4w);
        assertEquals(4, cable.getCurrentCarryingCount());

        cable.setSystem(VoltageSystemAC.v240_1ph_2w);
        assertEquals(2, cable.getCurrentCarryingCount());

        cable.setSystem(VoltageSystemAC.v240_1ph_3w);
        assertEquals(3, cable.getCurrentCarryingCount());

        cable.setSystem(VoltageSystemAC.v120_1ph_2w);
        assertEquals(2, cable.getCurrentCarryingCount());

        cable.setSystem(VoltageSystemAC.v240_3ph_4w);
        assertEquals(4, cable.getCurrentCarryingCount());
    }

    @Test
    void getAmpacity() {
        cable = new Cable(VoltageSystemAC.v277_1ph_2w, 1);
        cable.setNeutralConductorSize(Size.KCMIL_300);
        assertEquals(285, cable.getCorrectedAndAdjustedAmpacity());

        cable.setAmbientTemperatureF(100);
        assertEquals(285*0.88, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(38);
        assertEquals(285*0.88, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(36);
        assertEquals(285*0.67, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(6);
        assertEquals(285*0.67, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(2);
        assertEquals(285*0.58, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(0.25);
        assertEquals(285*0, cable.getCorrectedAndAdjustedAmpacity());

        cable.resetRoofTop();
        assertEquals(285*0.88, cable.getCorrectedAndAdjustedAmpacity());

        Conduit conduit = new Conduit(Type.ENT, false);
        conduit.add(cable);
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        cable.setAmbientTemperatureF(95);
        cable.setRoofTopDistance(20);//this has no effect since the cable is inside a conduit.
        assertFalse(cable.isRooftopCondition());
        assertEquals(TempRating.T75, cable.getTemperatureRating());
        assertEquals(7, conduit.getCurrentCarryingCount());
        //this ignores the rooftop condition since the conduit is not in that condition.
        assertEquals(285*0.94*0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        conduit.setRoofTopDistance(10);
        assertEquals(285*0.67*0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        conduit.resetRoofTop();
        assertEquals(285*0.94*0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.leaveConduit();
        assertEquals(285*0.75*1, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
        assertEquals(5, conduit.getCurrentCarryingCount());

        cable.setConduit(conduit);
        assertEquals(7, conduit.getCurrentCarryingCount());

        conduit.setRoofTopDistance(20);
        Conduitable fithConductor = conduit.getConduitables().get(4);
        conduit.remove(fithConductor);
        assertEquals(6, conduit.getCurrentCarryingCount());
        assertEquals(285*0.75*0.8, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        conduit.setNipple(true);
        assertEquals(285*0.75*1, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.setMetal(Metal.ALUMINUM);
        cable.setInsulation(Insul.THHN);
        assertEquals(6, conduit.getCurrentCarryingCount());

        cable.resetRoofTop();
        conduit.resetRoofTop();
        assertEquals(260*0.96*1, cable.getCorrectedAndAdjustedAmpacity());

        conduit.add(fithConductor);
        conduit.setNipple(false);
        assertEquals(260*0.96*0.7, cable.getCorrectedAndAdjustedAmpacity(),0.01);

        cable.setType(CableType.MC);
        conduit.remove(cable);
        assertEquals(260*0.96*1, cable.getCorrectedAndAdjustedAmpacity(),0.01);

        cable.setPhaseConductorSize(Size.AWG_12);
        cable.setMetal(Metal.COPPER);
        assertEquals(30*0.96*1.0, cable.getCorrectedAndAdjustedAmpacity());

        cable.setType(CableType.NM);
        cable.setAmbientTemperatureF(65);
        cable.setInsulation(Insul.RHW);
        cable.setJacketed(true);
        assertEquals(25*1.11*1.0, cable.getCorrectedAndAdjustedAmpacity());
        assertFalse(cable.isJacketed());

        cable.setType(CableType.AC);
        cable.setJacketed(true);
        assertTrue(cable.isJacketed());

        conduit.empty();
        cable.setConduit(conduit);
        assertEquals(2, conduit.getCurrentCarryingCount());
        assertEquals(25*1.11*1.0, cable.getCorrectedAndAdjustedAmpacity());

        conduit.add(cable.clone());
        assertEquals(4, conduit.getCurrentCarryingCount());
        assertEquals(25*1.11*0.8, cable.getCorrectedAndAdjustedAmpacity());

        conduit.add(cable.clone());
        conduit.add(cable.clone());
        conduit.add(cable.clone());
        conduit.add(cable.clone());
        conduit.add(cable.clone());
        conduit.add(cable.clone());
        conduit.add(cable.clone());
        conduit.add(cable.clone());
        conduit.add(cable.clone());
        assertEquals(22, conduit.getCurrentCarryingCount());
        assertEquals(25*1.11*0.45, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        conduit.empty();
        assertEquals(0, conduit.getCurrentCarryingCount());
        assertEquals(25*1.11*1.0, cable.getCorrectedAndAdjustedAmpacity());

        Bundle bundle = new Bundle();//new Bundle(cable, 5, 10);
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.setBundlingLength(10);
        assertEquals(10, bundle.getCurrentCarryingCount());

        cable.setBundle(bundle);
        assertEquals(12, bundle.getCurrentCarryingCount());
        assertTrue(cable.isJacketed());
        assertTrue(((Cable)bundle.getConduitables().get(4)).isJacketed());
        assertEquals(65, cable.getAmbientTemperatureF());
        assertEquals(65, bundle.getConduitables().get(4).getAmbientTemperatureF());
        assertEquals(1, Factors.getAdjustmentFactor(bundle.getCurrentCarryingCount(), bundle.getBundlingLength()));
        assertEquals(1.11, Factors.getTemperatureCorrectionF(cable.getAmbientTemperatureF(), cable.getTemperatureRating()));
        assertEquals(1, cable.getAdjustmentFactor());
        assertEquals(25*1.11*1, cable.getCorrectedAndAdjustedAmpacity());

        bundle.getConduitables().forEach(conduitable -> ((Cable)conduitable).setJacketed(true));
        assertTrue(cable.isJacketed());
        assertEquals(Size.AWG_12, cable.getPhaseConductorSize());
        assertEquals(Metal.COPPER, cable.getMetal());
        assertEquals(2, cable.getCurrentCarryingCount());
        assertEquals(12, bundle.getCurrentCarryingCount());
        assertFalse(bundle.complyWith310_15_B_3_a_4());
        assertEquals(25*1.11*1, cable.getCorrectedAndAdjustedAmpacity());
        bundle.setBundlingLength(25);
        assertEquals(25*1.11*0.5, cable.getCorrectedAndAdjustedAmpacity());


        bundle.getConduitables().forEach(conduitable -> ((Cable)conduitable).setJacketed(false));
        assertTrue(bundle.complyWith310_15_B_3_a_4());
        assertEquals(25*1.11*1, cable.getCorrectedAndAdjustedAmpacity());

        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        assertEquals(22, bundle.getCurrentCarryingCount());
        assertFalse(bundle.complyWith310_15_B_3_a_4());
        assertTrue(bundle.complyWith310_15_B_3_a_5());
        assertEquals(25*1.11*0.6, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.setType(CableType.MC);
        assertEquals(25*1.11*0.6, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.setType(CableType.NMS);
        assertEquals(25*1.11*0.45, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        //Running the example of the NEC2014 Handbook, page 262
        bundle.empty();
        cable.setAmbientTemperatureF(86);
        assertEquals(86, cable.getAmbientTemperatureF());

        cable.setType(CableType.MC);
        cable.setInsulation(Insul.THHN);
        cable.setSystem(VoltageSystemAC.v480_1ph_3w);
        assertEquals(3, cable.getCurrentCarryingCount());

        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable.clone());
        bundle.add(cable);
        assertEquals(21, bundle.getCurrentCarryingCount());
        assertTrue(bundle.complyWith310_15_B_3_a_5());
        assertEquals(86, cable.getAmbientTemperatureF());
        assertEquals(1, Factors.getTemperatureCorrectionF(cable.getAmbientTemperatureF(), cable.getTemperatureRating()));
        assertEquals(30*1*0.60, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.leaveBundle();
        assertEquals(30*1*1, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        bundle.empty();
        assertEquals(0, bundle.getCurrentCarryingCount());

    }

    @Test
    void setNeutralConductorSize(){
        cable = new Cable(VoltageSystemAC.v277_1ph_2w, 1);
        assertEquals(Size.AWG_12, cable.getGroundingConductorSize());

        cable.setNeutralConductorSize(Size.KCMIL_300);
        assertEquals(Size.AWG_12, cable.getGroundingConductorSize());
        assertEquals(Size.KCMIL_300, cable.getPhaseConductorSize());
        assertEquals(Size.KCMIL_300, cable.getNeutralConductorSize());

        cable.setSystem(VoltageSystemAC.v480_3ph_4w);
        assertEquals(Size.KCMIL_300, cable.getPhaseConductorSize());
        assertEquals(Size.KCMIL_300, cable.getNeutralConductorSize());
        assertEquals(Size.AWG_12, cable.getGroundingConductorSize());

        cable.setGroundingConductorSize(Size.KCMIL_250);
        assertEquals(Size.KCMIL_300, cable.getPhaseConductorSize());
        assertEquals(Size.KCMIL_300, cable.getNeutralConductorSize());
        assertEquals(Size.KCMIL_250, cable.getGroundingConductorSize());
    }

    @Test
    void getPhaseConductorSize() {
        cable = new Cable(VoltageSystemAC.v277_1ph_2w, 1);
        cable.setPhaseConductorSize(Size.KCMIL_250);
        cable.setNeutralConductorSize(Size.KCMIL_300);
        assertEquals(Size.KCMIL_300, cable.getPhaseConductorSize());
        cable.setNeutralConductorSize(Size.AWG_14);
        assertEquals(Size.AWG_14, cable.getPhaseConductorSize());
    }

    @Test
    void testClone() {
        Cable cable1 = new Cable(VoltageSystemAC.v208_3ph_3w, 1.5);
        Conduit conduit = new Conduit(Type.EMT, true);
        conduit.add(cable1);
        cable1.setType(CableType.NMS);
        cable1.setJacketed(true);
        cable1.setRoofTopDistance(20);
        cable1.setNeutralCarryingConductor(true);
        String cable1S = cable1.toString();
        //--cloning
        Cable cable2 = cable1.clone();
        String cable2S = cable2.toString();

        cable2.setType(CableType.AC);
        cable2.setJacketed(false);
        cable2.setRoofTopDistance(25);
        cable2.setNeutralCarryingConductor(false);
        cable2.setSystem(VoltageSystemAC.v277_1ph_2w); //it should assume it as W2
        cable2.setOuterDiameter(0.5);

        String cable1SS = cable1.toString();
        String cable2SS = cable2.toString();

        assertEquals(cable1S, cable1SS);
        assertNotEquals(cable2S, cable2SS);
    }

    @Test
    void bundleAndConduit(){
        Cable cable1 = new Cable();
        Bundle bundle1 = new Bundle();//new Bundle(cable1, 5, 60);
        bundle1.add(cable1.clone());
        bundle1.add(cable1.clone());
        bundle1.add(cable1.clone());
        bundle1.add(cable1.clone());
        bundle1.add(cable1.clone());
        bundle1.setBundlingLength(60);
        assertEquals(5, bundle1.getConduitables().size());
        assertFalse(bundle1.getConduitables().contains(cable1));

        bundle1.add(cable1);          //works with this one or
        //cable1.setBundle(bundle1);  //with this one
        assertEquals(6, bundle1.getConduitables().size());
        assertTrue(bundle1.getConduitables().contains(cable1));

        //bundle1.remove(cable1);   //works with this one or
        cable1.leaveBundle();       //with this one
        assertEquals(5, bundle1.getConduitables().size());
        assertFalse(bundle1.getConduitables().contains(cable1));

        cable1.setBundle(bundle1);
        assertEquals(6, bundle1.getConduitables().size());

        Cable cable2 = new Cable(VoltageSystemAC.v480_3ph_3w, 5);
        Bundle bundle2 = new Bundle();//new Bundle(cable2, 3, 35);
        bundle2.add(cable2.clone());
        bundle2.add(cable2.clone());
        bundle2.add(cable2.clone());

        bundle2.add(cable2);
        assertEquals(4, bundle2.getConduitables().size());

        //moving cable2 to bundle1
        bundle1.add(cable2);
        assertEquals(7, bundle1.getConduitables().size());
        assertTrue(bundle1.getConduitables().contains(cable2));
        assertFalse(bundle2.getConduitables().contains(cable2));
        assertSame(cable2.getBundle(), bundle1);
        assertEquals(3, bundle2.getConduitables().size());

        //moving cable1 to bundle2
        cable1.setBundle(bundle2);
        assertEquals(6, bundle1.getConduitables().size());
        assertEquals(4, bundle2.getConduitables().size());
        assertTrue(bundle2.getConduitables().contains(cable1));
        assertFalse(bundle1.getConduitables().contains(cable1));
        assertSame(cable1.getBundle(), bundle2);

        Conduit conduit1 = new Conduit(Type.PVC40, false);
        //moving cable1 to conduit1
        conduit1.add(cable1);
        assertTrue(conduit1.getConduitables().contains(cable1));
        assertFalse(bundle2.getConduitables().contains(cable1));
        assertTrue(cable1.hasConduit());
        assertFalse(cable1.hasBundle());
        assertNull(cable1.getBundle());
        assertSame(cable1.getConduit(), conduit1);

        //moving cable2 to conduit1
        cable2.setConduit(conduit1);
        assertTrue(conduit1.getConduitables().contains(cable2));
        assertFalse(bundle1.getConduitables().contains(cable2));
        assertEquals(2, conduit1.getConduitables().size());
        assertEquals(5, bundle1.getConduitables().size());
        assertEquals(3, bundle2.getConduitables().size());
        assertTrue(cable2.hasConduit());
        assertFalse(cable2.hasBundle());
        assertNull(cable2.getBundle());
        assertSame(cable2.getConduit(), conduit1);

        //putting cable1, cable2 and cable3 (first of bundle1) in free air.
        Cable cable3 = (Cable) bundle1.getConduitables().get(0);
        cable1.leaveConduit();
        cable2.leaveConduit();
        cable3.leaveBundle();
        //bundle1.remove(cable3);
        assertFalse(conduit1.getConduitables().contains(cable1));
        assertFalse(conduit1.getConduitables().contains(cable2));

        assertNull(cable1.getBundle());
        assertNull(cable1.getConduit());

        assertNull(cable2.getBundle());
        assertNull(cable2.getConduit());

        assertNull(cable3.getBundle());
        assertNull(cable3.getConduit());

        assertFalse(bundle1.getConduitables().contains(cable3));
        assertEquals(0, conduit1.getConduitables().size());
        assertEquals(4, bundle1.getConduitables().size());
        assertEquals(3, bundle2.getConduitables().size());

    }

    @Test
    void setNeutralCarryingConductor(){
        cable = new Cable();
        cable.setSystem(VoltageSystemAC.v480_3ph_4w);
        assertEquals(3, cable.getCurrentCarryingCount());

        cable.setNeutralCarryingConductor(true);
        assertEquals(4, cable.getCurrentCarryingCount());

        cable.setNeutralCarryingConductor(false);
        assertEquals(3, cable.getCurrentCarryingCount());

    }

}