package dk.aau.d101f14.tinyvm;

import java.util.Stack;

public class TinyFrame {
	int[] localVariables;
	TinyMethod method;
	Stack<Integer> operandStack;
	int codePointer;
	
	public TinyFrame(int[] localVariables, TinyMethod method) {
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

	public void setCodePointer(int codePointer) {
		this.codePointer = codePointer;
	}
	
	
}
