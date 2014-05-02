package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;
import dk.aau.d101f14.tinyvm.Type;

public class ReturnInstruction extends Instruction {

	Type type;
	
	public ReturnInstruction(TinyVM tinyVM) {
		super(tinyVM, OpCode.RETURN);
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public void read(byte[] code, int opCodeIndex) {
		type = Type.get(code[opCodeIndex + 1]);
	}

	@Override
	public void execute() {
		if(tinyVm.getCurrentFrame().checkFrame()) {
			tinyVm.getCurrentFrame().commitLocalHeap();
			tinyVm.getCurrentFrame().getCheckpoint().update(tinyVm.getCurrentFrame().getLocalVariables().clone(), 
					tinyVm.getCurrentFrame().getOperandStack(), 
					tinyVm.getCurrentFrame().getCodePointer()+3);
			
			Integer value = null;
			if(type != Type.VOID) {
				value = tinyVm.getCurrentFrame().getOperandStack().pop();
				tinyVm.getCurrentFrame().getOperandStackR().pop();
			}
			tinyVm.getCallStack().pop();
			
			if(!tinyVm.getCallStack().isEmpty()) {
				if(value != null) {
					tinyVm.getCurrentFrame().getOperandStack().push(new Integer(value));
					tinyVm.getCurrentFrame().getOperandStackR().push(new Integer(value));
					
					tinyVm.getCurrentFrame().getCheckpoint().getOperandStack().push(new Integer(value));
				}
				
			}
			if(tinyVm.getDebug()) {
				System.out.println("RETURN\t" + getType());
			}
		} else {
			tinyVm.getCurrentFrame().rollback();
		}
	}

}
