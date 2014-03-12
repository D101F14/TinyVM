package dk.aau.d101f14.tinyvm;

import java.io.IOException;
import java.io.InputStream;

public class TinyMethod {
	int methodDescriptor;
	int maxStack;
	int maxLocals;
	int codeLength;
	byte[] code;
	int handlerCount;
	TinyHandler[] handlers;
	
	public void read(InputStream stream) {
		try {
			methodDescriptor = stream.read() << 8 | stream.read();
			maxStack = stream.read() << 8 | stream.read();
			maxLocals = stream.read() << 8 | stream.read();
			codeLength = stream.read() << 8 | stream.read();
			code = new byte[codeLength];
			for(int i = 0; i < codeLength; i++) {
				code[i] = (byte)stream.read();
			}
			handlerCount = stream.read() << 8 | stream.read();
			handlers = new TinyHandler[handlerCount];
			for(int i = 0; i < handlerCount; i++) {
				handlers[i] = new TinyHandler();
				handlers[i].read(stream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
