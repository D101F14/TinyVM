package dk.aau.d101f14.tinyvm.instructions;

import java.io.InputStream;

public class NopInstruction extends Instruction {
	public NopInstruction() {
		super(OpCode.NOP.getOpCode());
	}

	@Override
	public void read(InputStream stream) {
		// No operation!
	}
}
