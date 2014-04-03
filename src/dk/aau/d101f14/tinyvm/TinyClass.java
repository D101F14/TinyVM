package dk.aau.d101f14.tinyvm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class TinyClass {
	TinyVM tinyVm;
	TinyClass superClass;
	CPInfo[] constantPool;
	int thisRef;
	Integer superRef;
	int methodCount;
	HashMap<String, TinyMethod> methods;
	ArrayList<String> fields;
	
	public CPInfo[] getConstantPool() {
		return constantPool;
	}

	public int getThisRef() {
		return thisRef;
	}

	public Integer getSuperRef() {
		return superRef;
	}

	public void setSuperClass(TinyClass superClass) {
		this.superClass = superClass;
	}
	
	public TinyClass getSuperClass() {
		return superClass;
	}

	public int getMethodCount() {
		return methodCount;
	}

	public HashMap<String, TinyMethod> getMethods() {
		return methods;
	}
	
	public ArrayList<String> getFields() {
		return fields;
	}
	
	public static TinyMethod methodLookup(TinyClass tinyClass, String methodName) {
		if(tinyClass.getMethods().containsKey(methodName)) {
			return tinyClass.getMethods().get(methodName);
		} else if(tinyClass.getSuperClass() != null) {
			return methodLookup(tinyClass.getSuperClass(), methodName);
		}
		return null;
	}
	
	public TinyClass(TinyVM tinyVm) {
		this.tinyVm = tinyVm;
		methods = new HashMap<String, TinyMethod>();
		fields = new ArrayList<String>();
	}
	
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
			thisRef = stream.read() << 8 | stream.read();
			superRef = stream.read() << 8 | stream.read();
			if(thisRef == superRef) {
				superRef = null;
			}
			methodCount = stream.read() << 8 | stream.read();
			for(int i = 0; i < methodCount; i++) {
				TinyMethod method = new TinyMethod(this);
				method.read(stream);
				MethodDescriptorInfo methodDescriptor = (MethodDescriptorInfo)constantPool[method.methodDescriptor];
				String methodName = ((StringInfo)constantPool[methodDescriptor.getMethodName()]).getBytesString();
				methodName += "(";
				for(int j = 0; j < methodDescriptor.argCount; j++) {
					TypeInfo typeInfo = (TypeInfo)constantPool[methodDescriptor.argTypes[j]];
					String argType = String.valueOf(typeInfo.getType());
					if(argType == "l") {
						argType += "(";
						argType += ((StringInfo)constantPool[typeInfo.getClassName()]).getBytesString();
						argType += ")";
					}
					methodName += argType;
					if(j < methodDescriptor.argCount) {
						methodName += ", ";
					}
				}
				methodName += ")";
				methods.put(methodName, method);
			}
			for(CPInfo cpInfo : constantPool) {
				if(cpInfo instanceof ClassNameInfo) {
					String className = ((StringInfo)constantPool[((ClassNameInfo)cpInfo).className]).getBytesString();
					if(!tinyVm.loadList.contains(className)) {
						tinyVm.loadList.add(className);
					}
				} else if (cpInfo instanceof FieldDescriptorInfo) {
					if(((FieldDescriptorInfo)cpInfo).getClassName() == thisRef) {
						String fieldName = ((StringInfo)constantPool[((FieldDescriptorInfo)cpInfo).getFieldName()]).getBytesString();
						fields.add(fieldName);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
