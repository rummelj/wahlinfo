package com.tu.wahlinfo.csv.entities.impl;

/**
 * 
 * @author cg
 */
public enum ElectionYear {

	_2005(5L), _2009(9L);

	private final long prefix;

	private ElectionYear(long prefix) {
		this.prefix = prefix;
	}

	public String toCleanString() {
		if (this.equals(ElectionYear._2005)) {
			return Integer.toString(2005);
		} else {
			return Integer.toString(2009);
		}
	}

	public long getPrefix() {
		return prefix;
	}

	/**
	 * 
	 * @param year
	 *            Supports: 2005 or 2009
	 * @return
	 */
	public static ElectionYear build(String year) {
		if (year.equals("2005")) {
			return ElectionYear._2005;
		} else {
			return ElectionYear._2009;
		}
	}

}
