package eecalcs.conductors;

public interface RWConduitable {
    /**
     Sets the ambient temperature of this RWConduitable.
     @param ambientTemperatureF The ambient temperature in degrees Fahrenheits. It must be in the
     [{@link Factors#MIN_TEMP_F},{@link Factors#MAX_TEMP_F}], otherwise, an IllegalArgumentException is thrown.
     */
    RWConduitable setAmbientTemperatureF(int ambientTemperatureF);
}
