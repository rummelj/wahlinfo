package com.tu.wahlinfo.queries;

public abstract class AbstractSqlQueryFileParser {

    protected final String FILE_PATH_QUERIES = "src/main/resources/sql/queries/";
    protected final String HEADER_PREFIX = "REQUIRES:";
    protected final String SQL_SUFFIX = ".sql";
    protected final String STANDARD_DELIMITER = ",";
    protected final String WITH = "with ";
    protected final String AS = " as ";
    protected final String END_OF_LINE_DELIMITER = "\n";
    protected final String END_OF_FILE_DELIMITER = "\\z";

    /**
     * Parses a query from a sql file specified by its name. A query file may
     * contain a header line starting with "REQUIRED:". Any comma separated name
     * preceded by this header will be included as view within the query. At
     * this point, the parser does not support more than one level of
     * dependencies.
     * 
     * @param fileName
     * @return The contents of the file as a parsed sql query.
     */
    public abstract String parseQueryFile(String fileName) throws SqlQueryFileParserException;

}
