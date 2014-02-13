package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;

public class PopInstruction extends Instruction {

	byte number;
	
	public PopInstruction() {
		super(OpCode.POP);
	}
	@Override
	public void read(InputStream stream) {
		try {
			number = (byte) stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte getNumber() {
		return number;
	}

}
