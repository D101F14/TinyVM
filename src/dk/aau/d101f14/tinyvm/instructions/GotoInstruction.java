package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.TinyVM;

public class GotoInstruction extends Instruction {

	byte address1;
	byte address2;
	
	public GotoInstruction(TinyVM tinyVM) {
		super(tinyVM, OpCode.GOTO);
	}
	
	public int getAddress() {
		return address1 << 8 | address2;
	}
	
	@Override
	public void read(InputStream stream) {
		try {
			address1 = (byte) stream.read();
			address2 = (byte) stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		// Set code pointer to address.
		tinyVm.getCurrentFrame().setCodePointer(getAddress());
		
		if(tinyVm.getDebug()) {
			System.out.println("GOTO\t" + getAddress());
		}
	}
}
