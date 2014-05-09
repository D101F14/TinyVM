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
			switch (exceptionName)
			{
			case "NullReferenceException":
				System.exit(2);
				break;
			case "OutOfMemoryException":
				System.exit(3);
				break;
			case "DivisionByZeroException":
				System.exit(4);
				break;
			default:
				System.exit(1);
				break;
			}
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
		
		while(!tinyVm.getCallStack().isEmpty()) {
			tinyVm.getCurrentFrame().execute();
		}
	}
	
	
	private void flipOperandStack(){
		
		TinyFrame current = this.getCallStack().get((int)(Math.random() * (this.getCallStack().size()-1)));
		
		if(current.operandStack.size() > 0)
		{
			int element = (int)(Math.random() * (current.operandStack.size()-1));
			int bit = (int)(Math.random() * 7);
			int val = current.operandStack.get(element).intValue();
			int temp = val;
			val ^= 1 << bit;
			current.operandStack.set(element, new Integer(val));
			System.out.println("Element "+ element + " was changed from " + temp + " to " + val + " on the OS. Bit " + bit + " was flipped.");
		}
	}
	
	private void flipOperandStack(int frameToFlip,int elementToFlip,int value){
		
		TinyFrame current = this.getCallStack().get(frameToFlip);
		
		if(current.operandStack.size() > 0)
		{
			int val = current.operandStack.get(elementToFlip).intValue();
			current.operandStack.set(elementToFlip, new Integer(value));
			System.out.println("Element "+ elementToFlip + " was changed from " + val + " to " + value + " on the OS.");
		}
	}
	
	private void flipOperandStackR(){
		TinyFrame current = this.getCallStack().get((int)(Math.random() * (this.getCallStack().size()-1)));
		if(current.operandStackR.size() > 0)
		{
			int element = (int)(Math.random() * (current.operandStackR.size()-1));
			int bit = (int)(Math.random() * 7);
			int val = current.operandStackR.get(element).intValue();
			int temp = val;
			val ^= 1 << bit;
			current.operandStackR.set(element, new Integer(val));
			System.out.println("Element "+ element + " was changed from " + temp + " to " + val + " on the OS_R. Bit " + bit + " was flipped.");
		}
	}
	
	private void flipOperandStackR(int frameToFlip,int elementToFlip,int value){
		
		TinyFrame current = this.getCallStack().get(frameToFlip);
		
		if(current.operandStackR.size() > 0)
		{
			int val = current.operandStackR.get(elementToFlip).intValue();
			current.operandStackR.set(elementToFlip, new Integer(value));
			System.out.println("Element "+ elementToFlip + " was changed from " + val + " to " + value + " on the OS.");
		}
	}
	
	private void flipProgramCounter(){
		TinyFrame current = this.getCallStack().get((int)(Math.random() * (this.getCallStack().size()-1)));
		int bit = (int)(Math.random()*15);
		int pc = current.getCodePointer();
		int temp = pc;
		pc ^= 1 << bit;
		current.setCodePointer(pc);
		System.out.println("Program Counter was flipped from " + temp + " to " + pc);
	}
	
	private void flipProgramCounter(int frameToFlip, int value){
		TinyFrame current = this.getCallStack().get(frameToFlip);
		
		int pc = current.getCodePointer();
		current.setCodePointer(value);
		System.out.println("Program Counter was flipped from " + pc + " to " + value);
	}
	
	private void flipProgramCounterR(){
		TinyFrame current = this.getCallStack().get((int)(Math.random() * (this.getCallStack().size()-1)));
		int bit = (int)(Math.random()*15);
		int pcR = current.getCodePointerR();
		int temp = pcR;
		pcR ^= 1 << bit;
		current.setCodePointerR(pcR);
		System.out.println("Program Counter R was flipped from " + temp + " to " + pcR);
	}
	
	private void flipProgramCounterR(int frameToFlip, int value){
		TinyFrame current = this.getCallStack().get(frameToFlip);
		int pcR = current.getCodePointerR();
		current.setCodePointerR(value);
		System.out.println("Program Counter R was flipped from " + pcR + " to " + value);
	}
	
	private void flipLocalHeap(){
		TinyFrame current = this.getCallStack().get((int)(Math.random() * (this.getCallStack().size()-1)));
		
		if(current.getLocalHeap().entrySet().size() > 0)
		{
			int mode = (int)(Math.random() * 2);
			int bit = (int)(Math.random()*15);
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
	}
	
	private void flipLocalHeap(int frameToFlip, int flipMode,int elementToFlip, int value, String strValue){
		TinyFrame current = this.getCallStack().get(frameToFlip);
		
		if(current.getLocalHeap().entrySet().size() > 0)
		{
			Entry<SimpleEntry<Integer, String>, Integer> entryElement = null;
			int i = 0;
			for(Entry<SimpleEntry<Integer, String>, Integer> entry : current.getLocalHeap().entrySet())
			{
				if(i == elementToFlip)
				{
					entryElement = entry;
					break;
				}
				++i;
			}
			
			SimpleEntry<Integer,String> key = entryElement.getKey();
			
			switch(flipMode)
			{
			case 0: //flip reference
				current.getLocalHeap().remove(key);
				current.getLocalHeap().put(new SimpleEntry<Integer,String>(new Integer(value),key.getValue()), new Integer(entryElement.getValue().intValue()));
				System.out.println("Local Heap entry reference was flipped in " + entryElement.toString() + " to " + value+"="+key.getValue()+"="+entryElement.getValue().intValue());
				break;
			case 1: //flip field name				
				current.getLocalHeap().remove(key);
				current.getLocalHeap().put(new SimpleEntry<Integer,String>(key.getKey(),strValue), new Integer(entryElement.getValue().intValue()));
				System.out.println("Local Heap entry fieldname was flipped in " + entryElement.toString() + " to " + key.getKey()+"="+strValue+"="+entryElement.getValue().intValue());
				break;
			case 2: //flip value
				current.getLocalHeap().remove(key);
				current.getLocalHeap().put(key, new Integer(value));
				System.out.println("Local Heap entry value was flipped in " + entryElement.toString() + " to " + current.getLocalHeap().get(key).toString());
				break;
			default:
				break;
			}
		}
	}
	
	private void flipLocalHeapR(){
		TinyFrame current = this.getCallStack().get((int)(Math.random() * (this.getCallStack().size()-1)));
		
		if(current.getLocalHeapR().entrySet().size() > 0)
		{
			int mode = (int)(Math.random() * 2);
			int bit = (int)(Math.random()*15);
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
				System.out.println("Local Heap R entry reference was flipped in " + entryElement.toString() + " to " + newKeyInt);
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
				System.out.println("Local Heap R entry fieldname was flipped in " + entryElement.toString() + " to " + newStringKey);
				break;
			case 2: //flip value
				int val = current.getLocalHeapR().get(key).intValue();
				val ^= 1 << bit;
				current.getLocalHeapR().remove(key);
				current.getLocalHeapR().put(key, new Integer(val));
				System.out.println("Local Heap entry value was flipped in " + entryElement.toString() + " to " + current.getLocalHeap().get(key).toString());
				break;
			default:
				break;
			}
		}
	}
	
	private void flipLocalHeapR(int frameToFlip, int flipMode,int elementToFlip, int value, String strValue){
		TinyFrame current = this.getCallStack().get(frameToFlip);
		
		if(current.getLocalHeapR().entrySet().size() > 0)
		{
			Entry<SimpleEntry<Integer, String>, Integer> entryElement = null;
			int i = 0;
			for(Entry<SimpleEntry<Integer, String>, Integer> entry : current.getLocalHeapR().entrySet())
			{
				if(i == elementToFlip)
				{
					entryElement = entry;
					break;
				}
				++i;
			}
			
			SimpleEntry<Integer,String> key = entryElement.getKey();
			
			switch(flipMode)
			{
			case 0: //flip reference
				current.getLocalHeapR().remove(key);
				current.getLocalHeapR().put(new SimpleEntry<Integer,String>(new Integer(value),key.getValue()), new Integer(entryElement.getValue().intValue()));
				System.out.println("Local Heap R entry reference was flipped in " + entryElement.toString() + " to " + value+"="+key.getValue()+"="+entryElement.getValue().intValue());
				break;
			case 1: //flip field name				
				current.getLocalHeapR().remove(key);
				current.getLocalHeapR().put(new SimpleEntry<Integer,String>(key.getKey(),strValue), new Integer(entryElement.getValue().intValue()));
				System.out.println("Local Heap R entry fieldname was flipped in " + entryElement.toString() + " to " + key.getKey()+"="+strValue+"="+entryElement.getValue().intValue());
				break;
			case 2: //flip value
				current.getLocalHeapR().remove(key);
				current.getLocalHeapR().put(key, new Integer(value));
				System.out.println("Local Heap R entry value was flipped in " + entryElement.toString() + " to " + current.getLocalHeapR().get(key).toString());
				break;
			default:
				break;
			}
		}
	}
	
	private void flipLocalVariables(){
		TinyFrame current = this.getCallStack().get((int)(Math.random() * (this.getCallStack().size()-1)));
		
		if(current.getLocalVariables().length > 0)
		{
			int element = (int)(Math.random()*(current.getLocalVariables().length-1));
			int bit = (int)(Math.random()*15);
			int val = current.getLocalVariables()[element];
			int temp = val;
			val ^= 1 << bit;
			current.getLocalVariables()[element] = val;
			System.out.println("Local Variables index " + element  + " was flipped from " + temp + " to " + val);
		}
	}
	
	private void flipLocalVariables(int frameToFlip, int elementToFlip, int value){
		TinyFrame current = this.getCallStack().get(frameToFlip);
		
		if(current.getLocalVariables().length > 0)
		{
			int val = current.getLocalVariables()[elementToFlip];
			current.getLocalVariables()[elementToFlip] = value;
			System.out.println("Local Variables index " + elementToFlip  + " was flipped from " + val + " to " + value);
		}
	}
	
	private void flipLocalVariablesR(){
		TinyFrame current = this.getCallStack().get((int)(Math.random() * (this.getCallStack().size()-1)));
		
		if(current.getLocalVariablesR().length > 0)
		{
			int element = (int)(Math.random()*(current.getLocalVariablesR().length-1));
			int bit = (int)(Math.random()*15);
			int val = current.getLocalVariablesR()[element];
			int temp = val;
			val ^= 1 << bit;
			current.getLocalVariablesR()[element] = val;
			System.out.println("Local Variables R index " + element  + " was flipped from " + temp + " to " + val);
		}
	}
	
	private void flipLocalVariablesR(int frameToFlip, int elementToFlip, int value){
		TinyFrame current = this.getCallStack().get(frameToFlip);
		
		if(current.getLocalVariablesR().length > 0)
		{

			int val = current.getLocalVariablesR()[elementToFlip];
			current.getLocalVariablesR()[elementToFlip] = value;
			System.out.println("Local Variables R index " + elementToFlip  + " was flipped from " + val + " to " + value);
		}
	}
	
}
