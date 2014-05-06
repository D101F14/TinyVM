package dk.aau.d101f14.tinyvm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

public class TinyVM {
	boolean debug;
	
	HashMap<String, TinyClass> classes;
	ArrayList<String> loadList;
	Path rootDirectory;
	Stack<TinyFrame> callStack;
	TinyObject[] heap;
	int heapCounter;
	TinyNativeInterface tni;
	
	public TinyVM() {
		heap = new TinyObject[1024];
		classes = new HashMap<String, TinyClass>();
		loadList = new ArrayList<String>();
		callStack = new Stack<TinyFrame>();
		debug = false;
		tni = new TinyNativeInterface();
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean getDebug() {
		return debug;
	}
	
	public Path getRootDirectory() {
		return rootDirectory;
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
	
	public TinyNativeInterface getNativeInterface() {
		return tni;
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
						
						getCurrentFrame().setCodePointerR(handler.startPc);
						getCurrentFrame().getOperandStackR().clear();
						getCurrentFrame().getOperandStackR().push(exception);
						
						getCurrentFrame().getCheckpoint().update(getCurrentFrame().getLocalVariables().clone(), getCurrentFrame().getOperandStack(), handler.startPc);
						
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
		tinyVm.rootDirectory = Paths.get(args[0]).toAbsolutePath().getParent();
		tinyVm.setDebug(true);
		
		String className = Paths.get(args[0]).getFileName().toString();
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
		
		//boolean flipBit = false;
		//boolean testForFlip = true;
		
		while(!tinyVm.getCallStack().isEmpty()) {
			tinyVm.getCurrentFrame().execute();
			
			/*if(!flipBit && testForFlip) {
				if(Math.random() * 100 > 50) {
					flipBit=true;
					testForFlip = false;
				}
			}
			if(flipBit)	{
				if(tinyVm.getCurrentFrame().getLocalHeap().entrySet().size() > 0) {
					flipBit(tinyVm, 4);
					flipBit = false;
				}
			}*/
		}
	}
	
	private static void flipBit(TinyVM vm, int id){
		
		TinyFrame current = vm.getCurrentFrame();
		int bit = 0;
		int temp = 0;
		
		switch(id)
		{
		case 0: //Operand Stack
			if(current.operandStack.size() > 0)
			{
				int element = (int)(Math.random() * (current.operandStack.size()-1));
				bit = (int)(Math.random() * 7);
				int val = current.operandStack.get(element).intValue();
				temp = val;
				val ^= 1 << bit;
				current.operandStack.set(element, new Integer(val));
				System.out.println("Element "+ element + " was changed from " + temp + " to " + val + " on the OS. Bit " + bit + " was flipped.");
			}
			break;
		case 1: //Operand Stack R
			if(current.operandStackR.size() > 0)
			{
				int element = (int)(Math.random() * (current.operandStackR.size()-1));
				bit = (int)(Math.random() * 7);
				int val = current.operandStackR.get(element).intValue();
				temp = val;
				val ^= 1 << bit;
				current.operandStackR.set(element, new Integer(val));
				System.out.println("Element "+ element + " was changed from " + temp + " to " + val + " on the OS_R. Bit " + bit + " was flipped.");
			}
			break;
		case 2: //Program Counter
			bit = (int)(Math.random()*15);
			int pc = current.getCodePointer();
			temp = pc;
			pc ^= 1 << bit;
			current.setCodePointer(pc);
			System.out.println("Program Counter was flipped from " + temp + " to " + pc);
			break;
		case 3: //Program Counter R
			bit = (int)(Math.random()*15);
			int pcR = current.getCodePointerR();
			temp = pcR;
			pcR ^= 1 << bit;
			current.setCodePointerR(pcR);
			System.out.println("Program Counter R was flipped from " + temp + " to " + pcR);
			break;
		case 4: //Local Heap
			if(current.getLocalHeap().entrySet().size() > 0)
			{
				int mode = (int)(Math.random() * 2);
				bit = (int)(Math.random()*15);
				Entry<SimpleEntry<Integer, String>, Integer> entryElement = null;
				int element = (int)(Math.random()*(current.getLocalHeap().entrySet().size()-1));
				int i = 0;
				for(Entry<SimpleEntry<Integer, String>, Integer> entry : current.getLocalHeap().entrySet())
				{
					if(i == element)
					{
						entryElement = entry;
						break;
					}
					++i;
				}
				
				SimpleEntry<Integer,String> key = entryElement.getKey();
				
				switch(mode)
				{
				case 0: //flip reference
					Integer newKeyInt = new Integer(key.getKey().intValue());
					newKeyInt ^= 1 << bit;
					current.getLocalHeap().remove(key);
					current.getLocalHeap().put(new SimpleEntry<Integer,String>(newKeyInt,key.getValue()), new Integer(entryElement.getValue().intValue()));
					System.out.println("Local Heap entry reference was flipped in " + entryElement.toString() + " to " + newKeyInt);
					break;
				case 1: //flip field name
					bit = (int)(Math.random() * 7);
					byte[] byteString = key.getValue().getBytes();
					element = (int)(Math.random() * (byteString.length -1));
					
					byte character = byteString[element];
					character ^= 1 << bit;
					byteString[element] = character;
					
					String newStringKey = new String(byteString);
					
					current.getLocalHeap().remove(key);
					current.getLocalHeap().put(new SimpleEntry<Integer,String>(key.getKey(),newStringKey), new Integer(entryElement.getValue().intValue()));
					System.out.println("Local Heap entry fieldname was flipped in " + entryElement.toString() + " to " + newStringKey);
					break;
				case 2: //flip value
					int val = current.getLocalHeap().get(key).intValue();
					val ^= 1 << bit;
					current.getLocalHeap().remove(key);
					current.getLocalHeap().put(key, new Integer(val));
					System.out.println("Local Heap entry value was flipped in " + entryElement.toString() + " to " + current.getLocalHeap().get(key).toString());
					break;
				default:
					break;
				}
			}
			break;
		case 5: //Local Heap R
			if(current.getLocalHeapR().entrySet().size() > 0)
			{
				int mode = (int)(Math.random() * 2);
				bit = (int)(Math.random()*15);
				Entry<SimpleEntry<Integer, String>, Integer> entryElement = null;
				int element = (int)(Math.random()*(current.getLocalHeapR().entrySet().size()-1));
				int i = 0;
				for(Entry<SimpleEntry<Integer, String>, Integer> entry : current.getLocalHeapR().entrySet())
				{
					if(i == element)
					{
						entryElement = entry;
						break;
					}
					++i;
				}
				
				SimpleEntry<Integer,String> key = entryElement.getKey();
				
				switch(mode)
				{
				case 0: //flip reference
					Integer newKeyInt = new Integer(key.getKey().intValue());
					newKeyInt ^= 1 << bit;
					current.getLocalHeapR().remove(key);
					current.getLocalHeapR().put(new SimpleEntry<Integer,String>(newKeyInt,key.getValue()), new Integer(entryElement.getValue().intValue()));
					break;
				case 1: //flip field name
					bit = (int)(Math.random() * 7);
					byte[] byteString = key.getValue().getBytes();
					element = (int)(Math.random() * (byteString.length -1));
					
					byte character = byteString[element];
					character ^= 1 << bit;
					byteString[element] = character;
					
					String newStringKey = new String(byteString);
					
					current.getLocalHeapR().remove(key);
					current.getLocalHeapR().put(new SimpleEntry<Integer,String>(key.getKey(),newStringKey), new Integer(entryElement.getValue().intValue()));
					break;
				case 2: //flip value
					int val = current.getLocalHeapR().get(key).intValue();
					val ^= 1 << bit;
					current.getLocalHeapR().remove(key);
					current.getLocalHeapR().put(key, new Integer(val));
					break;
				default:
					break;
				}
			}
			break;
		case 6: //Local Variables
			if(current.getLocalVariables().length > 0)
			{
				int element = (int)(Math.random()*(current.getLocalVariables().length-1));
				bit = (int)(Math.random()*15);
				int val = current.getLocalVariables()[element];
				temp = val;
				val ^= 1 << bit;
				current.getLocalVariables()[element] = val;
				System.out.println("Local Variables index " + element  + " was flipped from " + temp + " to " + val);
			}
			break;
		case 7: //Local Variables R
			if(current.getLocalVariablesR().length > 0)
			{
				int element = (int)(Math.random()*(current.getLocalVariablesR().length-1));
				bit = (int)(Math.random()*15);
				int val = current.getLocalVariablesR()[element];
				temp = val;
				val ^= 1 << bit;
				current.getLocalVariablesR()[element] = val;
				System.out.println("Local Variables R index " + element  + " was flipped from " + temp + " to " + val);
			}
			break;
		default:
			return;
			
		}
	}
}
