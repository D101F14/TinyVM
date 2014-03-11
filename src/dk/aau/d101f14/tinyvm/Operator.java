package dk.aau.d101f14.tinyvm;

import java.util.HashMap;
import java.util.Map;

public enum Operator {
	EQUALS((byte)0),
	NEQUALS((byte)1),
	GT((byte)2),
	LT((byte)3),
	NEG((byte)4),
	ADD((byte)5),
	SUB((byte)6),
	MUL((byte)7),
	DIV((byte)8),
	MOD((byte)9),
	SHL((byte)10),
	SHR((byte)11),
	USHR((byte)12),
	AND((byte)13),
	OR((byte)14),
	XOR((byte)15);
	
	private final byte operator;
	Operator(byte operator) {
		this.operator = operator;
	}
	
	public byte getByte() {
		return operator;
	}
	
	private static final Map<Byte, Operator> lookup = new HashMap<Byte, Operator>();
	  
	static {
		for(Operator operator : Operator.values()) {
			lookup.put(operator.getByte(), operator);
		}
	}
	
	public static Operator get(byte address) { 
		return lookup.get(address); 
	}
}
