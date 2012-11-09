/**
 * 
 */
package com.tu.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Johannes
 * 
 */
public class ListUtilTest {

	/**
	 * Test method for {@link com.tu.util.ListUtil#splice(java.util.List, int)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSpliceFalsePartsSizeZero() {
		ListUtil.splice(toList(1, 2, 3), 0);
	}

	/**
	 * Test method for {@link com.tu.util.ListUtil#splice(java.util.List, int)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSpliceFalsePartsSizeSmaller() {
		ListUtil.splice(toList(1, 2, 3), -7);
	}

	/**
	 * Test method for {@link com.tu.util.ListUtil#splice(java.util.List, int)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSpliceInputNull() {
		ListUtil.splice(null, 1);
	}

	/**
	 * Test method for {@link com.tu.util.ListUtil#splice(java.util.List, int)}.
	 */
	@Test
	public void testSpliceEmptyInput() {
		assertEquals(0, ListUtil.splice(toList(), 5).size());
	}

	/**
	 * Test method for {@link com.tu.util.ListUtil#splice(java.util.List, int)}.
	 */
	@Test
	public void testSpliceSplittedInOnes() {
		List<List<Integer>> result = ListUtil.splice(toList(1, 2, 3), 1);
		assertEquals(3, result.size());
		assertEquals(1, result.get(0).size());
		assertEquals(1, result.get(1).size());
		assertEquals(1, result.get(2).size());
		assertEquals(new Integer(1), result.get(0).get(0));
		assertEquals(new Integer(2), result.get(1).get(0));
		assertEquals(new Integer(3), result.get(2).get(0));
	}

	/**
	 * Test method for {@link com.tu.util.ListUtil#splice(java.util.List, int)}.
	 */
	@Test
	public void testSpliceSplittedWithRest() {
		List<List<Integer>> result = ListUtil.splice(toList(1, 2, 3), 2);
		assertEquals(2, result.size());
		assertEquals(2, result.get(0).size());
		assertEquals(1, result.get(1).size());
		assertEquals(new Integer(1), result.get(0).get(0));
		assertEquals(new Integer(2), result.get(0).get(1));
		assertEquals(new Integer(3), result.get(1).get(0));
	}

	/**
	 * Test method for {@link com.tu.util.ListUtil#splice(java.util.List, int)}.
	 */
	@Test
	public void testSpliceUsualUseCase() {
		List<List<Integer>> result = ListUtil.splice(
				toList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 3);
		assertEquals(4, result.size());
		assertEquals(3, result.get(0).size());
		assertEquals(3, result.get(1).size());
		assertEquals(3, result.get(2).size());
		assertEquals(1, result.get(3).size());
	}

	/**
	 * Test method for {@link com.tu.util.ListUtil#getLast(java.util.List)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetLastInputNull() {
		ListUtil.getLast(null);
	}

	/**
	 * Test method for {@link com.tu.util.ListUtil#getLast(java.util.List)}.
	 */
	@Test
	public void testGetLastInputEmpty() {
		assertNull(ListUtil.getLast(Collections.emptyList()));
	}

	/**
	 * Test method for {@link com.tu.util.ListUtil#getLast(java.util.List)}.
	 */
	@Test
	public void testGetLastSingletonList() {
		assertEquals("a", ListUtil.getLast(Collections.singletonList("a")));
	}

	/**
	 * Test method for {@link com.tu.util.ListUtil#getLast(java.util.List)}.
	 */
	@Test
	public void testGetLastUsualUsecase() {
		assertEquals(new Integer(3), ListUtil.getLast(toList(1, 2, 3)));
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> toList(T... ts) {
		List<T> result = new ArrayList<T>(ts.length);
		for (T t : ts) {
			result.add(t);
		}
		return result;
	}

}
