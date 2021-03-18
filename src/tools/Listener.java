package tools;

/**
 <p>An instance (an object) of a class implementing this interface will be
 notified about changes in other object's fields. For this notification to
 happen, the implementing instance has to be registered as a listener with the
 other objects using their NotifierDelegate objects.
 <p>For example, class A implements this interface and object_A is an instance
 of that class. object_A is now able to listen to other forecaster objects when
 it registers itself with those forecaster objects.
 <p>Let's assume that object_B is a forecaster, that is, it has a public
 NotifierDelegate object field member, which was created as:
 <br><br>
 <code>private NotifierDelegate delegate = new NotifierDelegate(this);</code>
 <br><br>
 If object_A wants to listen to changes in object_B, it has to register itself
 with the object_B's notifier delegate this way:<br><br>
 <code>object_B.delegate.addListener(this)</code> or<br><br>
 <code>object_B.delegate.addListener(object_A)</code><br><br>
 Now, whenever a change in object_B occurs, object_A will be notified.
 <p>Notice that forecasting from object_B can be enable/disabled from inside
 object_B but also from outside if object_B declares its delegate as public or
 provides a public getter for it.
 */
@FunctionalInterface
public interface Listener {
    void notify(Object sender);
}
