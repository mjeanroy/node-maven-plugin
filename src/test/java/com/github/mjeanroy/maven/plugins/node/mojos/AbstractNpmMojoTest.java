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

package com.github.mjeanroy.maven.plugins.node.mojos;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;

import java.io.File;
import java.util.Map;

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.writePrivate;
import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.mock;

public abstract class AbstractNpmMojoTest<T extends AbstractNpmMojo> {

	@Rule
	public TestResources resources = new TestResources();

	@Rule
	public MojoRule mojoRule = new MojoRule();

	T lookupMojo(String projectName) throws Exception {
		return lookupAndConfigureMojo(projectName, new MojoFactory<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T build(String goal, File pom) throws Exception {
				return (T) mojoRule.lookupMojo(goal, pom);
			}
		});
	}

	T lookupEmptyMojo(String projectName) throws Exception {
		return lookupAndConfigureMojo(projectName, new MojoFactory<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T build(String goal, File pom) throws Exception {
				return (T) mojoRule.lookupEmptyMojo(goal, pom);
			}
		});
	}

	T lookupMojo(String projectName, Map<String, ?> configuration) throws Exception {
		return lookupAndConfigureMojo(projectName, configuration, new MojoFactory<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T build(String goal, File pom) throws Exception {
				return (T) mojoRule.lookupEmptyMojo(goal, pom);
			}
		});
	}

	private T lookupAndConfigureMojo(String projectName, MojoFactory<T> factory) throws Exception {
		Map<String, Object> configuration = emptyMap();
		return lookupAndConfigureMojo(projectName, configuration, factory);
	}

	private T lookupAndConfigureMojo(String projectName, Map<String, ?> configuration, MojoFactory<T> factory) throws Exception {
		File baseDir = resources.getBasedir(projectName);
		File pom = new File(baseDir, "pom.xml");
		Log logger = createLogger();

		T mojo = factory.build(mojoName(), pom);

		writePrivate(mojo, "workingDirectory", baseDir);
		writePrivate(mojo, "log", logger);

		for (Map.Entry<String, ?> property : configuration.entrySet()) {
			writePrivate(mojo, property.getKey(), property.getValue());
		}

		return mojo;
	}

	/**
	 * Get the mojo name to test.
	 *
	 * @return Mojo Name.
	 */
	abstract String mojoName();

	/**
	 * The logger that will be injected into created mojos.
	 *
	 * @return The Logger.
	 */
	private Log createLogger() {
		return mock(Log.class);
	}

	private interface MojoFactory<T extends AbstractNpmMojo> {
		T build(String goal, File pom) throws Exception;
	}
}
