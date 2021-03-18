package test.java;

import eecalcs.conductors.*;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BundleTest {
    private Bundle bundle = new Bundle();//new Bundle(null, 0, 25);

    @Test
    void add() {
        Conductor conductor = new Conductor();
        Cable cable = new Cable();
        assertEquals(0, bundle.getConduitables().size());

        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(cable);
        bundle.add(cable.clone());
        assertEquals(5, bundle.getConduitables().size());
    }

    @Test
    void remove() {
        Conductor conductor = new Conductor();
        Cable cable = new Cable();
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(cable);
        bundle.add(cable.clone());
        bundle.remove(conductor);
        bundle.remove(cable);
        assertEquals(3, bundle.getConduitables().size());
    }

    @Test
    void empty() {
        Conductor conductor = new Conductor();
        Cable cable = new Cable();
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(cable);
        bundle.add(cable.clone());
        bundle.empty();
        assertEquals(0, bundle.getConduitables().size());
    }

    @Test
    void isEmpty() {
        Conductor conductor = new Conductor();
        Cable cable = new Cable();
        assertTrue(bundle.isEmpty());
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(cable);
        bundle.add(cable.clone());
        assertFalse(bundle.isEmpty());
    }

    @Test
    void hasConduitable() {
        Conductor conductor = new Conductor();
        Cable cable = new Cable();
        assertTrue(bundle.isEmpty());
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(cable);
        bundle.add(cable.clone());
        assertTrue(bundle.hasConduitable(cable));
        assertTrue(bundle.hasConduitable(conductor));
        assertFalse(bundle.hasConduitable(conductor.clone()));
        assertFalse(bundle.hasConduitable(null));
    }

    @Test
    void getCurrentCarryingNumber() {
        Conductor conductor = new Conductor();
        Cable cable = new Cable();
        cable.setSystem(VoltageSystemAC.v480_3ph_4w);//3ccc
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(cable);
        bundle.add(cable.clone());
        assertEquals(9, bundle.getCurrentCarryingCount());
        cable.setNeutralCarryingConductor(true);
        assertEquals(10, bundle.getCurrentCarryingCount());
    }

    @Test
    void complyWith310_15_B_3_a_4() {
        Conductor conductor = new Conductor();
        Cable cable = new Cable();
        cable.setSystem(VoltageSystemAC.v480_3ph_4w);//3ccc
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(cable);
        bundle.add(cable.clone());
        assertEquals(9, bundle.getCurrentCarryingCount());

        //case 1
        assertTrue(bundle.complyWith310_15_B_3_a_4());

        bundle.setBundlingLength(25);
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(1, cable.getAdjustmentFactor()); //even if d>24", cables have this exception.


        //case2
        cable.setNeutralCarryingConductor(true);
        assertFalse(bundle.complyWith310_15_B_3_a_4());
        assertEquals(0.5, conductor.getAdjustmentFactor()); //because d>24", #ccc=10 and conductors don't have exceptions
        assertEquals(0.5, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (one cable
        // has more than 3 ccc.
        cable.setNeutralCarryingConductor(false);
        assertTrue(bundle.complyWith310_15_B_3_a_4());

        //case 3
        cable.setJacketed(true);
        assertFalse(bundle.complyWith310_15_B_3_a_4());
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(0.7, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (one cable
        // is jacketed)
        cable.setJacketed(false);
        assertTrue(bundle.complyWith310_15_B_3_a_4());

        //case 4
        cable.setType(CableType.NM);
        assertFalse(bundle.complyWith310_15_B_3_a_4());
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(0.7, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (this
        // cable is not AC nor MC type.
        cable.setType(CableType.AC);
        assertTrue(bundle.complyWith310_15_B_3_a_4());

        //case 5
        conductor.setMetal(Metal.ALUMINUM);
        assertFalse(bundle.complyWith310_15_B_3_a_4());
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(0.7, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (AL)
        conductor.setMetal(Metal.COPPER);
        assertTrue(bundle.complyWith310_15_B_3_a_4());

        //case 6
        conductor.setSize(Size.AWG_8);
        assertFalse(bundle.complyWith310_15_B_3_a_4());
        assertFalse(bundle.complyWith310_15_B_3_a_4());
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(0.7, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (not #12AWG)
        conductor.setSize(Size.AWG_12);
        assertTrue(bundle.complyWith310_15_B_3_a_4());

        //case 7
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        assertEquals(20, bundle.getCurrentCarryingCount());
        assertEquals(0.5, conductor.getAdjustmentFactor()); //because d>24", #ccc=20 and conductors don't have
        // exceptions
        assertEquals(1.0, cable.getAdjustmentFactor()); //because d>24" and the exception is satisfied
        bundle.add(conductor.clone());
        assertEquals(0.45, conductor.getAdjustmentFactor()); //because d>24", #ccc=21 and conductors don't have
        // exceptions
        assertEquals(0.60, cable.getAdjustmentFactor()); //because d>24" and the exception is NOT satisfied (#ccc>20)
        //but the exception 310_15_B_3_a_5 is!
        assertFalse(bundle.complyWith310_15_B_3_a_4());
        bundle.remove(conductor);
        assertTrue(bundle.complyWith310_15_B_3_a_4());


        //case 8
        bundle.setBundlingLength(24);
        bundle.add(conductor.clone());
        conductor.setSize(Size.AWG_8);
        conductor.setMetal(Metal.ALUMINUM);
        //at this point there are 21 ccc, one is #8AWG AL but because d<=24 no adjustment factor is applied,
        //making the bundle to behave as a free air
        assertEquals(1.0, conductor.getAdjustmentFactor()); //because d<=24"
        assertEquals(1.0, cable.getAdjustmentFactor()); //because d<=24"
//        Tools.println("Conductor AdjustmentFactor: " + conductor.getAdjustmentFactor());
//        Tools.println("    Cable AdjustmentFactor: " + cable.getAdjustmentFactor());
    }

    @Test
    void complyWith310_15_B_3_a_5() {
        Conductor conductor = new Conductor();
        Cable cable = new Cable();
        cable.setSystem(VoltageSystemAC.v480_3ph_4w);//3ccc
        bundle.setBundlingLength(25);
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(cable);
        bundle.add(cable.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        assertEquals(21, bundle.getCurrentCarryingCount());
        assertEquals(25, bundle.getBundlingLength());

        //case 1
        assertTrue(bundle.complyWith310_15_B_3_a_5());
        assertEquals(0.45, conductor.getAdjustmentFactor());
        assertEquals(0.60, cable.getAdjustmentFactor());

        //case 2
        bundle.remove(conductor);
        assertFalse(bundle.complyWith310_15_B_3_a_5());
        bundle.add(conductor);
        assertTrue(bundle.complyWith310_15_B_3_a_5());

        //case 3
        cable.setJacketed(true);
        assertFalse(bundle.complyWith310_15_B_3_a_5());
        cable.setJacketed(false);
        assertTrue(bundle.complyWith310_15_B_3_a_5());

        //case 4
        cable.setType(CableType.NM);
        assertFalse(bundle.complyWith310_15_B_3_a_5());
        assertEquals(0.45, conductor.getAdjustmentFactor());
        assertEquals(0.45, cable.getAdjustmentFactor());
        cable.setType(CableType.MC);
        assertTrue(bundle.complyWith310_15_B_3_a_5());

        //case 5
        bundle.setBundlingLength(20);
        assertFalse(bundle.complyWith310_15_B_3_a_5());
        assertEquals(1.0, conductor.getAdjustmentFactor());
        assertEquals(1.0, cable.getAdjustmentFactor());
        bundle.setBundlingLength(25);
        assertTrue(bundle.complyWith310_15_B_3_a_5());
    }

}