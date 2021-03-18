package eecalcs.loads;

/**
 This class represents a nonlinear load.
 A non-linear load is rich in harmonics; triple harmonics (especially 3rd) do
 not cancel each other on the neutral node, but instead, they are in phase
 and hence add each other and increase the current in the neutral conductors,
 producing heat in the conductors, vibrations and noise in the motors and
 heat and noise in transformers. Harmonics are not good for the electrical
 systems and its effects need to be mitigated.
 <p>As explained by the NEC, the following loads may be nonlinear and should be
 treated as such:
 <ul>
 <li>Electronic equipment like computers, servers, printing equipment, etc.</li>
 <li>Electronic/electric discharge lighting (fluorescent and HID lights).</li>
 <li>Variable frequency drivers (VFDs).</li>
 <li>Solid-state phase-control dimmers (Not to be confused with Solid-State Sine
 Wave Dimmer which are linear loads).</li>
 </ul>

 <p><b>General NEC requirements for nonlinear loads:</b>

 <p>-On a 4-wire, 3-phase wye circuit where the major portion of the load
 consists of nonlinear loads, harmonic currents are present in the neutral
 conductor; the neutral conductor shall therefore be considered a
 current-carrying conductor. (NEC-310.15(B)(5)(c))
 <p>- Reduction of the neutral conductor is prohibited for feeder circuits
 feeding nonlinear Loads (NEC-220.61).
 <br><br>
 <p>A good engineering practice is to size the neutral conductor the same size
 of the phase conductors. However, for the particular case of big facilities
 with computer loads, it is common to increase the neutral conductor size at
 150% the size of the phase conductors, to handle the harmonics.
 <p>The NEC permits the reduction of the neutral conductor for all linear
 loads (for multiwire circuits) and does not establish a neutral conductor size
 calculation method for nonlinear loads. Its wording is just to consider the
 neutral conductor as a current-carrying.<br><br>
 <p><b>Recommendations from international standards</b></p>
 <p>The neutral current can be calculated based on the % of the 3rd harmonic
 in the line current (the hot or phase current), and  on table provided by the
 standard IEC 60364-5-52.
 <p>The user must provide how much is the 3rd harmonic content of the phase
 current in percentages. This should be provided by the load nameplate or
 specification manual. Values are grouped as following:
 <p>0-15%, 15-33%, 33-45% and >45%.<br><br>
 <p>Then, if this percentage does not exceed 15%, the neutral current is
 assumed to be the same as for the lines.<br><br>
 <p>For more than 15% and up to 33%, the neutral current can be greater than the
 line current in a factor that is equal to:
 <p>2*√3/3 = 1.1547.<br><br>
 <p>For more than 33% and up to 45%, the neutral current is calculated as
 follow:
 <p>Ihot*3*Percentage*1.1547<br><br>
 <p>Example:
 <p>The percentage of 3rd harmonic is 40% of the load current of 37 amperes.
 Ineutral=37 * 0.4 * 3 * 1.1547 = 51.3 Amperes.
 The equation can be reduced to Ihot*percentage*2*√3 = 37*0.4*2*√3 = 51.3 Amperes.

 Finally, for more than 45% (and a maximum value to be trimmed to 57.7%):
 Ineutral = Ihot*3*Percentage
 Example:
 Il=37, percentage = 50%
 Ineutral = 37*0.5*3 = 55.5 amperes. that is 55.5/37=150%
 Percentage = 57.7%
 Ineutral=37*0.577*3=64.05amperes, that is 64.05/37=173%

 So, the value of the neutral current for this type of load will be
 calculated based on these steps using as an input the percentage of 3rd
 harmonic present in the line current.
 This method is valid for single (non-composite) load fed from a 3φ-4W system.
 So there must be a method to assign these percentages, so the neutral
 current can be calculated.

 For combined loads (single phase and three phase) the method must be
 different since the percentage of triple harmonics is unpredictable. 120
 volt single loads can inject a lot of other order harmonics that are in
 phase and then do not cancel in the neutral but instead increase the neutral
 current. (Run simulation demonstrating this)
 ***************
 The solution is provide a method that accepts one parameter. In that method,
 the parameter is stored or used later to determine if the load is linear and
 even to calculate the neutral current. Examples of this method name would be
 setNonlinearPercentage() or setTHD() or whatever is the best in accordance
 with the proper method. I need to investigate more about this, so for now, I
 will just a method setLinear(boolean flag) to the abstract load base class.

 But, how the user determine this value? Is it provided by the load tag?
 Todo learn about THD interpretation.


 */
public class NonLinearLoad {
}
