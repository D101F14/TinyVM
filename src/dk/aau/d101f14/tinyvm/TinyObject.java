package dk.aau.d101f14.tinyvm;

import java.util.HashMap;

public class TinyObject {
	TinyClass tinyClass;
	HashMap<String, Integer> fields;
	
	public TinyClass getTinyClass(){
		return tinyClass;
	}
	
	public HashMap<String, Integer> getFields() {
		return fields;
	}
	
	public TinyObject(TinyClass tinyClass) {
		this.tinyClass = tinyClass;
		fields = new HashMap<String, Integer>();
		addFields(this.tinyClass);
	}
	
	public void addFields(TinyClass tinyClass) {
		for(String fieldName : tinyClass.getFields()) {
			fields.put(fieldName, new Integer(0));
		}
		if(tinyClass.getSuperClass() != null) {
			addFields(tinyClass.getSuperClass());
		}
	}
}
