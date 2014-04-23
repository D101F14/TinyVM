package dk.aau.d101f14.tinyvm;

public class NativeMethodDescriptorInfo extends CPInfo {

	int libraryPath;
	int methodName;
	int argCount;
	int[] argTypes;
	int retType;
	
	public int getLibraryPath() {
		return libraryPath;
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

	public NativeMethodDescriptorInfo(int libraryPath, int methodName, int argCount, int[] argTypes, int retType) {
		super((byte)6);
		this.libraryPath = libraryPath;
		this.methodName = methodName;
		this.argCount = argCount;
		this.argTypes = argTypes;
		this.retType = retType;
	}

}
