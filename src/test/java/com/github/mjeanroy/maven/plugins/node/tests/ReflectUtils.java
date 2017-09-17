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
