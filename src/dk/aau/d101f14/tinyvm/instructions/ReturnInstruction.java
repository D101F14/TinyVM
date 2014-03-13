package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;
import dk.aau.d101f14.tinyvm.Type;

public class ReturnInstruction extends Instruction {

	Type type;
	
	public ReturnInstruction(TinyVM tinyVM) {
		super(tinyVM, OpCode.RETURN);
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public void read(InputStream stream) {
		try {
			type = Type.get((byte) stream.read());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		//Increment code pointer
		tinyVm.getCurrentFrame().setCodePointer(tinyVm.getCurrentFrame().getCodePointer() + 1);
		
		if(tinyVm.getDebug()) {
			System.out.println("RETURN\t" + getType());
		}
	}

}
