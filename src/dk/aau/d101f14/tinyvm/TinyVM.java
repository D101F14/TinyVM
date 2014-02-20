package dk.aau.d101f14.tinyvm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Stack;

import dk.aau.d101f14.tinyvm.instructions.*;

public class TinyVM {
	
	boolean debug;
	
	int codePointer;
	Stack<Integer> operandStack;
	Instruction[] callStack;
		
	public TinyVM(String fileString) {
		codePointer = 0;
		operandStack = new Stack<Integer>();
		load(fileString);
		debug = false;
	}
	
	public void execute() {
		callStack[codePointer].execute();
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
	
	public Instruction getCurrentInstruction() {
		return callStack[codePointer];
	}
	
	public Instruction[] getCallStack() {
		return callStack;
	}
	
	public Stack<Integer> getOperandStack() {
		return operandStack;
	}
	
	public void load(String fileString) {
		try {
			FileInputStream	stream = new FileInputStream(fileString);
			int numInstructions = stream.read() << 8 | stream.read();
			callStack = new Instruction[numInstructions];
			int index = 0;
			while(index < numInstructions) {
				Instruction instruction = null;
				OpCode opcode = OpCode.get((byte)stream.read());
				switch(opcode) {
					case NOP:
						instruction = new NopInstruction(this);
						break;
					case PUSH:
						instruction = new PushInstruction(this);
						break;
					case POP:
						instruction = new PopInstruction(this);
						break;
					case LOAD:
						instruction = new LoadInstruction(this);
						break;
					case STORE:
						instruction = new StoreInstruction(this);
						break;
					case GOTO:
						instruction = new GotoInstruction(this);
						break;
					case IF:
						instruction = new IfInstruction(this);
						break;
					case COMP:
						instruction = new CompInstruction(this);
						break;
					case RETURN:
						instruction = new ReturnInstruction(this);
						break;
				}
				instruction.read(stream);
				callStack[index] = instruction;
				index++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		TinyVM tinyVM = new TinyVM(args[0]);
		tinyVM.setDebug(true);
		while(tinyVM.getCodePointer() < tinyVM.getCallStack().length) {
			if(tinyVM.getDebug()) {
				System.out.print(tinyVM.getCodePointer() + ":\t");
			}
			tinyVM.execute();
		}
	}
}
