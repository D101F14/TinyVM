package dk.aau.d101f14.tinyvm;

import java.util.Stack;

import dk.aau.d101f14.tinyvm.instructions.*;

public class TinyFrame {
	TinyVM tinyVm;
	int[] localVariables;
	TinyMethod method;
	Stack<Integer> operandStack;
	int codePointer;
	
	public TinyFrame(TinyVM tinyVm, int[] localVariables, TinyMethod method) {
		this.tinyVm = tinyVm;
		this.localVariables = localVariables;
		this.method = method;
		operandStack = new Stack<Integer>();
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
		default:
			break;
		}
		instruction.read(method.getCode(), codePointer);
		instruction.execute();
	}
}
