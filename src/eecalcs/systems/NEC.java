package eecalcs.systems;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;

@Documented
@Repeatable(NECYears.class)
public @interface NEC {
	String year();// default "2014";
}
