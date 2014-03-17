package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;

public class NopInstruction extends Instruction {
	public NopInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.NOP);
	}

	@Override
	public void read(byte[] code, int opCodeIndex) {
		// No operation!
	}

	@Override
	public void execute() {
		tinyVm.getCurrentFrame().incrementCodePointer(1);
		
		if(tinyVm.getDebug()) {
			System.out.println("NOP");
		}
	}
}
