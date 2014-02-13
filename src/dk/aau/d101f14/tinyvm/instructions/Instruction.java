package dk.aau.d101f14.tinyvm.instructions;

import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;

public abstract class Instruction {
	OpCode opcode;
	
	public Instruction(OpCode opcode) {
		this.opcode = opcode;
	}
	
	public abstract void read(InputStream stream);
	
	public OpCode getOpCode() {
		return opcode;
	}
}
