package dk.aau.d101f14.tinyvm.encoder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Type;

public class Encoder {
	public static void main(String[] args) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			while(reader.ready()) {
				String[] instructionString = reader.readLine().split("\t");
				switch(instructionString[0]) {
					case "NOP":
						System.out.write(OpCode.NOP.getByte());
						break;
					case "PUSH":
						System.out.write(OpCode.PUSH.getByte());
						System.out.write(Type.valueOf(instructionString[1]).getByte());
						short value = Short.parseShort(instructionString[2]);
						if(Type.valueOf(instructionString[1]) == Type.INT) {
							byte value1 = (byte)((value & 0xFF00) >> 8);
							System.out.write(value1);
						}
						System.out.write(value);
						break;
					case "POP":
						break;
					case "LOAD":
						break;
					case "STORE":
						break;
					case "GOTO":
						break;
					case "IF":
						break;
					case "COMP":
						break;
					case "RETURN":
						break;
					default:
						break;
				}
			}
			System.out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
