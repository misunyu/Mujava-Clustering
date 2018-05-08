package mujava;

import java.util.StringTokenizer;

import kaist.selab.util.MuJavaLogger;

public class MutantID implements Comparable<Object> {
	
	static int MUTATION_OP = 1;
	static int HASH_CODE = 2;
	static int CHANGE_POINT = 3;
	static int SUB_ID = 4;
	static String ID_SEPERATOR = "_";
	
	public static MutantID generateMutantID(String mutantOperatorName,
			int targetFileHashCode, String changePointID) {

		return new MutantID(mutantOperatorName, targetFileHashCode,
				changePointID);
	}

	public static String getChangePointID(String mutantID) {
		return getValue(mutantID,CHANGE_POINT);
		//return new MutantID(mutantID).getChangePointID();
	}

	public static  int getChangePoint(String mutantID) {
		return Integer.parseInt(getValue(mutantID,CHANGE_POINT));
		//return Integer.parseInt(new MutantID(mutantID).getChangePointID());
	}

	public static int getHashCode(String mutantID) {
		return Integer.parseInt(getValue(mutantID,HASH_CODE));
		//return new MutantID(mutantID).getHashCode();
	}

	public static String getMutationOperator(String mutantID) {
		/*String op = new MutantID(mutantID).getMutationOperator();
		MutantOperator mop = MutantOperator.getMutantOperator(op);
		return (mop == null) ? "" : op;*/
		String op = getValue(mutantID,MUTATION_OP);
		MutantOperator mop = MutantOperator.getMutantOperator(op);
		return (mop == null) ? "" : op;
		
	}

	public static String toStringWithoutIndex(String mutantID) {
		/*MutantID id = new MutantID(mutantID);
		id.index = -1;
		return id.toString();
		*/
		return new String(getValue(mutantID,MUTATION_OP) + ID_SEPERATOR
				+ getValue(mutantID,HASH_CODE) + ID_SEPERATOR + getValue(mutantID,CHANGE_POINT));

	}
		

	int hashCode;
	int index;
	String changePointID;
	String mutationOperator;

	public static String getValue(String mutantID,int type) {
		//if(type == MUTATION_OP)

		StringTokenizer st = new StringTokenizer(mutantID, ID_SEPERATOR);
		if(st.countTokens()!=4) return null;
		String temp_str;
		temp_str = st.nextToken();
		if(type==MUTATION_OP) return temp_str;
		temp_str = st.nextToken();
		if(type==HASH_CODE) return temp_str;
		temp_str = st.nextToken();
		if(type==CHANGE_POINT) return temp_str;
		temp_str = st.nextToken();
		if(type==SUB_ID) return temp_str;
		
		return null;
		
		/*if (st.hasMoreTokens())
			this.mutationOperator = st.nextToken();
		if (st.hasMoreTokens())
			this.hashCode = Integer.parseInt(st.nextToken());
		if (st.hasMoreTokens())
			this.changePointID = st.nextToken();
		if (st.hasMoreTokens())
			this.index = Integer.parseInt(st.nextToken());
			*/
	}
	
	
	public MutantID(String mutantID) {
		index = -1;
		this.mutationOperator = "";
		this.changePointID = "";
		this.hashCode = 0;

		StringTokenizer st = new StringTokenizer(mutantID, ID_SEPERATOR);
		if (st.hasMoreTokens())
			this.mutationOperator = st.nextToken();
		if (st.hasMoreTokens())
			this.hashCode = Integer.parseInt(st.nextToken());
		if (st.hasMoreTokens())
			this.changePointID = st.nextToken();
		if (st.hasMoreTokens())
			this.index = Integer.parseInt(st.nextToken());
	}

	private MutantID(String operator, int hashCode, String changePointID) {
		this.mutationOperator = operator;
		this.hashCode = hashCode;
		this.changePointID = changePointID;
		this.index = -1;
	}


	@Override
	public int compareTo(Object arg) {
		if (arg == null) {
			return -1;
		}
		if (!(arg instanceof MutantID)) {
			return -1;
		}

		MutantID target = (MutantID) arg;

		long result = this.mutationOperator.compareTo(target.mutationOperator);
		if (result != 0) {
			return (int) result;
		}
		result = this.hashCode - target.hashCode;
		if (result != 0) {
			return (int) result;
		}

		result = Integer.parseInt(this.changePointID)
				- Integer.parseInt(target.changePointID);
		if (result != 0) {
			return (int) result;
		}

		return this.index - target.index;
	}

//	private String getChangePointID() {
//		return this.changePointID;
//	}
	
	public int getChangePoint() {
		return Integer.parseInt(this.changePointID);
	}

//	private int getHashCode() {
//		return hashCode;
//	}

	public int getLastIndex() {
		return index;
	}

//	private String getMutationOperator() {
//		return this.mutationOperator;
//	}

	public void setLastIndex(int index) {
		if (index < 0) {
			// 새로 주어진 index가 0인 경우는 발생하지 않는다.
			MuJavaLogger.getLogger().error("Given index is negative value");
		}

		// //사전에 index가 정해졌던 mutantID에 대해서는 다시 Index를 정하지 못하도록 한다.
		// if(this.index >= 0) {
		// MuJavaLogger.getLogger().error("Given index is another index value of
		// MutantID");
		// }

		this.index = index;
	}

	public String toString() {
		return (index < 0) ? new String(this.mutationOperator + ID_SEPERATOR
				+ this.hashCode + ID_SEPERATOR + this.changePointID)
				: new String(this.mutationOperator + ID_SEPERATOR
						+ this.hashCode + ID_SEPERATOR + this.changePointID
						+ ID_SEPERATOR + this.index);
	}
}
