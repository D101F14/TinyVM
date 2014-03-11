package dk.aau.d101f14.tinyvm.instructions;

import java.io.IOException;
import java.io.InputStream;

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
	public void read(InputStream stream) {
		try {
			operator = Operator.get((byte) stream.read());
			address1 = (byte) stream.read();
			address2 = (byte) stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		boolean expression = false;
		int value1 = tinyVM.getOperandStack().pop();
		int value2 = tinyVM.getOperandStack().pop();
		switch(operator) {
		case EQUALS:
			expression = value1 == value2;
			break;
		case NEQUALS:
			expression = value1 != value2;
			break;
		case GT:
			expression = value1 > value2;
			break;
		case LT:
			expression = value1 < value2;
			break;
		case AND:
			expression = (value1 != 0) && (value2 != 0);
			break;
		case OR:
			expression = (value1 != 0) || (value2 != 0);
			break;
		case XOR:
			expression = (value1 != 0) ^ (value2 != 0);
			break;
		default:
			break;
		}
		
		if(expression) {
			tinyVM.setCodePointer(getAddress());
		} else {
			tinyVM.setCodePointer(tinyVM.getCodePointer() + 1);
		}
		
		if(tinyVM.getDebug()) {
			System.out.println("IF\t" + getOperator() + "\t" + getAddress());
		}
	}

}
