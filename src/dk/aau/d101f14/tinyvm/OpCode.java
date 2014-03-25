package dk.aau.d101f14.tinyvm;

import java.util.HashMap;
import java.util.Map;

public enum OpCode {
	NOP((byte)0),
	PUSH((byte)1),
	POP((byte)2),
	LOAD((byte)3),
	STORE((byte)4),
	GOTO((byte)5),
	IF((byte)6),
	COMP((byte)7),
	NEW((byte)8),
	GETFIELD((byte)9),
	PUTFIELD((byte)10),
	INVOKEVIRTUAL((byte)11),
	RETURN((byte)12),
	DUP((byte)13),
	THROW((byte)14);
	
	private final byte opcode;
	OpCode(byte opcode) {
		this.opcode = opcode;
	}
	
	public byte getByte() {
		return opcode;
	}
	
	private static final Map<Byte, OpCode> lookup = new HashMap<Byte, OpCode>();
	  
	static {
		for(OpCode opcode : OpCode.values()) {
			lookup.put(opcode.getByte(), opcode);
		}
	}
	
	public static OpCode get(byte address) { 
		return lookup.get(address); 
	}
}
