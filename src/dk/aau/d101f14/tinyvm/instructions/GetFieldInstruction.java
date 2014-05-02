package dk.aau.d101f14.tinyvm.instructions;

import java.util.AbstractMap.SimpleEntry;

import dk.aau.d101f14.tinyvm.FieldDescriptorInfo;
import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.StringInfo;
import dk.aau.d101f14.tinyvm.TinyObject;
import dk.aau.d101f14.tinyvm.TinyVM;

public class GetFieldInstruction extends Instruction {

	byte address1;
	byte address2;
	
	public GetFieldInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.GETFIELD);
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
		FieldDescriptorInfo fieldDescriptor = (FieldDescriptorInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[getAddress()];
		StringInfo fieldName = (StringInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[fieldDescriptor.getFieldName()];
		
		int objectRef = tinyVm.getCurrentFrame().getOperandStack().pop();
		int objectRefR = tinyVm.getCurrentFrame().getOperandStackR().pop();
		
		if(objectRef > 0 && objectRefR > 0) {
			Integer field = tinyVm.getCurrentFrame().getLocalHeap().get(new SimpleEntry<Integer, String>(objectRef, fieldName.getBytesString()));
			if(field == null) {
					field = tinyVm.getHeap()[objectRef].getFields().get(fieldName.getBytesString());
			}
			
			Integer fieldR = tinyVm.getCurrentFrame().getLocalHeapR().get(new SimpleEntry<Integer, String>(objectRefR, fieldName.getBytesString()));
			if(fieldR == null) {
					fieldR = tinyVm.getHeap()[objectRefR].getFields().get(fieldName.getBytesString());
			}

			tinyVm.getCurrentFrame().getOperandStack().push(new Integer(field.intValue()));
			tinyVm.getCurrentFrame().incrementCodePointer(3);

			tinyVm.getCurrentFrame().getOperandStackR().push(new Integer(fieldR.intValue()));
			tinyVm.getCurrentFrame().incrementCodePointerR(3);
			
			if(tinyVm.getDebug()) {
				System.out.println("GETFIELD\t" + getAddress());		
			}
		} else {
			if(tinyVm.getCurrentFrame().checkFrame()) {
				tinyVm.getCurrentFrame().commitLocalHeap();			
				tinyVm.getHeap()[tinyVm.getHeapCounter()] = new TinyObject(tinyVm.getClasses().get("NullReferenceException"));
				tinyVm.incrementHeapCounter();
				tinyVm.throwException(tinyVm.getHeapCounter() - 1);
			} else {
				tinyVm.getCurrentFrame().rollback();
			}
		}
	}

}
