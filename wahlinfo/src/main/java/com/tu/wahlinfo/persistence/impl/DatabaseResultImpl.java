/**
 * 
 */
package com.tu.wahlinfo.persistence.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tu.wahlinfo.model.DatabaseResult;

/**
 * @author Johannes
 * 
 */
public class DatabaseResultImpl implements DatabaseResult {

	Map<String, List<String>> data;

	public DatabaseResultImpl(Map<String, List<String>> data) {
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Map<String, String>> iterator() {
		return new Iterator<Map<String, String>>() {

			int current = 0;

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Not supported");
			}

			@Override
			public Map<String, String> next() {
				Map<String, String> result = new HashMap<String, String>();
				for (Entry<String, List<String>> entry : data.entrySet()) {
					result.put(entry.getKey(), entry.getValue().get(current));
				}
				current++;
				return result;
			}

			@Override
			public boolean hasNext() {
				for (Entry<String, List<String>> entry : data.entrySet()) {
					if (entry.getValue().size() < current + 1) {
						return false;
					}
				}
				return true;
			}
		};
	}

	@Override
	public List<String> toList() {
		if (data.keySet().size() != 1) {
			throw new UnsupportedOperationException(
					"Cannot convert a result from the database to a list if there are more than 1 columns contained (or none)");
		}
		for (Entry<String, List<String>> dataEntry : data.entrySet()) {
			return dataEntry.getValue();
		}
		// Cannot happen
		return null;
	}

}
