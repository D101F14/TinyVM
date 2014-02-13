package dk.aau.d101f14.tinyvm.instructions;

import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;

public class NopInstruction extends Instruction {
	public NopInstruction() {
		super(OpCode.NOP);
	}

	@Override
	public void read(InputStream stream) {
		// No operation!
	}
}
