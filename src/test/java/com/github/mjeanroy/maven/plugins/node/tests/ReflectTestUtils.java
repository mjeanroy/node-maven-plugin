/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;

/**
 * Reflection Utilites, used in tests only.
 */
public final class ReflectTestUtils {

	// Ensure non-instantiation.
	private ReflectTestUtils() {
	}

	/**
	 * Create new instance of given class.
	 *
	 * @param klass The class.
	 * @param <T> Type of created instances.
	 * @return The new instance.
	 */
	public static <T> T instantiate(Class<T> klass) {
		boolean wasNotAccessible = false;
		Constructor<T> ctor = null;

		try {
			ctor = klass.getDeclaredConstructor();
			if (!ctor.isAccessible()) {
				wasNotAccessible = true;
				ctor.setAccessible(true);
			}

			return ctor.newInstance();
		}
		catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
			throw new AssertionError(ex);
		}
		finally {
			if (ctor != null && wasNotAccessible) {
				ctor.setAccessible(true);
			}
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
		}
		catch (Exception ex) {
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
	public static <T> T readPrivate(Object instance, String name, Class<T> klass) {
		return (T) readPrivate(instance, name);
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
		}
		catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}
}
