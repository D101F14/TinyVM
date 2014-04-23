package dk.aau.d101f14.tinyvm.encoder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Operator;
import dk.aau.d101f14.tinyvm.Type;

public class Encoder {
	public static void writeInt(int value) {
		System.out.write((byte)((value & 0xFF00) >> 8));
		System.out.write(value);
	}
	
	public static void main(String[] args) {
		JsonParser parser = new JsonParser();
		try {
			JsonObject classObject = parser.parse(new FileReader(args[0])).getAsJsonObject();
			byte[] cafebabe = new byte[]{(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe};
			System.out.write(cafebabe);
			
			writeInt(classObject.get("cp_count").getAsInt());
			
			JsonArray constantPool = classObject.get("constant_pool").getAsJsonArray();
			for(int i = 0; i < constantPool.size(); i++) {
				JsonObject cpInfo = constantPool.get(i).getAsJsonObject();
				System.out.write(cpInfo.get("tag").getAsByte());
				switch(cpInfo.get("tag").getAsByte()) {
					case 1:
						writeInt(cpInfo.get("class_name").getAsInt());
						break;
					case 2:
						writeInt(cpInfo.get("class_name").getAsInt());
						writeInt(cpInfo.get("field_name").getAsInt());
						writeInt(cpInfo.get("field_type").getAsInt());
						break;
					case 3:
						writeInt(cpInfo.get("class_name").getAsInt());
						writeInt(cpInfo.get("method_name").getAsInt());
						writeInt(cpInfo.get("arg_count").getAsInt());
						for(int j = 0; j < cpInfo.get("arg_count").getAsInt(); j++) {
							writeInt(cpInfo.get("arg_types").getAsJsonArray().get(j).getAsInt());
						}
						writeInt(cpInfo.get("ret_type").getAsInt());
						break;
					case 4:
						writeInt(cpInfo.get("length").getAsInt());
						System.out.print(cpInfo.get("bytes").getAsString());
						break;
					case 5:
						System.out.print(cpInfo.get("type").getAsString());
						break;
					case 6:
						writeInt(cpInfo.get("library_path").getAsInt());
						writeInt(cpInfo.get("method_name").getAsInt());
						writeInt(cpInfo.get("arg_count").getAsInt());
						for(int j = 0; j < cpInfo.get("arg_count").getAsInt(); j++) {
							writeInt(cpInfo.get("arg_types").getAsJsonArray().get(j).getAsInt());
						}
						writeInt(cpInfo.get("ret_type").getAsInt());
						break;
				}
			}
			
			writeInt(classObject.get("this").getAsInt());
			writeInt(classObject.get("super").getAsInt());
			writeInt(classObject.get("method_count").getAsInt());
			
			JsonArray methods = classObject.get("methods").getAsJsonArray();
			
			for(int i = 0; i < methods.size(); i++) {
				JsonObject tinyMethod = methods.get(i).getAsJsonObject();
				writeInt(tinyMethod.get("method_descriptor").getAsInt());
				writeInt(tinyMethod.get("max_stack").getAsInt());
				writeInt(tinyMethod.get("max_locals").getAsInt());
				writeInt(tinyMethod.get("code_length").getAsInt());
				JsonArray code = tinyMethod.get("code").getAsJsonArray();
				for(int j = 0; j < code.size(); j++) {
					String[] instructionString = code.get(j).getAsString().split(" ");
					switch(instructionString[0].toUpperCase()) {
						case "NOP":
							System.out.write(OpCode.NOP.getByte());
							break;
						case "PUSH":
							System.out.write(OpCode.PUSH.getByte());
							System.out.write(Type.valueOf(instructionString[1]).getByte());
							short value = Short.parseShort(instructionString[2]);
							if(Type.valueOf(instructionString[1]) == Type.INT) {
								System.out.write((byte)((value & 0xFF00) >> 8));
							}
							System.out.write(value);
							break;
						case "POP":
							System.out.write(OpCode.POP.getByte());
							System.out.write(Byte.parseByte(instructionString[1]));
							break;
						case "LOAD":
							System.out.write(OpCode.LOAD.getByte());
							System.out.write(Type.valueOf(instructionString[1]).getByte()); 
							writeInt(Short.parseShort(instructionString[2]));
							break;
						case "STORE":
							System.out.write(OpCode.STORE.getByte());
							System.out.write(Type.valueOf(instructionString[1]).getByte()); 
							writeInt(Short.parseShort(instructionString[2]));
							break;
						case "GOTO":
							System.out.write(OpCode.GOTO.getByte());
							writeInt(Short.parseShort(instructionString[1]));
							break;
						case "IF":
							System.out.write(OpCode.IF.getByte());
							System.out.write(Operator.valueOf(instructionString[1]).getByte());
							writeInt(Short.parseShort(instructionString[2]));
							break;
						case "COMP":
							System.out.write(OpCode.COMP.getByte());
							System.out.write(Operator.valueOf(instructionString[1]).getByte());
							break;
						case "NEW":
							System.out.write(OpCode.NEW.getByte());
							writeInt(Short.parseShort(instructionString[1]));
							break;
						case "GETFIELD":
							System.out.write(OpCode.GETFIELD.getByte());
							writeInt(Short.parseShort(instructionString[1]));
							break;
						case "PUTFIELD":
							System.out.write(OpCode.PUTFIELD.getByte());
							writeInt(Short.parseShort(instructionString[1]));
							break;
						case "INVOKEVIRTUAL":
							System.out.write(OpCode.INVOKEVIRTUAL.getByte());
							writeInt(Short.parseShort(instructionString[1]));
							break;
						case "INVOKENATIVE":
							System.out.write(OpCode.INVOKENATIVE.getByte());
							writeInt(Short.parseShort(instructionString[1]));
							break;
						case "RETURN":
							System.out.write(OpCode.RETURN.getByte());
							System.out.write(Type.valueOf(instructionString[1]).getByte());
							break;
						case "DUP":
							System.out.write(OpCode.DUP.getByte());
							break;
						case "THROW":
							System.out.write(OpCode.THROW.getByte());
							break;
					}
				}
				
				writeInt(tinyMethod.get("handler_count").getAsInt());
				if(tinyMethod.get("handler_count").getAsInt() > 0) {
					JsonArray handlers = tinyMethod.get("handlers").getAsJsonArray();
					for(int j = 0; j < handlers.size(); j++) {
						JsonObject handler = handlers.get(j).getAsJsonObject();
						writeInt(handler.get("start_pc").getAsInt());
						writeInt(handler.get("end_pc").getAsInt());
						writeInt(handler.get("handler_pc").getAsInt());
						writeInt(handler.get("type").getAsInt());
					}
				}
			}
			
			
			
			System.out.flush();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
