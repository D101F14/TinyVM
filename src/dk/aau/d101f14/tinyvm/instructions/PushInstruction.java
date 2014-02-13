package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Type;

public class PushInstruction extends Instruction {

	Type type;
	byte value;
	byte value2;
	
	public PushInstruction() {
		super(OpCode.PUSH);
	}
	
	@Override
	public void read(InputStream stream) {
		try {
			type = Type.get((byte) stream.read());
			value = (byte) stream.read();
			if(type == Type.INT) {
				value2 = (byte) stream.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Type getType() {
		return type;
	}
	
	public int getValue() {
		if(type != Type.INT) {
			return value;
		} else {
			return value << 8 | value2;
		}
	}

}
