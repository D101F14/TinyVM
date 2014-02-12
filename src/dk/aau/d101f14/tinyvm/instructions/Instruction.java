package dk.aau.d101f14.tinyvm.instructions;

import java.io.InputStream;

public abstract class Instruction {
	byte opcode;
	
	public Instruction(byte opcode) {
		this.opcode = opcode;
	}
	
	public abstract void read(InputStream stream);
	
	public byte getOpCode() {
		return opcode;
	}
}
