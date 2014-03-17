package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.MethodDescriptorInfo;
import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.StringInfo;
import dk.aau.d101f14.tinyvm.TinyVM;

public class PutFieldInstruction extends Instruction {

	byte address1;
	byte address2;
	
	public PutFieldInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.PUTFIELD);
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
		MethodDescriptorInfo methodDescriptor = (MethodDescriptorInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[getAddress()];
		StringInfo fieldName = (StringInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[methodDescriptor.getClassName()];
		tinyVm.getHeap()[tinyVm.getCurrentFrame().getOperandStack().pop()].getFields().put(fieldName.getBytesString(), tinyVm.getCurrentFrame().getOperandStack().pop());
		tinyVm.getCurrentFrame().incrementCodePointer(3);
	}

}
