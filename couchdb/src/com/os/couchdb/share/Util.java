package com.os.couchdb.share;

public class Util {
	/**
	 * Null-safe equals implementation
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(Object a, Object b) {
		return ((a == null && b == null) || (a != null && a.equals(b)));
	}

	/**
	 * Null-safe hashcode implementation. Returns 0 if o is <code>null</code>.
	 * 
	 * @param o
	 * @return
	 */
	public static int safeHashcode(Object o) {
		if (o == null) {
			return 0;
		} else {
			return o.hashCode();
		}
	}

	public static boolean hasText(String s) {
		if (s == null || s.length() == 0) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c > ' ') {
				return true;
			}
		}
		return false;
	}
}
