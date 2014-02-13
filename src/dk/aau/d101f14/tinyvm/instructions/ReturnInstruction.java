package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Type;

public class ReturnInstruction extends Instruction {

	Type type;
	
	public ReturnInstruction() {
		super(OpCode.RETURN);
	}
	
	@Override
	public void read(InputStream stream) {
		try {
			type = Type.get((byte) stream.read());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Type getType() {
		return type;
	}

}
