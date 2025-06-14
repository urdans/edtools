package eecalcs.systems;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;

/**
 Interface used to annotate function as full compliant with the indicated year edition of the NEC.
 For example, the following function:<br><br>
 <code>@NEC(year="2014")<br>
 <code>@NEC(year="2017")<br>
 <code>@NEC(year="2020")<br>
 <code>public boolean isRoofTopCondition() {...}
 <br>
 <p>is marked to indicate that it complies with these 3 editions of the NEC.
 */
@Documented
@Repeatable(NECYears.class)
public @interface NEC {
	String year();// default "2014";
}
