package nujava.classical;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.xstream.XStream;

public class EOAMetaMutant {

	public static Object EOAGen(Object originalVar, String mutantID) {

		if (!nujava.NuJavaHelper.isExpWeakMode()) {

			return originalVar;
		}

		// 입력이 Null인 경우는 Exception이 발생되므로, 생성 및 비교 과정 없이 바로 retunr한다.
		if (originalVar == null) {
			String originalResult = "ori:null";
			String mutantResult = "mut:NullPointerException";
			// comparator.reportMutant(mutantID, originalResult, 0,
			// mutantResult);

			return originalVar;
		}

		Class<?> clz = originalVar.getClass();
		Class<?>[] types = null;
		Method mez;
		try {
			mez = clz.getMethod("clone", types);
			Object obj = mez.invoke(originalVar, (Object[]) null);

			XStream xstream = new XStream();
			if (originalVar != obj) {
				String originalResult = "ori:" + xstream.toXML(originalVar)
						+ ":" + Integer.toHexString(originalVar.hashCode());
				String mutantResult = "mut:" + xstream.toXML(obj) + ":"
						+ Integer.toHexString(obj.hashCode());
				// comparator.reportMutant(mutantID, originalResult, 0,
				// mutantResult);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			assert (false);
		} catch (NoSuchMethodException e) {
			assert (false);
		} catch (IllegalArgumentException e) {
			assert (false);
		} catch (IllegalAccessException e) {
			assert (false);
		} catch (InvocationTargetException e) {
			assert (false);
		}

		return originalVar;
	}
}
