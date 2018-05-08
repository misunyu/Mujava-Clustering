package mujava.gen;

import java.io.File;
import java.util.StringTokenizer;

import org.eclipse.core.resources.ResourcesPlugin;

public class PerformanceElement {

	// 현재의 모든 자원에 대해 저장한다.
	public static PerformanceElement getCurrent() {
		PerformanceElement element = new PerformanceElement();
		element.nanotime = System.nanoTime();
		File file = ResourcesPlugin.getWorkspace().getRoot().getLocation()
				.toFile();
		element.disk = file.getFreeSpace();
		element.mem = Runtime.getRuntime().freeMemory();

		return element;
	}

	public static PerformanceElement getDifferentFromNow(PerformanceElement init) {
		PerformanceElement last = PerformanceElement.getCurrent();

		// 나중 시간이 더 큰 값을 가진다.
		last.nanotime -= init.nanotime;
		// 나중 Disk가 더 적거나 같은 Space를 가진다.
		last.disk -= init.disk;
		last.disk *= -1;
		// 만약의 경우, 사용 Disk가 증가하는 경우에 대해 음수를 보이지 않는다.
		if (last.disk < 0)
			last.disk = 0;

		// 나중 Memory가 더 적거나 같은 Space를 가진다.
		// 단, GC등으로 인해 Memory Space가 증가할 소지가 있기때문에 0보다 작은 경우에 한해서 0으로 reset한다.
		last.mem -= init.mem;
		last.mem *= -1;
		if (last.mem < 0)
			last.mem = 0;

		return last;
	}

	private long disk;

	private long mem;

	private long nanotime;

	public PerformanceElement() {
		// do nothing
	}

	public PerformanceElement(PerformanceElement cost) {
		this.nanotime = cost.nanotime;
		this.disk = cost.disk;
		this.mem = cost.mem;
	}

	public void addTo(PerformanceElement cost) {
		this.nanotime += cost.getTimeCost();
		this.disk += cost.getDiskCost();
		this.mem += cost.getMemoryCost();
	}

	public void fromString(String str) {
		StringTokenizer st = new StringTokenizer(str, "_");
		if (st.hasMoreTokens())
			nanotime = Long.parseLong(st.nextToken());
		if (st.hasMoreTokens())
			disk = Long.parseLong(st.nextToken());
		if (st.hasMoreTokens())
			mem = Long.parseLong(st.nextToken());

	}

	public long getDiskCost() {
		return disk;
	}

	public long getMemoryCost() {
		return mem;
	}

	// private long getNanoTimeCost() {
	// return nanotime;
	// }

	public long getTimeCost() {
		return this.nanotime;
	}

	public void reset() {
		nanotime = 0;
		disk = 0;
		mem = 0;
	}

	public void setDiskCost(long disk) {
		this.disk = disk;
	}

	public void setMemoryCost(long mem) {
		this.mem = mem;
	}

	public void setNanoTime(long time) {
		this.nanotime = time;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(nanotime);
		buf.append("_");
		buf.append(disk);
		buf.append("_");
		buf.append(mem);

		return buf.toString();
	}
}
