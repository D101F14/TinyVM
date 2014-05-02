package dk.aau.d101f14.tinyvm;

import java.io.IOException;
import java.io.InputStream;

public class TinyMethod {
	TinyClass tinyClass;
	int methodDescriptor;
	int maxStack;
	int maxLocals;
	int codeLength;
	byte[] code;
	int handlerCount;
	TinyHandler[] handlers;
	
	public TinyMethod(TinyClass tinyClass) {
		this.tinyClass = tinyClass;
	}
	
	public void read(InputStream stream) {
		try {
			methodDescriptor = (int)(stream.read() & 0xFF << 8) | (int)(stream.read() & 0xFF);
			maxStack = (int)(stream.read() & 0xFF << 8) | (int)(stream.read() & 0xFF);
			maxLocals = (int)(stream.read() & 0xFF << 8) | (int)(stream.read() & 0xFF);
			codeLength = (int)(stream.read() & 0xFF << 8) | (int)(stream.read() & 0xFF);
			code = new byte[codeLength];
			for(int i = 0; i < codeLength; i++) {
				code[i] = (byte)stream.read();
			}
			handlerCount = (int)(stream.read() & 0xFF << 8) | (int)(stream.read() & 0xFF);
			handlers = new TinyHandler[handlerCount];
			for(int i = 0; i < handlerCount; i++) {
				handlers[i] = new TinyHandler();
				handlers[i].read(stream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getMethodDescriptor() {
		return methodDescriptor;
	}

	public int getMaxStack() {
		return maxStack;
	}

	public int getMaxLocals() {
		return maxLocals;
	}

	public int getCodeLength() {
		return codeLength;
	}

	public byte[] getCode() {
		return code;
	}

	public int getHandlerCount() {
		return handlerCount;
	}

	public TinyHandler[] getHandlers() {
		return handlers;
	}
	
	public TinyClass getTinyClass() {
		return tinyClass;
	}
	
	
}
