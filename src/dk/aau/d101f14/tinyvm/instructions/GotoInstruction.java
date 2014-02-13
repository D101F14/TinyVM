package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;

public class GotoInstruction extends Instruction {

	byte address1;
	byte address2;
	
	public GotoInstruction() {
		super(OpCode.GOTO);
	}
	
	@Override
	public void read(InputStream stream) {
		try {
			address1 = (byte) stream.read();
			address2 = (byte) stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getAddress() {
		return address1 << 8 | address2;
	}
}
