package dk.aau.d101f14.tinyvm;

import java.util.HashMap;
import java.util.Map;

public enum Type {
	VOID((byte)0),
	BOOLEAN((byte)1),
	BYTE((byte)2),
	INT((byte)3);
	
	private final byte type;
	Type(byte type) {
		this.type = type;
	}
	
	public byte getType() {
		return type;
	}
	
	private static final Map<Byte, Type> lookup = new HashMap<Byte, Type>();
	  
	static {
		for(Type type : Type.values()) {
			lookup.put(type.getType(), type);
		}
	}
	
	public static Type get(byte address) { 
		return lookup.get(address); 
	}
}
