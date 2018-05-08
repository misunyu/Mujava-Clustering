package kaist.selab.util;

import java.io.UnsupportedEncodingException;

public class XMLValidator {

	public static String makeValidString(String str) {
		String newStr = new String();
		try {
			newStr = new String(str.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return newStr;
	}

}
