package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.FieldDescriptorInfo;
import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.StringInfo;
import dk.aau.d101f14.tinyvm.TinyObject;
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
		FieldDescriptorInfo fieldDescriptor = (FieldDescriptorInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[getAddress()];
		StringInfo fieldName = (StringInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[fieldDescriptor.getFieldName()];
		
		Integer objectRef = tinyVm.getCurrentFrame().getOperandStack().pop();

		if(objectRef > 0) {
			tinyVm.getHeap()[objectRef].getFields().put(fieldName.getBytesString(), tinyVm.getCurrentFrame().getOperandStack().pop());
			tinyVm.getCurrentFrame().incrementCodePointer(3);
			if(tinyVm.getDebug()) {
				System.out.println("PUTFIELD\t" + getAddress());		
			}
		} else {
			tinyVm.getHeap()[tinyVm.getHeapCounter()] = new TinyObject(tinyVm.getClasses().get("NullReferenceException"));
			tinyVm.incrementHeapCounter();
			tinyVm.throwException(tinyVm.getHeapCounter() - 1);
		}
	}

}
