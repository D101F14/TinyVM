package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;

public class DupInstruction extends Instruction {

	public DupInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.DUP);
	}

	@Override
	public void read(byte[] code, int opCodeIndex) {
	}

	@Override
	public void execute() {
		tinyVm.getCurrentFrame().getOperandStack().push(tinyVm.getCurrentFrame().getOperandStack().peek());
		tinyVm.getCurrentFrame().getOperandStackR().push(tinyVm.getCurrentFrame().getOperandStackR().peek());
		
		tinyVm.getCurrentFrame().incrementCodePointer(1);
		tinyVm.getCurrentFrame().incrementCodePointerR(1);
		
		if(tinyVm.getDebug()) {
			System.out.println("DUP");		
		}
	}

}
