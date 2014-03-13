package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;
import dk.aau.d101f14.tinyvm.Type;

public class PushInstruction extends Instruction {

	Type type;
	byte value;
	byte value2;
	
	public PushInstruction(TinyVM tinyVM) {
		super(tinyVM, OpCode.PUSH);
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

	@Override
	public void execute() {
		//Push value to operand stack
		tinyVm.getCurrentFrame().getOperandStack().push(getValue());
				
		//Increment code pointer
		tinyVm.getCurrentFrame().setCodePointer(tinyVm.getCurrentFrame().getCodePointer() + 1);
		
		if(tinyVm.getDebug()) {
			System.out.println("PUSH\t" + getType() + "\t" + getValue());
		}
	}

}
