/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

package com.github.mjeanroy.maven.plugins.node.commons;

import com.github.mjeanroy.maven.plugins.node.exceptions.JsonException;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Json Static Utilities.
 */
public final class JsonUtils {

	/**
	 * Read json file and return object representation.
	 *
	 * @param jsonFile Json File.
	 * @param klass Class to use for deserialization.
	 * @param <T> Type of object to return.
	 * @return Object representation of json file.
	 */
	public static <T> T parseJson(File jsonFile, Class<T> klass) {
		try {
			FileReader reader = new FileReader(jsonFile);
			BufferedReader buf = new BufferedReader(reader);

			StringBuilder json = new StringBuilder();
			String line;
			while ((line = buf.readLine()) != null) {
				json.append(line);
			}

			return new Gson().fromJson(json.toString(), klass);
		}
		catch (IOException ex) {
			throw new JsonException(ex);
		}
	}
}
