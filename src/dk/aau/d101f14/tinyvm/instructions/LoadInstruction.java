package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Type;

public class LoadInstruction extends Instruction {

	Type type;
	byte address1;
	byte address2;
	
	public LoadInstruction() {
		super(OpCode.LOAD);
	}

	@Override
	public void read(InputStream stream) {
		try {
			type = Type.get((byte) stream.read());
			address1 = (byte) stream.read();
			address2 = (byte) stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Type getType() {
		return type;
	}
	
	public int getAddress() {
		return address1 << 8 | address2;
	}

}
