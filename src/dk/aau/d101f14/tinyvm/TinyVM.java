package dk.aau.d101f14.tinyvm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import dk.aau.d101f14.tinyvm.instructions.*;

public class TinyVM {
	public static void main(String[] args) {
		try {
			FileInputStream	stream = new FileInputStream(args[0]);
			int currentByte;
			while((currentByte = stream.read()) != -1) {
				OpCode opcode = OpCode.get((byte)currentByte);
				switch(opcode) {
					case NOP:
						NopInstruction nop = new NopInstruction();
						nop.read(stream);
						System.out.println("NOP");
						break;
					case PUSH:
						PushInstruction push = new PushInstruction();
						push.read(stream);
						System.out.println("PUSH\t" + push.getType() + "\t" + push.getValue());
						break;
					case POP:
						PopInstruction pop = new PopInstruction();
						pop.read(stream);
						System.out.println("POP\t" + pop.getNumber());
						break;
					case LOAD:
						LoadInstruction load = new LoadInstruction();
						load.read(stream);
						System.out.println("LOAD\t" + load.getType() + "\t" + load.getAddress());
						break;
					case STORE:
						StoreInstruction store = new StoreInstruction();
						store.read(stream);
						System.out.println("STORE\t" + store.getType() + "\t" + store.getAddress());
						break;
					case GOTO:
						GotoInstruction goto1 = new GotoInstruction();
						goto1.read(stream);
						System.out.println("GOTO\t" + goto1.getAddress());
						break;
					case IF:
						IfInstruction if1 = new IfInstruction();
						if1.read(stream);
						System.out.println("IF\t" + if1.getOperator() + "\t" + if1.getAddress());
						break;
					case COMP:
						CompInstruction comp = new CompInstruction();
						comp.read(stream);
						System.out.println("COMP\t" + comp.getOperator());
						break;
					case RETURN:
						ReturnInstruction return1 = new ReturnInstruction();
						return1.read(stream);
						System.out.println("RETURN\t" + return1.getType());
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
