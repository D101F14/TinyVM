package dk.aau.d101f14.tinyvm;

import java.util.Stack;

import dk.aau.d101f14.tinyvm.instructions.*;

public class TinyFrame {
	TinyVM tinyVm;
	TinyMethod method;
	
	
	int[] localVariables;
	Stack<Integer> operandStack;
	int codePointer;
	
	// Redundant elements
	int[] localVariablesR;
	Stack<Integer> operandStackR;
	int codePointerR;
	
	public TinyFrame(TinyVM tinyVm, int[] localVariables, TinyMethod method) {
		this.tinyVm = tinyVm;
		this.method = method;
		
		this.localVariables = localVariables;
		operandStack = new Stack<Integer>();
		codePointer = 0;
		
		localVariablesR = localVariables;
		operandStackR = new Stack<Integer>();
		codePointerR = 0;
		
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
			break;
		}
		instruction.read(method.getCode(), codePointer);
		instruction.execute();
	}
}
