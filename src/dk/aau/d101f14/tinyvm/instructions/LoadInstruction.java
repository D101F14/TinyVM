package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;
import dk.aau.d101f14.tinyvm.Type;

public class LoadInstruction extends Instruction {

	Type type;
	byte address1;
	byte address2;
	
	public LoadInstruction(TinyVM tinyVM) {
		super(tinyVM, OpCode.LOAD);
	}

	public Type getType() {
		return type;
	}
	
	public int getAddress() {
		return (int)(address1 & 0xFF << 8) | (int)(address2 & 0xFF);
	}
	
	@Override
	public void read(byte[] code, int opCodeIndex) {
		type = Type.get(code[opCodeIndex + 1]);
		address1 = code[opCodeIndex + 2];
		address2 = code[opCodeIndex + 3];
	}

	@Override
	public void execute() {
		tinyVm.getCurrentFrame().getOperandStack().push(new Integer(tinyVm.getCurrentFrame().getLocalVariables()[getAddress()]));
		tinyVm.getCurrentFrame().getOperandStackR().push(new Integer(tinyVm.getCurrentFrame().getLocalVariablesR()[getAddress()]));
		
		//Increment code pointer
		tinyVm.getCurrentFrame().incrementCodePointer(4);
		tinyVm.getCurrentFrame().incrementCodePointerR(4);
		
		if(tinyVm.getDebug()) {
			System.out.println("LOAD\t" + getType() + "\t" + getAddress());		
		}
	}

}
