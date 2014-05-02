package dk.aau.d101f14.tinyvm.instructions;

import java.nio.file.Path;
import java.nio.file.Paths;

import dk.aau.d101f14.tinyvm.NativeMethodDescriptorInfo;
import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.StringInfo;
import dk.aau.d101f14.tinyvm.TinyVM;

public class InvokeNativeInstruction extends Instruction {
	byte address1;
	byte address2;
	
	public InvokeNativeInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.INVOKENATIVE);
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
			tinyVm.getCurrentFrame().getCheckpoint().update(tinyVm.getCurrentFrame().getLocalVariables().clone(), 
					tinyVm.getCurrentFrame().getOperandStack(), 
					tinyVm.getCurrentFrame().getCodePointer()+3);
			
			NativeMethodDescriptorInfo nativeMethodDescriptor = (NativeMethodDescriptorInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[getAddress()];
			StringInfo libraryPathInfo = (StringInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[nativeMethodDescriptor.getLibraryPath()];
			StringInfo methodNameInfo = (StringInfo)tinyVm.getCurrentFrame().getMethod().getTinyClass().getConstantPool()[nativeMethodDescriptor.getMethodName()];
			
			Path libraryPath = Paths.get(libraryPathInfo.getBytesString());
			if(!libraryPath.isAbsolute()) {
				libraryPath = tinyVm.getRootDirectory().resolve(libraryPath);
			}
			String methodName = methodNameInfo.getBytesString();
			
			int[] args = new int[nativeMethodDescriptor.getArgCount()];
			
			for(int i = 0; i < args.length; i++) {
				args[i] = tinyVm.getCurrentFrame().getOperandStack().pop();
				tinyVm.getCurrentFrame().getOperandStackR().pop();
			}
			
			int result = tinyVm.getNativeInterface().execute(libraryPath.toString(), methodName, args);
			
			tinyVm.getCurrentFrame().getOperandStack().push(new Integer(result));
			tinyVm.getCurrentFrame().getOperandStackR().push(new Integer(result));
			
			tinyVm.getCurrentFrame().incrementCodePointer(3);
			tinyVm.getCurrentFrame().incrementCodePointerR(3);
			
			if(tinyVm.getDebug()) {
				System.out.println("INVOKENATIVE\t" + getAddress());		
			}
		} else {
			tinyVm.getCurrentFrame().rollback();
		}
	}

}
