package dk.aau.d101f14.tinyvm.instructions;

import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;

public abstract class Instruction {
	TinyVM tinyVm;
	OpCode opcode;

	
	public Instruction(TinyVM tinyVm, OpCode opcode) {
		this.tinyVm = tinyVm;
		this.opcode = opcode;
	}
	
	public abstract void read(InputStream stream);
	
	public abstract void execute();
	
	public OpCode getOpCode() {
		return opcode;
	}
}
