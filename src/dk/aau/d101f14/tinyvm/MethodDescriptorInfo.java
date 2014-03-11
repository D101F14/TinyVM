package dk.aau.d101f14.tinyvm;

public class MethodDescriptorInfo extends CPInfo {
	int className;
	int methodName;
	int argCount;
	int[] argTypes;
	int retType;
	
	public MethodDescriptorInfo(int className, int methodName, int argCount, int[] argTypes, int retType) {
		super((byte)3);
		this.className = className;
		this.methodName = methodName;
		this.argCount = argCount;
		this.argTypes = argTypes;
		this.retType = retType;
	}
	
	public int getClassName() {
		return className;
	}

	public int getMethodName() {
		return methodName;
	}

	public int getArgCount() {
		return argCount;
	}

	public int[] getArgTypes() {
		return argTypes;
	}

	public int getRetType() {
		return retType;
	}
}
