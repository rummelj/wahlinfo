package com.tu.util;

import java.util.Random;

public class StringUtil {

	private static Random random = new Random();

	/**
	 * Utility class.
	 */
	private StringUtil() {

	}

	public static String generateRandomString(int length, String base) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			sb.append(base.charAt(random.nextInt(base.length())));
		return sb.toString();
	}
}
