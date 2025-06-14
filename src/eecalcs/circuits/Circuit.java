package eecalcs.circuits;

/**
<h1>Interface for all circuit classes.</h1>
The main purpose of a Circuit object is to calculate:<br>
1. The minimum conductor size for:<br>
 - Phases<br>
 - Neutral (if present)<br>
 - EGC<br>
   This would be done based on voltage drop and ampacity.<br>
2. The maximum OCPD.<br>
   To protect the conductors and the load, or the conductors only (when the load is protected by a local fuse).<br>
3. The minimum conduit trade size.<br>
   Applies only if the circuit uses private conduit(s) or a shared conduit.<br>
4. The minimum circuit ampacity.<br><br>
5. etc.<br>

 A circuit can have different configurations based on how the conductors are arranged:<br><br>

 <b>PRIVATE_CONDUIT:</b><br>
 This is the most common situation. The circuit uses its own conduit, that is, the conduit is not shared with any
 other circuit. If the circuit uses multiples sets of conductors in parallel there will be one conduit for each set
 of conductors; however, the user could chose to increase or reduce the number of conduits, or increase or reduce
 the number of sets in the circuit.<br>
 By default, a circuit has one set of conductors in a single conduit. But, it can also have more than one set, either
 because the user wants it, or because the ampacity is too high for a single set.<br>

 All the conductors of a set can have their own conduit, that is, the number of sets equals the number of conduits
 (this is the default and most common situation), or they can fit in a single conduit (less common), unless the
 combined area of all the conductors is to big for the biggest conduit.<br>

 A circuit can use conductors or cables, but not a combination of both.<br>

 <b>SHARED_CONDUIT</b>
 <b>PRIVATE_BUNDLE</b>
 <b>SHARED_BUNDLE</b>
 <b>FREE_AIR</b>
 */
public interface Circuit {

}
