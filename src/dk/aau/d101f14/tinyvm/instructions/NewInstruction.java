package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.ClassNameInfo;
import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.StringInfo;
import dk.aau.d101f14.tinyvm.TinyObject;
import dk.aau.d101f14.tinyvm.TinyVM;

public class NewInstruction extends Instruction {
	byte address1;
	byte address2;
	
	public NewInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.NEW);
	}

	public int getAddress() {
		return address1 << 8 | address2;
	}

	@Override
	public void read(byte[] code, int opCodeIndex) {
		address1 = code[opCodeIndex + 1];
		address2 = code[opCodeIndex + 2];
	}

	@Override
	public void execute() {
		ClassNameInfo classNameInfo = (ClassNameInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[getAddress()];
		StringInfo className = (StringInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[classNameInfo.getClassName()];
		tinyVm.getHeap()[tinyVm.getHeapCounter()] = new TinyObject(tinyVm.getClasses().get(className.getBytesString()));
		
		tinyVm.getCurrentFrame().getOperandStack().push(tinyVm.getHeapCounter());
		tinyVm.incrementHeapCounter();
		
		tinyVm.getCurrentFrame().incrementCodePointer(3);
	}
}
