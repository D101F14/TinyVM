package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Operator;

public class CompInstruction extends Instruction {

	Operator operator;
	
	public CompInstruction() {
		super(OpCode.COMP);
	}
	
	@Override
	public void read(InputStream stream) {
		try {
			operator = Operator.get((byte) stream.read());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Operator getOperator() {
		return operator;
	}
}
