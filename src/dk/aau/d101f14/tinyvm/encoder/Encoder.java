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
	
	public static int getArrayIndexById(JsonArray jsonArray, int id) {
		for(int i = 0; i < jsonArray.size(); i++) {
			JsonObject obj = jsonArray.get(i).getAsJsonObject();
			if(obj.get("id").getAsInt() == id) {
				return i;
			}
		}
		return -1;
	}
	
	public static void main(String[] args) {
		JsonParser parser = new JsonParser();
		try {
			JsonObject classObject = parser.parse(new FileReader(args[0])).getAsJsonObject();
			byte[] cafebabe = new byte[]{(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe};
			System.out.write(cafebabe);
			
			JsonArray constantPool = classObject.get("constant_pool").getAsJsonArray();
			writeInt(constantPool.size());
			for(int i = 0; i < constantPool.size(); i++) {
				JsonObject cpInfo = constantPool.get(i).getAsJsonObject();
				System.out.write(cpInfo.get("tag").getAsByte());
				switch(cpInfo.get("tag").getAsByte()) {
					case 1:
						writeInt(getArrayIndexById(constantPool, cpInfo.get("class_name").getAsInt()));
						break;
					case 2:
						writeInt(getArrayIndexById(constantPool, cpInfo.get("class_name").getAsInt()));
						writeInt(getArrayIndexById(constantPool, cpInfo.get("field_name").getAsInt()));
						writeInt(getArrayIndexById(constantPool, cpInfo.get("field_type").getAsInt()));
						break;
					case 3:
						writeInt(getArrayIndexById(constantPool, cpInfo.get("class_name").getAsInt()));
						writeInt(getArrayIndexById(constantPool, cpInfo.get("method_name").getAsInt()));
						if(cpInfo.get("arg_types") != null) {
							writeInt(cpInfo.get("arg_types").getAsJsonArray().size());
							for(int j = 0; j < cpInfo.get("arg_types").getAsJsonArray().size(); j++) {
								writeInt(getArrayIndexById(constantPool, cpInfo.get("arg_types").getAsJsonArray().get(j).getAsInt()));
							}
						} else {
							writeInt(0);
						}
						writeInt(getArrayIndexById(constantPool, cpInfo.get("ret_type").getAsInt()));
						break;
					case 4:
						writeInt(cpInfo.get("bytes").getAsString().length());
						System.out.print(cpInfo.get("bytes").getAsString());
						break;
					case 5:
						System.out.print(cpInfo.get("type").getAsString());
						break;
					case 6:
						writeInt(getArrayIndexById(constantPool, cpInfo.get("library_path").getAsInt()));
						writeInt(getArrayIndexById(constantPool, cpInfo.get("method_name").getAsInt()));
						writeInt(cpInfo.get("arg_types").getAsJsonArray().size());
						for(int j = 0; j < cpInfo.get("arg_types").getAsJsonArray().size(); j++) {
							writeInt(getArrayIndexById(constantPool, cpInfo.get("arg_types").getAsJsonArray().get(j).getAsInt()));
						}
						writeInt(getArrayIndexById(constantPool, cpInfo.get("ret_type").getAsInt()));
						break;
				}
			}
			
			writeInt(getArrayIndexById(constantPool, classObject.get("this").getAsInt()));
			writeInt(getArrayIndexById(constantPool, classObject.get("super").getAsInt()));
			writeInt(classObject.get("methods").getAsJsonArray().size());
			
			JsonArray methods = classObject.get("methods").getAsJsonArray();
			
			for(int i = 0; i < methods.size(); i++) {
				JsonObject tinyMethod = methods.get(i).getAsJsonObject();
				writeInt(tinyMethod.get("method_descriptor").getAsInt());
				writeInt(tinyMethod.get("max_stack").getAsInt());
				writeInt(tinyMethod.get("max_locals").getAsInt());
				JsonArray code = tinyMethod.get("code").getAsJsonArray();
				int codeLength = 0;
				for(int j = 0; j < code.size(); j++) {
					String[] instructionString = code.get(j).getAsString().split(" ");
					
					switch(instructionString[0].toUpperCase()) {
						case "NOP":
							codeLength += 1;
							break;
						case "PUSH":
							codeLength += 3;
							if(Type.valueOf(instructionString[1]) == Type.INT || Type.valueOf(instructionString[1]) == Type.REF) {
								codeLength += 1;
							}
							break;
						case "POP":
							codeLength += 2;
							break;
						case "LOAD":
							codeLength += 4;
							break;
						case "STORE":
							codeLength += 4;
							break;
						case "GOTO":
							codeLength += 3;
							break;
						case "IF":
							codeLength += 4;
							break;
						case "COMP":
							codeLength += 2;
							break;
						case "NEW":
							codeLength += 3;
							break;
						case "GETFIELD":
							codeLength += 3;;
							break;
						case "PUTFIELD":
							codeLength += 3;
							break;
						case "INVOKEVIRTUAL":
							codeLength += 3;
							break;
						case "INVOKENATIVE":
							codeLength += 3;
							break;
						case "RETURN":
							codeLength += 2;
							break;
						case "DUP":
							codeLength += 1;
							break;
						case "THROW":
							codeLength += 1;
							break;
					}
				}
				
				writeInt(codeLength);
				
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
							if(Type.valueOf(instructionString[1]) == Type.INT || Type.valueOf(instructionString[1]) == Type.REF) {
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
							writeInt(getArrayIndexById(constantPool, Short.parseShort(instructionString[1])));
							break;
						case "GETFIELD":
							System.out.write(OpCode.GETFIELD.getByte());
							writeInt(getArrayIndexById(constantPool, Short.parseShort(instructionString[1])));
							break;
						case "PUTFIELD":
							System.out.write(OpCode.PUTFIELD.getByte());
							writeInt(getArrayIndexById(constantPool, Short.parseShort(instructionString[1])));
							break;
						case "INVOKEVIRTUAL":
							System.out.write(OpCode.INVOKEVIRTUAL.getByte());
							writeInt(getArrayIndexById(constantPool, Short.parseShort(instructionString[1])));
							break;
						case "INVOKENATIVE":
							System.out.write(OpCode.INVOKENATIVE.getByte());
							writeInt(getArrayIndexById(constantPool, Short.parseShort(instructionString[1])));
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
				
				if(tinyMethod.get("handlers") != null) {
				writeInt(tinyMethod.get("handlers").getAsJsonArray().size());
					if(tinyMethod.get("handlers").getAsJsonArray().size() > 0) {
						JsonArray handlers = tinyMethod.get("handlers").getAsJsonArray();
						for(int j = 0; j < handlers.size(); j++) {
							JsonObject handler = handlers.get(j).getAsJsonObject();
							writeInt(handler.get("start_pc").getAsInt());
							writeInt(handler.get("end_pc").getAsInt());
							writeInt(handler.get("handler_pc").getAsInt());
							writeInt(getArrayIndexById(constantPool, handler.get("type").getAsInt()));
						}
					}
				} else {
					writeInt(0);
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
