package nujava;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MutantResult {

	private String mutantResult = "";
	private String originalResult = "";
	private String originalState = "";
	private String mutantID;

	public MutantResult() {
		super();
	}

	public void setMutantResult(String mutant) {
		mutant = nujava.NuJavaHelper.makeValidString(mutant);
		this.mutantResult = mutant;
	}

	public void setOriginalResult(String original) {
		original = nujava.NuJavaHelper.makeValidString(original);
		this.originalResult = original;
	}

	public String getMutantID() {
		return mutantID;
	}

	/**
	 * It return a string which has 4 serialized string object, mutant id,
	 * original output, mutant output
	 * 
	 * @return
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);

			oos.writeObject(mutantID);
			oos.writeObject(originalResult);
			oos.writeObject(mutantResult);
			oos.writeObject(originalState);

			oos.flush();
			oos.close();
			oos = null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		// String str = new String(bos.toByteArray());
		// if (str.isEmpty())
		// System.out.print("a");

		return bos.toByteArray();
	}

	public void setMutantID(String mid) {
		this.mutantID = mid;
	}

	public void setOriginalState(String preStates) {
		preStates = nujava.NuJavaHelper.makeValidString(preStates);
		this.originalState = preStates;
	}
}
