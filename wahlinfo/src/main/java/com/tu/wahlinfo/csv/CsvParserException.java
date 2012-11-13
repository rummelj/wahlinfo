package com.tu.wahlinfo.csv;

/**
 *
 * @author cg
 */
public class CsvParserException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CsvParserException(String message) {
        super(message);
    }

    public CsvParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
