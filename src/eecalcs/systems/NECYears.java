package eecalcs.systems;

import java.lang.annotation.Documented;


/**
 Defines an array of string to hold the parameter accompanying the annotation {@link NEC}
 */
@Documented
public @interface NECYears {
	NEC[] value();
}
