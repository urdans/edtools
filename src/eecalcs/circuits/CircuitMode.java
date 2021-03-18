package eecalcs.circuits;
/**Enums to represent the 5 ways a circuit can be set up:
 <ol>
 <li>PRIVATE_CONDUIT
 <p>The circuit object creates and uses its own conduit. The circuit can
 increment or decrement the number of conduits if asked for and if the number
 of sets is more than 1.</p></li>
 <li>FREE_AIR
 <p>The circuit is in free air. There is no bundle or conduit associated with
 the circuit. No adjustment factor are applied.</p></li>
 <li>SHARED_CONDUIT
 <p>The circuit uses an external conduit that might also contain other
 conductors or cables. That conduit is shared between two or more circuits.
 </p></li>
 <li>PRIVATE_BUNDLE
 <p>The circuit does not use a conduit but its conductors/cables are bundled
 together.</p></li>
 <li>SHARED_BUNDLE
 <p>The circuit is part of an external bundle that might also contain other
 circuits (conductors or cables). That bundle is shared between two or more
 circuits</p></li>
 </ol>
 */
public enum CircuitMode {
    PRIVATE_CONDUIT, FREE_AIR, SHARED_CONDUIT, PRIVATE_BUNDLE, SHARED_BUNDLE
}
