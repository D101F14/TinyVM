package dk.aau.d101f14.tinyvm;

import java.io.IOException;
import java.io.InputStream;

public class TinyClass {
	CPInfo[] constantPool;
	
	public void read(InputStream stream) {
		try {
			int cpSize = stream.read() << 8 | stream.read();
			constantPool = new CPInfo[cpSize];
			for(int i = 0; i < cpSize; i++) {
				byte tag = (byte) stream.read();
				switch(tag) {
					case 1: {
						int className = stream.read() << 8 | stream.read();
					    constantPool[i] = new ClassNameInfo(className);
						break;
					}
					case 2: {
						int className = stream.read() << 8 | stream.read();
						int fieldName = stream.read() << 8 | stream.read();
						int fieldType = stream.read() << 8 | stream.read();
						constantPool[i] = new FieldDescriptorInfo(className, fieldName, fieldType);
						break;
					}
					case 3: {
						int className = stream.read() << 8 | stream.read();
						int methodName = stream.read() << 8 | stream.read();
						int argCount = stream.read() << 8 | stream.read();
						int[] argTypes = new int[argCount];
						for(int j = 0; j < argCount; j++) {
							argTypes[j] = stream.read() << 8 | stream.read();
						}
						int retType = stream.read() << 8 | stream.read();
						constantPool[i] = new MethodDescriptorInfo(className, methodName, argCount, argTypes, retType);
						break;
					}
					case 4: {
						int length = stream.read() << 8 | stream.read();
						byte[] bytes = new byte[length];
 						for(int j = 0; j < length; j++) {
							bytes[j] = (byte)stream.read();
						}
 						constantPool[i] = new StringInfo(length, bytes);
						break;
					}
					case 5: {
						byte type = (byte)stream.read();
						if(String.valueOf((char)type) == "l") {
							int className = stream.read() << 8 | stream.read();
							constantPool[i] = new TypeInfo(type, className);
							break;
						}
						constantPool[i] = new TypeInfo(type);
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
