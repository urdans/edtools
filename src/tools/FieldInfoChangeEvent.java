package tools;

import java.util.ArrayList;
import java.util.List;

/**
 This class represents the information about changing in field values. It
 contains the name of the field that changed, its old and new values. */
public class FieldInfoChangeEvent {
	public static class Fields {
		public String fieldName;
		public Object oldValue;
		public Object newValue;

		public Fields(String fieldName, Object oldValue, Object newValue) {
			this.fieldName = fieldName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}

	public List<Fields> fields = new ArrayList<>();

	/**
	 Sets all the data at once, about the field whose value changed.
	 @param fieldName The name of the field.
	 @param oldValue The previous value of the field before it changes.
	 @param newValue The new value of the field.
	 */
	public void addFieldChange(String fieldName, Object oldValue,
                               Object newValue) {
		fields.add(new Fields(fieldName, oldValue, newValue));
	}

	/**
	 Clear the data stored in this class (field name, old and new values).
	 */
	public void clearFields() {
		fields.clear();
	}
}
