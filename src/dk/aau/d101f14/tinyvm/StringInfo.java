package dk.aau.d101f14.tinyvm;

public class StringInfo extends CPInfo {
	int length;
	byte[] bytes;
	
	public StringInfo(int length, byte[] bytes) {
		super((byte)4);
		this.length = length;
		this.bytes = bytes;
	}
	
	public int getLength() {
		return length;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public String getBytesString() {
		return new String(bytes);
	}
}
