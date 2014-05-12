package dk.aau.d101f14.tinyvm;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Stack;

import dk.aau.d101f14.tinyvm.instructions.*;

public class TinyFrame {
	TinyVM tinyVm;
	TinyMethod method;
	Checkpoint checkpoint;
	
	int[] localVariables;
	Stack<Integer> operandStack;
	int codePointer;
	HashMap<SimpleEntry<Integer, String>, Integer> localHeap;
	
	// Redundant elements
	int[] localVariablesR;
	Stack<Integer> operandStackR;
	int codePointerR;
	HashMap<SimpleEntry<Integer, String>, Integer> localHeapR;
	
	private int getRandomInt(){
		return (int)(Math.random()*100);
	}
	
	private int getRandomInt(int span){
		return (int)(Math.random()*span);
	}
	
	
	public TinyFrame(TinyVM tinyVm, int[] localVariables, TinyMethod method) {
		this.tinyVm = tinyVm;
		this.method = method;
		
		this.localVariables = localVariables.clone();
		operandStack = new Stack<Integer>();
		codePointer = 0;
		localHeap = new HashMap<SimpleEntry<Integer, String>, Integer>();
		
		localVariablesR = localVariables.clone();
		operandStackR = new Stack<Integer>();
		codePointerR = 0;
		localHeapR = new HashMap<SimpleEntry<Integer, String>, Integer>();
		
		checkpoint = new Checkpoint(localVariables.clone(), new Stack<Integer>(), 0);
	}
	
	public int[] getLocalVariables() {
		return localVariables;
	}

	public TinyMethod getMethod() {
		return method;
	}

	public Stack<Integer> getOperandStack() {
		return operandStack;
	}

	public int getCodePointer() {
		return codePointer;
	}

	public void incrementCodePointer(int i) {
		this.codePointer += i;
	}
	
	public void setCodePointer(int codePointer) {
		this.codePointer = codePointer;
	}
	
	public int getCodePointerR() {
		return codePointerR;
	}

	public void setCodePointerR(int codePointerR) {
		this.codePointerR = codePointerR;
	}

	public void incrementCodePointerR(int i) {
		this.codePointerR += i;
	}
	
	public int[] getLocalVariablesR() {
		return localVariablesR;
	}

	public Stack<Integer> getOperandStackR() {
		return operandStackR;
	}

	public HashMap<SimpleEntry<Integer, String>, Integer> getLocalHeap() {
		return localHeap;
	}

	public HashMap<SimpleEntry<Integer, String>, Integer> getLocalHeapR() {
		return localHeapR;
	}
	
	public Checkpoint getCheckpoint() {
		return checkpoint;
	}
	
	public void commitLocalHeap() {
		for(SimpleEntry<Integer, String> objectField : localHeap.keySet()) {
			tinyVm.getHeap()[objectField.getKey()].fields.put(objectField.getValue(), localHeap.get(objectField));
		}
		
		localHeap.clear();
		localHeapR.clear();
	}
	
	public void rollback() {
		System.out.println("ROLLBACK!");
		localHeap.clear();
		localHeapR.clear();
		
		operandStack.clear();
		operandStackR.clear();
		
		for(int i = 0; i < checkpoint.operandStack.size(); i++)	{
			operandStack.add(new Integer(checkpoint.operandStack.get(i).intValue()));
			operandStackR.add(new Integer(checkpoint.operandStack.get(i).intValue()));
		}
		//operandStack = (Stack<Integer>)checkpoint.getOperandStack().clone();
		//operandStackR = (Stack<Integer>)checkpoint.getOperandStack().clone();
		
		localVariables = checkpoint.getLocalVariables().clone();
		localVariablesR = checkpoint.getLocalVariables().clone();
		
		codePointer = checkpoint.getCodePointer();
		codePointerR = checkpoint.getCodePointer();
	}
	
	public boolean checkFrame() {
		boolean validOperandStack = true;
		boolean validLocalVariables = true;
		boolean validLocalHeap = true;
		boolean validCodePointer = codePointer == codePointerR;

		for(int i = 0; i < operandStack.size(); i++) {
			if(operandStack.get(i).intValue() != operandStackR.get(i).intValue()) validOperandStack = false;
		}
		
		for(int i = 0; i < localVariables.length; i++) {
			if(localVariables[i] != localVariablesR[i]) validLocalVariables = false;
		}
		
		if(localHeap.entrySet().size() == localHeapR.entrySet().size())
		{
			for(int i = 0; i < localHeap.entrySet().size(); i++) {
				if(!localHeap.entrySet().toArray()[i].equals(localHeapR.entrySet().toArray()[i])) validLocalHeap = false;
					
			}
		}
		else
		{
			validLocalHeap = false;
		}
		
		return validOperandStack && validLocalVariables && validLocalHeap && validCodePointer;
	}
	
	public void execute() {
		Instruction instruction = null;
		OpCode opCode = OpCode.get(method.getCode()[codePointer]);
		switch(opCode) {
		case DUP:
			instruction = new DupInstruction(tinyVm);
			break;
		case COMP:
			instruction = new CompInstruction(tinyVm);
			break;
		case GETFIELD:
			instruction = new GetFieldInstruction(tinyVm);
			break;
		case GOTO:
			instruction = new GotoInstruction(tinyVm);
			break;
		case IF:
			instruction = new IfInstruction(tinyVm);
			break;
		case INVOKEVIRTUAL:
			instruction = new InvokeVirtualInstruction(tinyVm);
			break;
		case INVOKENATIVE:
			instruction = new InvokeNativeInstruction(tinyVm);
			break;
		case LOAD:
			instruction = new LoadInstruction(tinyVm);
			break;
		case NEW:
			instruction = new NewInstruction(tinyVm);
			break;
		case NOP:
			instruction = new NopInstruction(tinyVm);
			break;
		case POP:
			instruction = new PopInstruction(tinyVm);
			break;
		case PUSH:
			instruction = new PushInstruction(tinyVm);
			break;
		case PUTFIELD:
			instruction = new PutFieldInstruction(tinyVm);
			break;
		case RETURN:
			instruction = new ReturnInstruction(tinyVm);
			break;
		case STORE:
			instruction = new StoreInstruction(tinyVm);
			break;
		case THROW:
			instruction = new ThrowInstruction(tinyVm);
			break;
		default:
			rollback();
			return;
		}
		instruction.read(method.getCode(), codePointer);
		instruction.execute();
	}
}
