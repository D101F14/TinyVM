package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.FieldDescriptorInfo;
import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.StringInfo;
import dk.aau.d101f14.tinyvm.TinyVM;

public class GetFieldInstruction extends Instruction {

	byte address1;
	byte address2;
	
	public GetFieldInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.GETFIELD);
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
		
		int field = tinyVm.getHeap()[tinyVm.getCurrentFrame().getOperandStack().pop()].getFields().get(fieldName.getBytesString());
		tinyVm.getCurrentFrame().getOperandStack().push(field);
		tinyVm.getCurrentFrame().incrementCodePointer(3);
		
		if(tinyVm.getDebug()) {
			System.out.println("GETFIELD\t" + getAddress());		
		}
	}

}
