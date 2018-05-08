package mujava.executor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MutantPartition {

	class GroupManager {
		/**
		 * Key = MutantResult Value = Group
		 */
		Map<String, Group> groups = new HashMap<String, Group>();

		public void add(String mutantID, String mutantResult) {
			Group group = groups.get(mutantResult);
			if (group == null) {
				group = new Group();
				groups.put(mutantResult, group);
			}
			group.add(mutantID);
		}

		public void merge(List<Set<String>> results) {
			if (groups.values().isEmpty()) {
				return;
			}

			ArrayList<Set<String>> mutants = new ArrayList<Set<String>>();
			for (Group group : groups.values()) {
				mutants.add(group.list);
			}
			for (Set<String> group : results) {
				mutants.add(group);
			}

			if (mutants.size() > 1) {

				ArrayList<Set<String>> disjointedMutants = new ArrayList<Set<String>>(
						mutants);
				boolean disjoint = true;
				do {
					disjoint = true;
					for (int i = 0; disjoint
							&& i < disjointedMutants.size() - 1; i++) {
						Set<String> left = disjointedMutants.get(i);
						for (int j = i + 1; disjoint
								&& j < disjointedMutants.size(); j++) {
							Set<String> right = disjointedMutants.get(j);
							Set<String> interection = new HashSet<String>(left);
							interection.retainAll(right);

							if (!interection.isEmpty()) {
								left.removeAll(interection);
								right.removeAll(interection);

								disjoint = false;

								disjointedMutants.add(interection);
								if (right.isEmpty()) {
									disjointedMutants.remove(j);
								}
								if (left.isEmpty()) {
									disjointedMutants.remove(i);
								}
							}
						}
					}

				} while (!disjoint);

				results.clear();
				results.addAll(disjointedMutants);
			} else {
				results.clear();
				results.addAll(mutants);
			}

		}

		public void clear() {
			groups.clear();
		}

	}

	class Group {
		Set<String> list = new HashSet<String>();

		String getRepresentative() {
			return (list.isEmpty()) ? "" : list.iterator().next();
		}

		public boolean disjoint(Group group) {
			return Collections.disjoint(list, group.list);
		}

		public void add(String mutantID) {
			list.add(mutantID);
		}

		boolean contains(String mutantID) {
			return list.contains(mutantID);
		}
	}

	public void add(String mID, long time, String mutantResult) {
		if (currentTime < time) {
			currentGroupMgr.merge(groups);
			currentGroupMgr.clear();
			currentTime = time;
			currentGroupMgr.add(mID, mutantResult);
		} else if (currentTime == time) {
			currentGroupMgr.add(mID, mutantResult);
		} else {
			// do nothing
		}
	}

	GroupManager currentGroupMgr = new GroupManager();
	List<Set<String>> groups = new ArrayList<Set<String>>();
	long currentTime = 0;

	public void makeDisjointSet(DisjointSet killedMutants) {

		for (Set<String> group : groups) {

			String anchor = "";
			
			for (String id : group) {
				String value = (String) killedMutants.findSet(id);
				if (value == null) {
					killedMutants.makeSet(id);
					value = id;
				}

				if (anchor.isEmpty()) {
					anchor = value;
				}

				killedMutants.union(anchor, value);
			}
		}
	}

	public void close() {
		if (currentGroupMgr != null) {
			currentGroupMgr.merge(groups);
		}
	}

	/**
	 * 독립된 단독 List로 등록한다.
	 * 
	 * @param mID
	 */
	public void add(String mID) {
		Set<String> set = new HashSet<String>(1);
		set.add(mID);
		groups.add(set);
	}
}
