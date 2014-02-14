package dk.aau.d101f14.tinyvm.encoder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Type;
import dk.aau.d101f14.tinyvm.Operator;

public class Encoder {
	public static void main(String[] args) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			while(reader.ready()) {
				String[] instructionString = reader.readLine().split("\t");
				switch(instructionString[0]) {
					case "NOP": {
						System.out.write(OpCode.NOP.getByte());
						break;
					}
					case "PUSH":  {
						System.out.write(OpCode.PUSH.getByte());
						System.out.write(Type.valueOf(instructionString[1]).getByte());
						short value = Short.parseShort(instructionString[2]);
						if(Type.valueOf(instructionString[1]) == Type.INT) {
							System.out.write((byte)((value & 0xFF00) >> 8));
						}
						System.out.write(value);
						break;
					}
					case "POP": {
						System.out.write(OpCode.POP.getByte());
						System.out.write(Byte.parseByte(instructionString[1]));
						break;
					}
					case "LOAD": {
						System.out.write(OpCode.LOAD.getByte());
						System.out.write(Type.valueOf(instructionString[1]).getByte());
						short address = Short.parseShort(instructionString[2]);
						System.out.write((byte)((address & 0xFF00) >> 8));
						System.out.write(address);
						break;
					}
					case "STORE": {
						System.out.write(OpCode.STORE.getByte());
						System.out.write(Type.valueOf(instructionString[1]).getByte());
						short address = Short.parseShort(instructionString[2]);
						System.out.write((byte)((address & 0xFF00) >> 8));
						System.out.write(address);
						break;
					}
					case "GOTO": {
						System.out.write(OpCode.GOTO.getByte());
						short address = Short.parseShort(instructionString[1]);
						System.out.write((byte)((address & 0xFF00) >> 8));
						System.out.write(address);
						break;
					}
					case "IF": {
						System.out.write(OpCode.IF.getByte());
						System.out.write(Operator.valueOf(instructionString[1]).getByte());
						short address = Short.parseShort(instructionString[2]);
						System.out.write((byte)((address & 0xFF00) >> 8));
						System.out.write(address);
						break;
					}
					case "COMP": {
						System.out.write(OpCode.COMP.getByte());
						System.out.write(Operator.valueOf(instructionString[1]).getByte());
						break;
					}
					case "RETURN": {
						System.out.write(OpCode.RETURN.getByte());
						System.out.write(Type.valueOf(instructionString[1]).getByte());
						break;
					}
					default: {
						break;
					}
				}
			}
			System.out.flush();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
