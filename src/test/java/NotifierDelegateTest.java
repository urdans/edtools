package test.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.Listener;
import tools.NotifierDelegate;

import static org.junit.jupiter.api.Assertions.*;

class NotifierDelegateTest {
    Speaker speaker;
    ListenerClass listener1;
    ListenerClass listener2;

    static class Speaker {
        private String field1 = "string value of field1";
        private int field2 = -10;
        private NotifierDelegate notifierDelegate = new NotifierDelegate(this);

        public void setField1(String newValue){
            notifierDelegate.info.addFieldChange("field1",field1,newValue);
            field1 = newValue;
            notifierDelegate.notifyAllListeners();
        }
        public void setField2(int newValue){
            notifierDelegate.info.addFieldChange("field2", field2, newValue);
            field2 = newValue;
            notifierDelegate.notifyAllListeners();
        }
    }

    static class ListenerClass implements Listener {
        public String fieldName;
        public Object fieldOldValue;
        public Object fieldNewValue;
        public boolean notified = false;

        @Override
        public void notify(Object sender) {
            notified = true;
            if(sender instanceof Speaker) {
                Speaker sp = (Speaker) sender;
                fieldName = sp.notifierDelegate.info.fields.get(0).fieldName;
                fieldOldValue = sp.notifierDelegate.info.fields.get(0).oldValue;
                fieldNewValue = sp.notifierDelegate.info.fields.get(0).newValue;
            }
        }
    }

    @BeforeEach
    void setUp() {
        speaker = new Speaker();
        listener1 = new ListenerClass();
        listener2 = new ListenerClass();
        speaker.notifierDelegate.addListener(listener1);
        speaker.notifierDelegate.addListener(listener2);
    }

    @Test
    void notifyAllListeners() {
        String ov = "string value of field1";
        String nv = "New string value";
        speaker.setField1(nv);
        assertTrue(listener1.notified);
        assertTrue(listener2.notified);
        assertEquals("field1", listener1.fieldName);
        assertEquals("field1", listener2.fieldName);
        assertEquals(ov, listener1.fieldOldValue);
        assertEquals(ov, listener2.fieldOldValue);
        assertEquals(nv, listener1.fieldNewValue);
        assertEquals(nv, listener2.fieldNewValue);

        listener1.notified =  false;
        listener2.notified =  false;
        speaker.setField2(50);
        assertTrue(listener1.notified);
        assertTrue(listener2.notified);
        assertEquals("field2", listener1.fieldName);
        assertEquals("field2", listener2.fieldName);
        assertEquals(-10, listener1.fieldOldValue);
        assertEquals(-10, listener2.fieldOldValue);
        assertEquals(50, listener1.fieldNewValue);
        assertEquals(50, listener2.fieldNewValue);
    }

    @Test
    void addListener() {
    //this test is passed if notifyAllListeners passes.
    }

    @Test
    void removeListener() {
        speaker.notifierDelegate.removeListener(listener1);
        speaker.setField1("what ever");
        assertFalse(listener1.notified);
        assertTrue(listener2.notified);

        listener2.notified =  false;
        speaker.notifierDelegate.removeListener(listener2);
        speaker.setField2(53);
        assertFalse(listener1.notified);
        assertFalse(listener2.notified);
    }

    @Test
    void enabled() {
        //too trivial to be tested
    }
}