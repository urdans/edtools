package eecalcs.conductors;

import eecalcs.bundle.Bundle;
import eecalcs.conduits.Conduit;
import org.jetbrains.annotations.Nullable;

public interface RWConduitable {
    /**
     Sets the ambient temperature of this RWConduitable. If this RWConduitable is inside a conduit or is part of a
     bundle, an IllegalArgumentException is thrown, as this parameter must be set by the conduit or bundle object
     that owns this RWConduitable. Otherwise, the ambient temperature is set for this object.

     @param ambientTemperatureF The ambient temperature in degrees Fahrenheits. It must be in the
     [{@link Factors#MIN_TEMP_F},{@link Factors#MAX_TEMP_F}].
     */
    void setAmbientTemperatureF2(int ambientTemperatureF);

    /**
     Sets the conduit for this RWConduitable. This method can only be called from the Conduit object that owns
     this RWConduitable; a call from other objects will throw an IllegalCallerException. Once this RWConduitable
     is set in a conduit, it cannot be removed.
     @param conduit The conduit this RWConduitable will be inserted into.
     */
    void setConduit2(@Nullable Conduit conduit);

    /**
     Sets the bundle for this RWConduitable. This method can only be called from the Bundle object that owns
     this RWConduitable; a call from other objects will throw an IllegalCallerException. Once this RWConduitable is set
     in a bundle, it cannot be removed.
     @param bundle The bundle this RWConduitable will be part of.
     */
    void setBundle2(@Nullable Bundle bundle);
}
