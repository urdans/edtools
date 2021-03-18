package tools;

import java.util.ArrayList;
import java.util.List;

/**
 This class represents a delegate whose role is forecasting notifications about
 changes field values in its owner object, to the registered listeners.
 All objects using a delegate should declare it private and provide a common
 name for the getter as<br><br>
 <code>public NotifierDelegate getNotifier()</code>
 @see Listener
 */
public class NotifierDelegate {
    private final List<Listener> listeners = new ArrayList<>();
    private final Object sender;

    private boolean enable = true;
    /**
     See {@link FieldInfoChangeEvent} for details.
     */
    public FieldInfoChangeEvent info =  new FieldInfoChangeEvent();

    /**
     Creates a new NotifierDelegate object.
     @param sender Is the object that needs to forecast a field change event and
     it is also the owner of this NotifierDelegate object.
     */
    public NotifierDelegate(Object sender){
        this.sender = sender;
    }

    /**
     Sends the FieldInfoChangeEvent message to all registered listeners if this
     NotifierDelegate is enable to do so. Refer to {@link #enable(boolean)}
     and {@link Listener} for details.
     */
    public void notifyAllListeners() {
        if(enable)
            for(Listener listener: listeners)
                listener.notify(sender);
            info.clearFields();
    }

    /**
     Adds a Listener object to this object list of listeners. Every listener in
     this list will be notified when the owner of this NotifierDelegate object
     calls the {@link #notifyAllListeners()} method.
     @param listener the Listener object.
     */
    public void addListener(Listener listener) {
        if(listeners.contains(listener))
            return;
        listeners.add(listener);
    }

    /**
     Removes the given Listener object from this object list of listeners.
     @param listener The listener object to be removed.
     */
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /**
     Enable/disable the forecasting/notification of messages to the Listener
     objects registered with this NotifierDelegate object.
     @param flag If true, message forecasting is enable. If false, no message
     will be sent to any of the registered listeners.
     */
    public void enable(boolean flag){
        enable = flag;
    }

    /**
     @return the state of this notifier.
     @see #enable(boolean)
     */
    public boolean isEnable() {
        return enable;
    }
}
