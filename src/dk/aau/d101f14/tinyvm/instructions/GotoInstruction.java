package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;

public class GotoInstruction extends Instruction {

	byte address1;
	byte address2;
	
	public GotoInstruction(TinyVM tinyVM) {
		super(tinyVM, OpCode.GOTO);
	}
	
	public int getAddress() {
		return (int)(address1 & 0xFF << 8) | (int)(address2 & 0xFF);
	}
	
	@Override
	public void read(byte[] code, int opCodeIndex) {
		address1 = code[opCodeIndex + 1];
		address2 = code[opCodeIndex + 2];
	}

	@Override
	public void execute() {
		// Set code pointer to address.
		tinyVm.getCurrentFrame().setCodePointer(getAddress());
		tinyVm.getCurrentFrame().setCodePointerR(getAddress());
		
		if(tinyVm.getDebug()) {
			System.out.println("GOTO\t" + getAddress());
		}
	}
}
