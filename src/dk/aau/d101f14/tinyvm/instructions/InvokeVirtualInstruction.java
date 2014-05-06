package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.MethodDescriptorInfo;
import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.StringInfo;
import dk.aau.d101f14.tinyvm.TinyClass;
import dk.aau.d101f14.tinyvm.TinyFrame;
import dk.aau.d101f14.tinyvm.TinyMethod;
import dk.aau.d101f14.tinyvm.TinyVM;
import dk.aau.d101f14.tinyvm.TypeInfo;

public class InvokeVirtualInstruction extends Instruction {

	byte address1;
	byte address2;
	
	public InvokeVirtualInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.INVOKEVIRTUAL);
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
		if(tinyVm.getCurrentFrame().checkFrame()) {
			tinyVm.getCurrentFrame().commitLocalHeap();
			
			MethodDescriptorInfo methodDescriptor = (MethodDescriptorInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[getAddress()];
			StringInfo className = (StringInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[methodDescriptor.getClassName()];
			StringInfo methodNameInfo = (StringInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[methodDescriptor.getMethodName()];
			
			String methodName = methodNameInfo.getBytesString();
			
			methodName += "(";
			for(int j = 0; j < methodDescriptor.getArgCount(); j++) {
				TypeInfo typeInfo = (TypeInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[methodDescriptor.getArgTypes()[j]];
				String argType = String.valueOf(typeInfo.getType());
				if(argType == "l") {
					argType += "(";
					argType += ((StringInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[typeInfo.getClassName()]).getBytesString();
					argType += ")";
				}
				methodName += argType;
				if(j < methodDescriptor.getArgCount()) {
					methodName += ", ";
				}
			}
			methodName += ")";
			
			TinyMethod method = TinyClass.methodLookup(tinyVm.getClasses().get(className.getBytesString()), methodName);
			int[] localVariables = new int[method.getMaxLocals()];
			for(int i = 0; i < methodDescriptor.getArgCount(); i++) {
				localVariables[i] = tinyVm.getCurrentFrame().getOperandStack().pop();
				tinyVm.getCurrentFrame().getOperandStackR().pop();
			}
			
			tinyVm.getCurrentFrame().incrementCodePointer(3);
			tinyVm.getCurrentFrame().incrementCodePointerR(3);
			
			tinyVm.getCurrentFrame().getCheckpoint().update(tinyVm.getCurrentFrame().getLocalVariables().clone(), 
					tinyVm.getCurrentFrame().getOperandStack(), 
					tinyVm.getCurrentFrame().getCodePointer());
			
			tinyVm.getCallStack().push(new TinyFrame(tinyVm, localVariables, method));
			
			if(tinyVm.getDebug()) {
				System.out.println("INVOKEVIRTUAL\t" + getAddress());		
			}
		} else {
			tinyVm.getCurrentFrame().rollback();
		}
	}
}
