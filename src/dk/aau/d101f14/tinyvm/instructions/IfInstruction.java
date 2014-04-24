package dk.aau.d101f14.tinyvm.instructions;

import dk.aau.d101f14.tinyvm.OpCode;
import dk.aau.d101f14.tinyvm.Operator;
import dk.aau.d101f14.tinyvm.TinyVM;

public class IfInstruction extends Instruction {

	Operator operator;
	byte address1;
	byte address2;
	
	public IfInstruction(TinyVM tinyVM) {
		super(tinyVM, OpCode.IF);
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public int getAddress() {
		return address1 << 8 | address2;
	}
	
	@Override
	public void read(byte[] code, int opCodeIndex) {
		operator = Operator.get(code[opCodeIndex + 1]);
		address1 = code[opCodeIndex + 2];
		address2 = code[opCodeIndex + 3];
	}

	@Override
	public void execute() {
		boolean expression = false;
		boolean expressionR = false;
		
		int value1 = tinyVm.getCurrentFrame().getOperandStack().pop();
		int value2 = tinyVm.getCurrentFrame().getOperandStack().pop();
		
		int value1R = tinyVm.getCurrentFrame().getOperandStackR().pop();
		int value2R = tinyVm.getCurrentFrame().getOperandStackR().pop();
		
		switch(operator) {
		case EQUALS:
			expression = value1 == value2;
			expressionR = value1R == value2R;
			break;
		case NEQUALS:
			expression = value1 != value2;
			expressionR = value1R != value2R;
			break;
		case GT:
			expression = value1 > value2;
			expressionR = value1R > value2R;
			break;
		case LT:
			expression = value1 < value2;
			expressionR = value1R < value2R;
			break;
		case AND:
			expression = (value1 != 0) && (value2 != 0);
			expressionR = (value1R != 0) && (value2R != 0);
			break;
		case OR:
			expression = (value1 != 0) || (value2 != 0);
			expressionR = (value1R != 0) || (value2R != 0);
			break;
		case XOR:
			expression = (value1 != 0) ^ (value2 != 0);
			expressionR = (value1R != 0) ^ (value2R != 0);
			break;
		}
		
		if(expression) {
			tinyVm.getCurrentFrame().setCodePointer(getAddress());
		} else {
			tinyVm.getCurrentFrame().incrementCodePointer(4);
		}
		
		if(expressionR) {
			tinyVm.getCurrentFrame().setCodePointerR(getAddress());
		} else {
			tinyVm.getCurrentFrame().incrementCodePointerR(4);
		}
		
		if(tinyVm.getDebug()) {
			System.out.println("IF\t" + getOperator() + "\t" + getAddress());
		}
	}

}
