package dk.aau.d101f14.tinyvm.instructions;

import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;

public class NopInstruction extends Instruction {
	public NopInstruction(TinyVM tinyVM) {
		super(tinyVM, OpCode.NOP);
	}

	@Override
	public void read(InputStream stream) {
		// No operation!
	}

	@Override
	public void execute() {
		// Increment code pointer
		tinyVm.getCurrentFrame().setCodePointer(tinyVm.getCurrentFrame().getCodePointer() + 1);
		
		if(tinyVm.getDebug()) {
			System.out.println("NOP");
		}
	}
}
