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

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;

/**
 * Reflection Utilites, used in tests only.
 */
public final class ReflectUtils {

	// Ensure non-instantiation.
	private ReflectUtils() {
	}

	/**
	 * Write static final field on given class.
	 *
	 * @param klass The class.
	 * @param name The field name.
	 * @param value The new field value.
	 * @param <T> Type of value to write.
	 */
	public static <T> void writeStatic(Class<?> klass, String name, T value) {
		try {
			Field field = FieldUtils.getField(klass, name, true);
			FieldUtils.removeFinalModifier(field);
			FieldUtils.writeStaticField(field, value, true);
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}

	/**
	 * Read private field on given instance.
	 *
	 * @param instance Object instance.
	 * @param name Filed name.
	 * @param <T> Type of value to read.
	 * @return The value of field on instance.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readPrivate(Object instance, String name) {
		try {
			return (T) readField(instance, name, true);
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}

	/**
	 * Write private field on given instance.
	 *
	 * @param instance Object instance.
	 * @param name Filed name.
	 * @param value Field value.
	 * @param <T> Type of value to read.
	 */
	@SuppressWarnings("unchcked")
	public static <T> void writePrivate(Object instance, String name, T value) {
		try {
			writeField(instance, name, value, true);
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}
}
