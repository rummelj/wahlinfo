package com.tu.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides static methods to ease working with lists.
 * 
 * @author Johannes
 * 
 */
public class ListUtil {

	/**
	 * Utility class.
	 */
	private ListUtil() {

	}

	/**
	 * Splits a list into sublists of a given maximum length.
	 * 
	 * @param input
	 *            A list of elements
	 * @param partsSize
	 *            How long every list in the return value should maximally be.
	 *            Must be greater than zero.
	 * @return A list of lists that concatenated contain exactly the elemnts
	 *         from input but having a size of partsSize (except the last one
	 *         maybe)
	 */
	public static <T> List<List<T>> splice(List<T> input, int partsSize) {
		spliceCheckInput(input, partsSize);

		List<List<T>> result = new ArrayList<List<T>>(input.size() / partsSize);
		List<T> current = null;
		for (T element : input) {
			if (current == null || current.size() >= partsSize) {
				current = new ArrayList<T>(partsSize);
				result.add(current);
			}
			current.add(element);
		}
		return result;
	}

	static <T> void spliceCheckInput(List<T> input, int partsSize) {
		if (partsSize < 1) {
			throw new IllegalArgumentException(
					"partsSize must be greater than zero");
		}

		if (input == null) {
			throw new IllegalArgumentException("input is null");
		}
	}

	/**
	 * Returns the last element of a list.
	 * 
	 * @param input
	 *            input list
	 * @return null if input is empty
	 */
	public static <T> T getLast(List<T> input) {
		if (input == null) {
			throw new IllegalArgumentException("input is null");
		}

		if (input.isEmpty()) {
			return null;
		}
		return input.get(input.size() - 1);
	}

}
