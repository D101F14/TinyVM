package dk.aau.d101f14.tinyvm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class TinyVM {
	boolean debug;
	
	HashMap<String, TinyClass> classes;
	ArrayList<String> loadList;
	String rootDirectory;
	Stack<TinyFrame> callStack;
	TinyObject[] heap;
	int heapCounter;
	
	public TinyVM() {
		heap = new TinyObject[1024];
		classes = new HashMap<String, TinyClass>();
		loadList = new ArrayList<String>();
		callStack = new Stack<TinyFrame>();
		debug = false;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean getDebug() {
		return debug;
	}
	
	public Stack<TinyFrame> getCallStack() {
		return callStack;
	}
	
	public TinyFrame getCurrentFrame() {
		return callStack.peek();
	}
	
	public TinyObject[] getHeap() {
		return heap;
	}
	
	public int getHeapCounter() {
		return heapCounter;
	}
	
	public void incrementHeapCounter() {
		heapCounter++;
	}
	
	public HashMap<String, TinyClass> getClasses() {
		return classes;
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
			TinyClass tinyClass = new TinyClass(this);
			tinyClass.read(stream);
			classes.put(className, tinyClass);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TinyVM tinyVm = new TinyVM();
		tinyVm.rootDirectory = args[0].substring(0, args[0].lastIndexOf("/"));
		tinyVm.setDebug(true);
		String className = args[0].substring(args[0].lastIndexOf("/") + 1);
		tinyVm.loadList.add(className);
		for(int i = 0; i < tinyVm.loadList.size(); i++) {
			tinyVm.load(tinyVm.loadList.get(i));
		}
		
		for(TinyClass tinyClass : tinyVm.classes.values()) {
			if(tinyClass.getSuperRef() != null) {
				ClassNameInfo superClassNameInfo = (ClassNameInfo)tinyClass.getConstantPool()[tinyClass.getSuperRef()];
				StringInfo superClassName = (StringInfo)tinyClass.getConstantPool()[superClassNameInfo.getClassName()];
				tinyClass.setSuperClass(tinyVm.classes.get(superClassName.getBytesString()));
			}
		}
		
		tinyVm.getHeap()[tinyVm.getHeapCounter()] = new TinyObject(tinyVm.getClasses().get(className));
		tinyVm.incrementHeapCounter();
		
		TinyMethod mainMethod = tinyVm.getClasses().get(className).getMethods().get("main()");
		int[] localVariables = new int[mainMethod.getMaxLocals()];
		tinyVm.getCallStack().push(new TinyFrame(tinyVm, localVariables, mainMethod));
		
		while(!tinyVm.getCallStack().isEmpty()) {
			tinyVm.getCurrentFrame().execute();
		}
	}
}
