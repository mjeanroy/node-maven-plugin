/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 Mickael Jeanroy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.mjeanroy.maven.plugins.node.tests;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * Static Collection Utilites, to used in unit tests.
 */
public final class CollectionTestUtils {

	// Ensure non instantiation.
	private CollectionTestUtils() {
	}

	/**
	 * Create new, non modifiable, map from given entries.
	 *
	 * @param entries Map entries.
	 * @param <T>     Type of keys in map.
	 * @param <U>     Type of values in map.
	 * @return The map.
	 */
	public static <T, U> Map<T, U> newMap(List<Map.Entry<T, U>> entries) {
		Map<T, U> map = new LinkedHashMap<>();

		for (Map.Entry<T, U> entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}

		return unmodifiableMap(map);
	}

	/**
	 * Create new, immutable, map entry.
	 *
	 * @param key   Entry key.
	 * @param value Entry value.
	 * @param <T>   Type of key.
	 * @param <U>   Type of value.
	 * @return The map entry.
	 */
	public static <T, U> Map.Entry<T, U> newMapEntry(T key, U value) {
		return new DefaultMapEntry<>(key, value);
	}

	private static class DefaultMapEntry<K, V> implements Map.Entry<K, V> {
		private final K key;
		private final V value;

		private DefaultMapEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException();
		}
	}
}
