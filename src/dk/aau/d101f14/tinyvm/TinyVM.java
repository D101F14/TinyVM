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
		if(heapCounter == heap.length - 1) {
			heap[heapCounter] = new TinyObject(classes.get("OutOfMemoryException"));
			throwException(heapCounter);
		}
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
	
	public void throwException(int exception) {
		ClassNameInfo exceptionNameInfo = (ClassNameInfo)heap[exception].tinyClass.constantPool[heap[exception].tinyClass.thisRef];
		String exceptionName = ((StringInfo)heap[exception].tinyClass.constantPool[exceptionNameInfo.getClassName()]).getBytesString();
		if(getCurrentFrame().method.handlerCount > 0) {
			for(int i = 0; i < getCurrentFrame().method.handlerCount; i++) {
				TinyHandler handler = getCurrentFrame().method.handlers[i];
				TypeInfo typeInfo = (TypeInfo)getCurrentFrame().method.tinyClass.getConstantPool()[handler.type];
				if(((StringInfo)getCurrentFrame().method.tinyClass.getConstantPool()[typeInfo.getClassName()]).getBytesString().equals(exceptionName)) {
					if(handler.startPc <= getCurrentFrame().getCodePointer() && handler.endPc >= getCurrentFrame().getCodePointer()) {
						getCurrentFrame().setCodePointer(handler.startPc);
						getCurrentFrame().getOperandStack().clear();
						getCurrentFrame().getOperandStack().push(exception);
						return;
					}
				}
			}
		}
		
		callStack.pop();
		if(!callStack.empty()) {
			throwException(exception);
			return;
		} else {
			System.out.println("Unhandled exception: " + exceptionName + ".");
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		TinyVM tinyVm = new TinyVM();
		tinyVm.rootDirectory = args[0].substring(0, args[0].lastIndexOf("/"));
		tinyVm.setDebug(true);
		String className = args[0].substring(args[0].lastIndexOf("/") + 1);
		tinyVm.loadList.add("Exception");
		tinyVm.loadList.add("OutOfMemoryException");
		tinyVm.loadList.add("NullReferenceException");
		tinyVm.loadList.add("DivisionByZeroException");
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
