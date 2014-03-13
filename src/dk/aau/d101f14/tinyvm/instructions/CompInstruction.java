package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Operator;
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
	public void read(InputStream stream) {
		try {
			operator = Operator.get((byte) stream.read());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			result = value1 / value2;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case MOD:
			result = value1 % value2;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case SHL:
			result = value1 << value2;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		case SHR:
			result = value1 >> value2;
			tinyVm.getCurrentFrame().getOperandStack().pop();
			break;
		//case USHL:
		//	result = value1 << value2;
		//	tinyVm.getCurrentFrame().getOperandStack().pop();
		//	break;
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
		tinyVm.getCurrentFrame().setCodePointer(tinyVm.getCurrentFrame().getCodePointer() + 1);
		
		if(tinyVm.getDebug()) {
			System.out.println("COMP\t" + getOperator());
		}
	}
}
