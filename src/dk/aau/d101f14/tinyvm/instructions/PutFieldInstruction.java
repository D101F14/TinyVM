package dk.aau.d101f14.tinyvm.instructions;

import java.util.AbstractMap.SimpleEntry;

import com.sun.org.apache.xalan.internal.xsltc.dom.MultiValuedNodeHeapIterator.HeapNode;

import dk.aau.d101f14.tinyvm.CPInfo;
import dk.aau.d101f14.tinyvm.ClassNameInfo;
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
			tinyVm.getCurrentFrame().getLocalHeap().put(new SimpleEntry<Integer, String>(objectRef, fieldName.getBytesString()), tinyVm.getCurrentFrame().getOperandStack().pop());		
			tinyVm.getCurrentFrame().getLocalHeapR().put(new SimpleEntry<Integer, String>(objectRefR, fieldName.getBytesString()), tinyVm.getCurrentFrame().getOperandStackR().pop());		
			
			tinyVm.getCurrentFrame().incrementCodePointer(3);
			tinyVm.getCurrentFrame().incrementCodePointerR(3);
			
			if(tinyVm.getDebug()) {
				System.out.println("PUTFIELD\t" + getAddress());
				if(!tinyVm.getHeap()[objectRef].getTinyClass().getFields().contains(fieldName.getBytesString())){
					int targetRef = tinyVm.getHeap()[objectRef].getTinyClass().getThisRef();
					CPInfo[] targetCP = tinyVm.getHeap()[objectRef].getTinyClass().getConstantPool();
					ClassNameInfo classNameInfo = (ClassNameInfo)targetCP[targetRef];
					String className = ((StringInfo)targetCP[classNameInfo.getClassName()]).getBytesString();
					System.out.println(className + " does not have field " + fieldName.getBytesString());
					System.exit(0);
				}		
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
