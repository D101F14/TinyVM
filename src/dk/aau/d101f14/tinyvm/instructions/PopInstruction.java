package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;

public class PopInstruction extends Instruction {

	byte number;
	
	public PopInstruction(TinyVM tinyVM) {
		super(tinyVM, OpCode.POP);
	}
	
	public byte getNumber() {
		return number;
	}
	
	@Override
	public void read(InputStream stream) {
		try {
			number = (byte) stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		// Pop n elements from stack
		for(int i = 0; i < number; i++) {
			tinyVM.getOperandStack().pop();
		}
		
		// Increment code pointer
		tinyVM.setCodePointer(tinyVM.getCodePointer() + 1);
		
		if(tinyVM.getDebug()) {
			System.out.println("POP\t" + getNumber());
		}
	}

}
