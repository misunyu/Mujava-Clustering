package MSG;

public class MSGConstraints {

	public static final int TRUE = 20;
	public static final int FALSE = 21;
	public static final int LHS = 22;
	public static final int RHS = 23;
	
	public static final int B_BITAND = 15;
	public static final int B_BITOR = 17;
	public static final int B_DIVIDE = 1;
	public static final int B_EQUAL = 13;
	public static final int B_GREATER = 9;
	public static final int B_GREATEREQUAL = 11;
	public static final int B_INSTANCEOF = 12;
	public static final int B_LESS = 8;
	public static final int B_LESSEQUAL = 10;
	public static final int B_LOGICAL_AND = 18;
	public static final int B_LOGICAL_OR = 19;
	public static final int B_MINUS = 4;
	public static final int B_MOD = 2;
	public static final int B_NOTEQUAL = 14;
	public static final int B_PLUS = 3;
	public static final int B_SHIFT_L = 5;
	public static final int B_SHIFT_R = 6;
	public static final int B_SHIFT_RR = 7;
	public static final int B_TIMES = 0;
	public static final int B_XOR = 16;

	public static final int U_POST_INCREMENT = 0;
	public static final int U_POST_DECREMENT = 1;
	public static final int U_PRE_INCREMENT = 2;
	public static final int U_PRE_DECREMENT = 3;
	public static final int U_BIT_NOT = 4;
	public static final int U_NOT = 5;
	public static final int U_PLUS = 6;
	public static final int U_MINUS = 7;

	public static final int A_EQUALS = 0;
	public static final int A_MULT = 1;
	public static final int A_DIVIDE = 2;
	public static final int A_MOD = 3;
	public static final int A_ADD = 4;
	public static final int A_SUB = 5;
	public static final int A_SHIFT_L = 6;
	public static final int A_SHIFT_R = 7;
	public static final int A_SHIFT_RR = 8;
	public static final int A_AND = 9;
	public static final int A_XOR = 10;
	public static final int A_OR = 11;

	public static final String header_mutantID = "MSG.mutantID";
	public static final String header_MSG_TARGET_CLASS = "MSG.target.class";
}
