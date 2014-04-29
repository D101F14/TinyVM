package dk.aau.d101f14.tinyvm;

import java.util.Stack;

public class Checkpoint {

	int[] localVariables;
	Stack<Integer> operandStack;
	int codePointer;
	
	public int[] getLocalVariables() {
		return localVariables;
	}

	public Stack<Integer> getOperandStack() {
		return operandStack;
	}
	
	public int getCodePointer() {
		return codePointer;
	}
	
	public Checkpoint(int[] localVariables, Stack<Integer> operandStack, int codePointer) {
		this.localVariables = localVariables;
		this.operandStack = operandStack;
		this.codePointer = codePointer;
	}
	
	public void update(int[] localVariables, Stack<Integer> operandStack, int codePointer) {
		this.localVariables = localVariables;
		this.operandStack = operandStack;
		this.codePointer = codePointer;
	}
	

}
