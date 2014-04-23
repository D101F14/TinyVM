package dk.aau.d101f14.tinyvm;

public class TinyNativeInterface {
	public native int execute(String library, String function, int[] params);
	
	static {
		System.loadLibrary("tni");
	} 
}
