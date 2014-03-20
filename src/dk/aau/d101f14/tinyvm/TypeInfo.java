package dk.aau.d101f14.tinyvm;

public class TypeInfo extends CPInfo {
	byte type;
	int className;
	
	public TypeInfo(byte type, int className) {
		super((byte)5);
		this.type = type;
		this.className = className;
	}
	
	public byte getType() {
		return type;
	}
	
	public int getClassName() {
		return className;
	}
	
	public TypeInfo(byte type) {
		this(type, -1);
	}
}
