package dk.aau.d101f14.tinyvm;

public class FieldDescriptorInfo extends CPInfo {
	int className;
	int fieldName;
	int fieldType;
	
	public FieldDescriptorInfo(int className, int fieldName, int fieldType) {
		super((byte)2);
		this.className = className;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}
	
	public int getClassName() {
		return className;
	}
	
	public int getFieldName() {
		return fieldName;
	}
	
	public int getFieldType() {
		return fieldType;
	}
}
