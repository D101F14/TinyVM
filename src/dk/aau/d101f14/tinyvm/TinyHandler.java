package dk.aau.d101f14.tinyvm;

import java.io.IOException;
import java.io.InputStream;

public class TinyHandler {
	int startPc;
	int endPc;
	int handlerPc;
	int type;
	
	public void read(InputStream stream) {
		try {
			startPc = stream.read() << 8 | stream.read();
			endPc = stream.read() << 8 | stream.read();
			handlerPc = stream.read() << 8 | stream.read();
			type = stream.read() << 8 | stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
