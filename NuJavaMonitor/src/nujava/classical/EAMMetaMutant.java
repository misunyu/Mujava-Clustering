package nujava.classical;

import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;

import nujava.MutantMonitor;

import com.thoughtworks.xstream.XStream;

public class EAMMetaMutant {

	class A {
		public void m() {
			System.out.println("A");
		}
	}

	class B extends A {
		public void m() {

			System.out.println("B");
		}
	}

	class C extends B {
		public void m() {

			System.out.println("C");
		}
	}

	public static void main(String args[]) {
		EAMMetaMutant m = new EAMMetaMutant();

		//EAMMetaMutant.A a = m.new A();
		// a.m();
		//EAMMetaMutant.B b = m.new B();
		// b.m();
		EAMMetaMutant.C c = m.new C();
		// c.m();

		Class clz = c.getClass().getSuperclass();
		Method[] methods = clz.getMethods();
		for (Method method : methods) {
			if (method.getName().equals("m")) {
				try {
					method.invoke(c, (Object[]) null);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			// System.out.println(method);
		}
	}

	public static <E> E EAMGen(Object exp, Class<?> type, String[] list,
			String original, String mutantID, E deadVar) {

		if (exp == null && type == null) {
			// 둘다 없는 경우는 Exception이 발생되는 경우
			throw new NullPointerException();
		}

		Class<?> clz = (type != null) ? type : exp.getClass();
		if (clz == null) {
			throw new NullPointerException();
		}

		Class<?>[] types = null;
		Method mez = null;

		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			int sID = MutantMonitor.subID;
			String name = list[sID];

			E obj = null;
			try {
				mez = clz.getMethod(name, types);
				obj = (E) mez.invoke(exp, (Object[]) null);
			} catch (SecurityException e) {
				// e.printStackTrace();
				throw new NullPointerException();
			} catch (NoSuchMethodException e) {
				// e.printStackTrace();
				throw new NullPointerException();
			} catch (IllegalArgumentException e) {
				// e.printStackTrace();
				throw new NullPointerException();
			} catch (IllegalAccessException e) {
				// e.printStackTrace();
				throw new NullPointerException();
			} catch (InvocationTargetException e) {
				// e.printStackTrace();
				throw new NullPointerException();
			}

			return obj;
		}

		XStream xs = MutantMonitor.getInstance().getXstream();

		String ori = "";
		E originalObj = null;

		RuntimeException runtimeException = null;
		Error error = null;

		try {
			mez = clz.getMethod(original, types);
			originalObj = (E) mez.invoke(exp, (Object[]) null);
			ori = xs.toXML(originalObj);
		} catch (SecurityException e) {
			// e.printStackTrace();
			ori = "SecurityException";
		} catch (NoSuchMethodException e) {
			// e.printStackTrace();
			ori = "NoSuchMethodException";
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
			ori = "IllegalArgumentException";
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
			ori = "IllegalAccessException";
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
			ori = "InvocationTargetException";
		} catch (RuntimeException e) {
			ori = e.toString();
			runtimeException = e;
		} catch (Error e) {
			ori = e.toString();
			error = e;
		}

		for (int i = 0; i < list.length; i++) {
			String candidateName = list[i];
			String mut = "";
			try {
				mez = clz.getMethod(candidateName, types);
				E mutantObj = (E) mez.invoke(exp, (Object[]) null);
				mut = xs.toXML(mutantObj);
			} catch (SecurityException e) {
				// e.printStackTrace();
				mut = "SecurityException";
			} catch (NoSuchMethodException e) {
				// e.printStackTrace();
				mut = "NoSuchMethodException";
			} catch (IllegalArgumentException e) {
				// e.printStackTrace();
				mut = "IllegalArgumentException";
			} catch (IllegalAccessException e) {
				// e.printStackTrace();
				mut = "IllegalAccessException";
			} catch (InvocationTargetException e) {
				// e.printStackTrace();
				mut = "InvocationTargetException";
			} catch (Exception e) {
				mut = e.toString();
			}

			if (!ori.equals(mut)) {
				// comparator.reportMutant(mutantID, ori, i, mut);
			}
		}

		if (runtimeException != null)
			throw runtimeException;
		if (error != null)
			throw error;

		return originalObj;
	}
}
