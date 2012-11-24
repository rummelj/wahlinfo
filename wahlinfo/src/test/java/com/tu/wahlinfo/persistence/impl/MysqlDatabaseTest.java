/**
 * 
 */
package com.tu.wahlinfo.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;

/**
 * @author Johannes
 * 
 */
public class MysqlDatabaseTest {

	private static final int MAX_BULK_INSERT_SIZE = 3;

	@Mock
	DatabaseAccessor databaseAccessorMock;

	DatabaseImpl underTest;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		DatabaseImpl.MAX_BULK_INSERT_SIZE = MAX_BULK_INSERT_SIZE;

		MockitoAnnotations.initMocks(this);

		underTest = new DatabaseImpl();
		underTest.databaseAccessor = databaseAccessorMock;
	}

	/**
	 * Test method for
	 * {@link com.tu.wahlinfo.persistence.impl.DatabaseImpl#insert(java.lang.String, java.util.Map)}
	 * .
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public void testInsert() throws DatabaseException {
		Map<String, String> values = new LinkedHashMap<String, String>();
		values.put("a", "b");
		values.put("c", "d");
		underTest.insert("table", values);
		verify(databaseAccessorMock, times(1)).executeStatement(
				"insert into table (a, c) values ('b', 'd');");
	}

	/**
	 * Test method for
	 * {@link com.tu.wahlinfo.persistence.impl.DatabaseImpl#bulkInsert(java.lang.String, java.util.Map)}
	 * .
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public void testBulkInsert() throws DatabaseException {
		Map<String, List<String>> values = new LinkedHashMap<String, List<String>>();
		values.put("a", toList("1", "2", "3", "4", "5"));
		values.put("b", toList("6", "7", "8", "9", "1'0"));
		underTest.bulkInsert("table", values);
		verify(databaseAccessorMock, times(1))
				.executeStatement(
						"insert into table (a, b) values ('1', '6'), ('2', '7'), ('3', '8');");
		verify(databaseAccessorMock, times(1)).executeStatement(
				"insert into table (a, b) values ('4', '9'), ('5', '1''0');");
	}

	/**
	 * Test method for
	 * {@link com.tu.wahlinfo.persistence.impl.DatabaseImpl#bulkInsert(java.lang.String, java.util.Map)}
	 * .
	 * 
	 * @throws DatabaseException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testBulkInsertWrongListSizes() throws DatabaseException {
		Map<String, List<String>> values = new LinkedHashMap<String, List<String>>();
		values.put("a", toList("1", "2", "3", "4", "5"));
		values.put("b", toList("6", "7", "8", "9"));
		underTest.bulkInsert("table", values);
	}

	@Test
	public void testSanizise() {
		assertEquals("'a''b'", underTest.sanitise("a'b"));
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
