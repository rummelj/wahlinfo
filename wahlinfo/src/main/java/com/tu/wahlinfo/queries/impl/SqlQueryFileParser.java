package com.tu.wahlinfo.queries.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.tu.wahlinfo.queries.AbstractSqlQueryFileParser;
import com.tu.wahlinfo.queries.SqlQueryFileParserException;

public class SqlQueryFileParser extends AbstractSqlQueryFileParser {

    @Override
    public String parseQueryFile(String fileName) throws SqlQueryFileParserException {
	Scanner scanner = null;
	String content = "";
	try {
	    scanner = new Scanner(new File(FILE_PATH_QUERIES + fileName + SQL_SUFFIX)).useDelimiter(Pattern.compile(END_OF_LINE_DELIMITER));
	    content = scanner.next();	   
	    //contains is bad, but startsWith seemed to bug without offset 1 ...o0
	    if (content.contains(HEADER_PREFIX)) {
		String[] requiredViews = content.substring(HEADER_PREFIX.length()+1).split(STANDARD_DELIMITER);
		content = "";
		if (requiredViews.length > 0) {
		    content = content.concat(WITH);
		    for (int k = 0; k < requiredViews.length; k++) {
			content = content.concat(requiredViews[k].trim()).concat(AS);
			content = content.concat("( ");
			content = content.concat(this.readWholeRead(requiredViews[k].trim()));
			content = content.concat(" )");
			if (k < requiredViews.length - 1) {
			    content = content.concat(STANDARD_DELIMITER);
			}
		    }
		}
	    }
	    content = content.concat(scanner.useDelimiter(Pattern.compile(END_OF_FILE_DELIMITER)).next());
	    scanner.close();
	} catch (FileNotFoundException ex) {
	    if (scanner != null) {
		try {
		    scanner.close();
		} catch (Exception ex1) {
		    // fail
		}
		throw new SqlQueryFileParserException(ex);
	    }
	} catch (IOException ex) {
	    try {
		scanner.close();
	    } catch (Exception ex1) {
		// fail
	    }
	    throw new SqlQueryFileParserException(ex);
	}
	return content;
    }

    private String readWholeRead(String fileName) throws IOException {
	Scanner scanner = new Scanner(new File(FILE_PATH_QUERIES + fileName + SQL_SUFFIX)).useDelimiter(Pattern.compile(END_OF_FILE_DELIMITER));
	String content = scanner.next();
	scanner.close();
	return content;
    }

}
