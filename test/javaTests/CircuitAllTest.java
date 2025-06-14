package javaTests;

import eecalcs.circuits.CircuitAll;
import eecalcs.conductors.*;
import eecalcs.conduits.TradeSize;
import eecalcs.loads.GeneralLoad;
import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircuitAllTest {

    /*This test is to find out if the all circuit methods are congruent, that
     is, there is consistency between all these methods' returned values*/
    @Test
    void congruencyTest01() {
        GeneralLoad generalLoad = new GeneralLoad();
        CircuitAll circuitAll = new CircuitAll.Builder(generalLoad).build();
        assertEquals(10, circuitAll.getLoad().getNominalCurrent());
        /*The voltage drop is governing and the proper size is #10.*/
        assertEquals(Size.AWG_12, circuitAll.getPhaseConductor().getSize());
    }

    @Test
    void congruencyTest02() {
        GeneralLoad generalLoad = new GeneralLoad(VoltageAC.v120_1ph_2w, 100);
        CircuitAll circuitAll = new CircuitAll.Builder(generalLoad).build();
        //the size of the phase conductor must correspond to the current
        assertEquals(Size.AWG_1, circuitAll.getPhaseConductor().getSize(), circuitAll.toString());
        //the same applies for the neutral conductor
        assertEquals(Size.AWG_1, circuitAll.getNeutralConductor().getSize());
    }

    @Test
    void congruencyTest03() {
        GeneralLoad generalLoad = new GeneralLoad(VoltageAC.v208_3ph_3w, 100);
        CircuitAll circuitAll = new CircuitAll.Builder(generalLoad).build();

        /*there must not be a neutral conductor.*/
        assertNull(circuitAll.getNeutralConductor());
        //the size of the phase must still be the same.
        assertEquals(Size.AWG_1, circuitAll.getPhaseConductor().getSize());
        //OCPD
        assertEquals(110, circuitAll.getOCPDRating());
        //grounding
        assertEquals(Size.AWG_6, circuitAll.getGroundingConductor().getSize());
        //conduit
        assertEquals(TradeSize.T1_1$2, circuitAll.getPrivateConduit().getTradeSize());





//        quedé aquí
/*To solve this incongruity, the class circuitAll must be refactored as follows:
1. Once the class is created, any change to its primitive or simple members
through setters must change the state of the class, including the state of
all its object (non primitive) members.

The following are the primitives to watch:
    -setBundleMode()                                                   -->Tested
    -setBundleMode(Bundle bundle)                                      -->Tested
    -setCircuitAmbientTemperatureF(int temperature)        --> to be more tested
    -setCircuitLength(double length)                       --> to be more tested
    -setConduitMode()                                                  -->Tested
    -setConduitMode(Conduit conduit)                                   -->Tested
    -setFreeAirMode()                                                  -->Tested
    -setNumberOfSets(int numberOfSets)                                 -->Tested
    -setTerminationTempRating(TempRating terminationTempRating)        -->Tested
    -setUsingCable(boolean usingCable)                                 -->Tested

2. The circuitAll object members that are exposed through getters must be
observed, so if they change state, the circuitAll must also change state, to
avoid loosing synchrony.

One way to avoid it is to provide a read-only interface to the exposed
objects. Another, is to fully observe the changes to those objects. I chose to expose through read-only interfaces.

The following are the getters that expose such objects:
*   -ROCable getCable()                                   -->To hide more
*       ->setAmbientTemperatureF(int ambientTemperatureF)-->hide&move to CircuitAll
*    ->use setCircuitAmbientTemperatureF and rename it to setAmbientTemperatureF
*       ->setInsulation(Insulation insul)                     -->hide&move to CircuitAll
*       ->setLength(double length)                       -->hide&move to CircuitAll
*            ->use setCircuitLength and rename it to setLength
*       ->setMetalForPhaseAndNeutral(ConductiveMaterial metal)                          -->hide&move to CircuitAll
*       ->setPhaseConductorSize(Size size)     ->WEIRD->### what is this for?###
*            (why do I need to manually set the size of the circuitAll conductors?)
*             Try not to use it.
*   -ROConduitable getConduitable()                     -->full read-only
*    -RoConductor getGroundingConductor()                  -->To hide more
*       ->Conductor setSize(Size size)         ->WEIRD->### what is this for?###
*            (why do I need to manually set the size of the circuitAll conductors?)
*            Try not to use it.
*       ->Conductor setMetalForPhaseAndNeutral(ConductiveMaterial metal)                -->hide&move to CircuitAll
*       ->setInsulation(Insulation insulation)                -->hide&move to CircuitAll
*       ->setLength(double length)                       -->hide&move to CircuitAll
*       ->setAmbientTemperatureF(int ambientTemperatureF)-->hide&move to CircuitAll
*   -Load getLoad()                                      -->to be fully observed
*       ->setDescription(String description)      ->make it notify all listeners
*   -OCPD getOcdp()                               ->make it notify all listeners
*   -RoConductor getNeutralConductor()                  -->same as before
*   -RoConductor getPhaseConductor()                    -->same as before
*   -ROBundle getPrivateBundle()                          -->To hide more
*       ->setDistance(double distance)         ->WEIRD->### what is this for?###
*               (refactor name to setPrivateBundleLength, hide&move to circuitAll?)
*   -ROConduit getPrivateConduit()                        -->To hide more
*       ->setMinimumTrade(TradeSize minimumTrade)            -->hide&move to CircuitAll
*       ->setType(Type type)                             -->hide&move to CircuitAll
*       ->setNipple(Conduit.Nipple nipple)               -->hide&move to CircuitAll
*       ->setRoofTopDistance(double roofTopDistance)     -->hide&move to CircuitAll
*       ->resetRoofTop()                                 -->hide&move to CircuitAll
*   -ROBundle getSharedBundle()                         -->same as before
*   -ROConduit getSharedConduit()                       -->same as before
*   -ROVoltDrop getVoltageDrop()                          -->To hide more
*       ->setMaxVoltageDropPercent(double)               -->hide&move to CircuitAll

** Hiding and moving to CircuitAll is less work to do, less complex.
** Notifying al listeners require more lines of code but the advantage is
that it does not add more complexity (methods) to the circuitAll class and does
not require to create a read-only interface.

Internal objects belonging to the CircuitAll class that are exposed:
1. The RO cable object that represents all cables.
2. The RO conductor object that represents all the phase conductors.
3. The RO conductor object that represents all the neutral conductors.
4. The RO conductor object that represents all the ground conductors.
5. The RO conduitable object that represents all cables or all conductors.
6. The OCPD object.
7. The private RO bundle, if used.
8. The private RO conduit, if used.
9. The shared RO bundle, if used.
10.The shared RO conduit, is used.
11.The RO voltageDrop object used for internal calculations.

The calculation of the circuitAll consists of:
a.setupModelConductors:
  -creates the objects listed in 1~3.
  -calls prepareConduitableList()
  -Depends on:
    -voltage system of the load
    -if using cable or conductors
  ==>call this if:
  *-the voltage system of the load changes (change is only possible through the
    load object, which is now fully observed)
  *-the type of wiring changes, through setUsingCable. Implemented.

b.prepareConduitableList:
  -creates the private conduitable object list that will make up the circuitAll.
  -calls setMode()
  -depends on:
    -conductorsPerSet (changes only in SetupModelConductors)
    -setsPerConduit
  ==>call this if:
  -don't call it directly, it will be called from SetupModelConductors(),
   morePrivateConduits(), lessPrivateConduits() and setNumberOfSets() (these last 3 change
   the setsPerConduit state)

c.setMode()
  -set up the circuitAll for the corresponding mode.
  -calls setFreeAirMode(), setConduitMode(), setConduitMode(sharedConduit),
   setBundleMode(), setBundleMode(sharedBundle)
  -calls calculateCircuit().
  -depends on:
    -the circuitAll mode.
  ==>call this if:
  -don't call it.

Implementations to be done:
-Create a method calculateCircuit() that: (or the existing getCircuitSize)
    .Calculates the biggest hot wire size per both ampacity and voltage drop;
    .Calculates the neutral conductor, if used.
    .Calculates the rating of the OCPD.
    .Calculates the size of the EGC
    .Updates all the conductors of the circuitAll to this wire size.
    .Calculates the size of the conduit, if used.
    .At this point if no error has been detected, a circuitChanged flag is
    cleared.

This method should be private and called only when a circuitStateChanged flag
is true. All the methods or objects that change the state of the circuitAll must
set this flag as an indication that the circuitAll needs to be recalculated.
When the class is created, just after all setup is achieved calculatedCircuit
must be called.

ALl the parameters of the circuitAll are obtained through the read only objects.

*/
    }
}
