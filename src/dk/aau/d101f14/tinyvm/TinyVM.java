package dk.aau.d101f14.tinyvm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import dk.aau.d101f14.tinyvm.instructions.*;

public class TinyVM {
	
	boolean debug;
	
	int codePointer;
	Stack<Integer> operandStack;
	HashMap<String, TinyClass> classes;
	ArrayList<String> loadList;
	String rootDirectory;
	
	public TinyVM() {
		codePointer = 0;
		operandStack = new Stack<Integer>();
		classes = new HashMap<String, TinyClass>();
		loadList = new ArrayList<String>();
		debug = false;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean getDebug() {
		return debug;
	}
	
	public int getCodePointer() {
		return codePointer;
	}
	
	public void setCodePointer(int address) {
		codePointer = address;
	}
	
	public Stack<Integer> getOperandStack() {
		return operandStack;
	}
	
	public void load(String className) {
		try {
			FileInputStream	stream = new FileInputStream(rootDirectory + "/" + className + ".tclass");
			byte[] cafebabe = new byte[4];
			stream.read(cafebabe);
			ByteBuffer bbuffer = ByteBuffer.wrap(cafebabe);
			if(bbuffer.getInt() != 0xCAFEBABE) {
				System.err.println("Invalid TinyClass file.");
				System.exit(1);
			}
			TinyClass tinyClass = new TinyClass();
			tinyClass.read(stream);
			classes.put(className, tinyClass);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		TinyVM tinyVM = new TinyVM();
		tinyVM.rootDirectory = args[0].substring(0, args[0].lastIndexOf("/"));
		tinyVM.setDebug(true);
		tinyVM.loadList.add(args[0].substring(args[0].lastIndexOf("/") + 1));
		while(!tinyVM.loadList.isEmpty()) {
			tinyVM.load(tinyVM.loadList.remove(0));
		}
	}
}
