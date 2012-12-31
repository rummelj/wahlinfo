package com.tu.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class FileScanner {

	private FileScanner() {
	};

	/**
	 * Scan the contents of a file until its EOF token and return it.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 *             In case the reader could not be closed
	 */
	public static String scanFile(String path) throws IOException {
		InputStream is = FileScanner.class.getResourceAsStream(path);

		if (is == null) {
			throw new IOException("Could not locate file " + path);
		}

		InputStreamReader reader = new InputStreamReader(is);
		Scanner scanner = new Scanner(reader).useDelimiter("\\Z");
		String res = scanner.next();
		scanner.close();
		reader.close();
		return res;
	}

}
