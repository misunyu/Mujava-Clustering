package nujava.classical;

import nujava.MutantMonitor;

import com.thoughtworks.xstream.XStream;

public class PRVMetaMutant {

	public static <E> E PRVGen(E[] list, E original, String mutantID) {
		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			int sID = MutantMonitor.subID;

			sID++;

			if (list != null && list.length > 0 && sID >= list.length) {
				sID = list.length - 1;
			}

			return list[sID];
		}

		XStream xs = MutantMonitor.getInstance().getXstream();
		String ori = xs.toXML(original);
		for (int i = 1; i < list.length; i++) {
			E candidate = list[i];
			String mut = xs.toXML(candidate);
			if (!ori.equals(mut)) {
				// comparator.reportMutant(mutantID, ori, i-1, mut);
			}
		}
		return original;
	}

	public static int PRVGen(int[] list, int original, String mutantID) {
		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			int sID = MutantMonitor.subID;

			sID++;

			if (list != null && list.length > 0 && sID >= list.length) {
				sID = list.length - 1;
			}

			return list[sID];
		}

		XStream xs = MutantMonitor.getInstance().getXstream();
		String ori = xs.toXML(original);
		for (int i = 1; i < list.length; i++) {
			int candidate = list[i];
			String mut = xs.toXML(candidate);
			if (!ori.equals(mut)) {
				// comparator.reportMutant(mutantID, ori, i - 1, mut);
			}
		}
		return original;
	}

	public static boolean PRVGen(boolean[] list, boolean original,
			String mutantID) {
		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			int sID = MutantMonitor.subID;

			sID++;

			if (list != null && list.length > 0 && sID >= list.length) {
				sID = list.length - 1;
			}

			return list[sID];
		}

		XStream xs = MutantMonitor.getInstance().getXstream();
		String ori = xs.toXML(original);
		for (int i = 0; i < list.length; i++) {
			boolean candidate = list[i];
			String mut = xs.toXML(candidate);
			if (!ori.equals(mut)) {
				// comparator.reportMutant(mutantID, ori, i - 1, mut);
			}
		}
		return original;
	}
}
