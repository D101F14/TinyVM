package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Operator;

public class IfInstruction extends Instruction {

	Operator operator;
	byte address1;
	byte address2;
	
	public IfInstruction() {
		super(OpCode.IF);
	}
	
	@Override
	public void read(InputStream stream) {
		try {
			operator = Operator.get((byte) stream.read());
			address1 = (byte) stream.read();
			address2 = (byte) stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public int getAddress() {
		return address1 << 8 | address2;
	}

}
