package com.tu.wahlinfo.csv.parser.impl;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

public class VoteFileGeneratorTest {

	private static VoteFileGeneratorImpl generator;

	@BeforeClass
	public static void setup() {
		generator = new VoteFileGeneratorImpl(new CsvParser(),true);
	}

	@Test		
	public void testConstructCsvLine() {
		String expected = "1;2;3";
		String actual = generator.constructCsvLine(1L, 2L, 3L);
		Assert.assertEquals(expected, actual);
	}

}
