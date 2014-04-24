package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;

public class PopInstruction extends Instruction {

	byte number;
	
	public PopInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.POP);
	}
	
	public byte getNumber() {
		return number;
	}
	
	@Override
	public void read(byte[] code, int opCodeIndex) {
		number = code[opCodeIndex + 1];
	}

	@Override
	public void execute() {
		// Pop n elements from stack
		for(int i = 0; i < number; i++) {
			tinyVm.getCurrentFrame().getOperandStack().pop();
			tinyVm.getCurrentFrame().getOperandStackR().pop();
		}
		
		tinyVm.getCurrentFrame().incrementCodePointer(2);
		tinyVm.getCurrentFrame().incrementCodePointerR(2);
		
		if(tinyVm.getDebug()) {
			System.out.println("POP\t" + getNumber());
		}
	}

}
