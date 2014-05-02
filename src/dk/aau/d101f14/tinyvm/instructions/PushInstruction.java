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
		if(type != Type.INT && type != Type.REF) {
			return (int)(value & 0xFF);
		} else {
			return (int)(value & 0xFF << 8) | (int)(value2 & 0xFF);
		}
	}
	
	@Override
	public void read(byte[] code, int opCodeIndex) {
		type = Type.get(code[opCodeIndex + 1]);
		value = code[opCodeIndex + 2];
		if(type == Type.INT || type == Type.REF) {
			value2 = code[opCodeIndex + 3];
		}
	}

	@Override
	public void execute() {
		//Push value to operand stack
		tinyVm.getCurrentFrame().getOperandStack().push(new Integer(getValue()));
		tinyVm.getCurrentFrame().getOperandStackR().push(new Integer(getValue()));
				
		//Increment code pointer
		if(type == Type.INT || type == Type.REF) {
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
