package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Operator;
import dk.aau.d101f14.tinyvm.TinyObject;
import dk.aau.d101f14.tinyvm.TinyVM;

public class CompInstruction extends Instruction {

	Operator operator;
	
	public CompInstruction(TinyVM tinyVm) {
		super(tinyVm, OpCode.COMP);
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	@Override
	public void read(byte[] code, int opCodeIndex) {
		operator = Operator.get(code[opCodeIndex + 1]);
	}

	@Override
	public void execute() {
		int result = 0;
		int value1 = tinyVm.getCurrentFrame().getOperandStack().pop();
		int value2 = tinyVm.getCurrentFrame().getOperandStack().peek();
		switch(operator) {
		case EQUALS:
			result = (value1 == value2) ? 1 : 0;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case NEQUALS:
			result = (value1 != value2) ? 1 : 0;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case GT:
			result = (value1 > value2) ? 1 : 0;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case LT:
			result = (value1 < value2) ? 1 : 0;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case NEG:
			result = -value1;
			break;
		case ADD:
			result = value1 + value2;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case SUB:
			result = value1 - value2;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case MUL:
			result = value1 * value2;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case DIV:
			if(value2 != 0) {
				result = value1 / value2;
				tinyVm.getCurrentFrame().getOperandStack().pop();
			} else {
				tinyVm.getHeap()[tinyVm.getHeapCounter()] = new TinyObject(tinyVm.getClasses().get("DivisionByZeroException"));
				tinyVm.incrementHeapCounter();
				tinyVm.throwException(tinyVm.getHeapCounter() - 1);
			}
			break;
		case MOD:
			if(value2 != 0) {
				result = value1 % value2;
				tinyVm.getCurrentFrame().getOperandStack().pop();
			} else {
				tinyVm.getHeap()[tinyVm.getHeapCounter()] = new TinyObject(tinyVm.getClasses().get("DivisionByZeroException"));
				tinyVm.incrementHeapCounter();
				tinyVm.throwException(tinyVm.getHeapCounter() - 1);
			}
			break;
		case SHL:
			result = value1 << value2;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case SHR:
			result = value1 >> value2;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case USHR:
			result = value1 >>> value2;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case AND:
			result = (value1 != 0) && (value2 != 0) ? 1 : 0;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case OR:
			result = (value1 != 0) || (value2 != 0) ? 1 : 0;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case XOR:
			result = (value1 != 0) ^ (value2 != 0) ? 1 : 0;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		}
		
		// Push result onto operand stack
		tinyVm.getCurrentFrame().getOperandStack().push(result);
		
		// Increment code pointer
		tinyVm.getCurrentFrame().incrementCodePointer(2);
		
		if(tinyVm.getDebug()) {
			System.out.println("COMP\t" + getOperator());
		}
	}
}
