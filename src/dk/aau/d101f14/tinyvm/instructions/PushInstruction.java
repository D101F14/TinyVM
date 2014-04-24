package dk.aau.d101f14.tinyvm.instructions;

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
	public void read(byte[] code, int opCodeIndex) {
		type = Type.get(code[opCodeIndex + 1]);
		value = code[opCodeIndex + 2];
		if(type == Type.INT) {
			value2 = code[opCodeIndex + 3];
		}
	}

	@Override
	public void execute() {
		//Push value to operand stack
		tinyVm.getCurrentFrame().getOperandStack().push(getValue());
		tinyVm.getCurrentFrame().getOperandStackR().push(getValue());
				
		//Increment code pointer
		if(type == Type.INT) {
			tinyVm.getCurrentFrame().incrementCodePointer(4);
			tinyVm.getCurrentFrame().incrementCodePointerR(4);
		} else {
			tinyVm.getCurrentFrame().incrementCodePointer(3);
			tinyVm.getCurrentFrame().incrementCodePointerR(3);
		}
		
		if(tinyVm.getDebug()) {
			System.out.println("PUSH\t" + getType() + "\t" + getValue());
		}
	}

}
