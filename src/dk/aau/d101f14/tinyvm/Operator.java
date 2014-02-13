package dk.aau.d101f14.tinyvm;

import java.util.HashMap;
import java.util.Map;

public enum Operator {
	EQUALS((byte)0),
	NEQUALS((byte)1),
	GT((byte)2),
	LS((byte)3),
	NEG((byte)4),
	ADD((byte)5),
	SUB((byte)6),
	MUL((byte)7),
	DIV((byte)8),
	MOD((byte)9),
	SHL((byte)10),
	SHR((byte)11),
	USHL((byte)12),
	USHR((byte)13),
	AND((byte)14),
	OR((byte)15),
	XOR((byte)16);
	
	private final byte operator;
	Operator(byte operator) {
		this.operator = operator;
	}
	
	public byte getOperator() {
		return operator;
	}
	
	private static final Map<Byte, Operator> lookup = new HashMap<Byte, Operator>();
	  
	static {
		for(Operator operator : Operator.values()) {
			lookup.put(operator.getOperator(), operator);
		}
	}
	
	public static Operator get(byte address) { 
		return lookup.get(address); 
	}
}
