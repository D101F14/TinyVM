package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;

public class ThrowInstruction extends Instruction {
	
	public ThrowInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.THROW);
	}

	@Override
	public void read(byte[] code, int opCodeIndex) {
	}
	
	@Override
	public void execute() {
		tinyVm.throwException(tinyVm.getCurrentFrame().getOperandStack().pop());
	}

}
