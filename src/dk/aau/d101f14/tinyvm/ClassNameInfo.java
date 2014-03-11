package dk.aau.d101f14.tinyvm;

public class ClassNameInfo extends CPInfo {
	int className;
	
	public ClassNameInfo(int className) {
		super((byte)1);
		this.className = className;
	}
	
	public int getClassName() {
		return className;
	}

}
