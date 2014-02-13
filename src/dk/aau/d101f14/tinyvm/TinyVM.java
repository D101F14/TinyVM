package dk.aau.d101f14.tinyvm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import dk.aau.d101f14.tinyvm.instructions.*;

public class TinyVM {
	public static void main(String[] args) {
		try {
			FileInputStream	fiStream = new FileInputStream(args[0]);
			int currentByte;
			while((currentByte = fiStream.read()) != -1) {
				OpCode opcode = OpCode.get((byte)currentByte);
				switch(opcode) {
					case NOP:
						NopInstruction nop = new NopInstruction();
						nop.read(fiStream);
						System.out.println("NOP");
						break;
					case PUSH:
						PushInstruction push = new PushInstruction();
						push.read(fiStream);
						System.out.println("PUSH\t" + push.getType() + "\t" + push.getValue());
						break;
					case POP:
						System.out.println("POP!");
						break;
					case LOAD:
						System.out.println("LOAD!");
						break;
					default:
						break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
