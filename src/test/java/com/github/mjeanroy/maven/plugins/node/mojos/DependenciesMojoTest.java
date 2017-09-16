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

import com.github.mjeanroy.maven.plugins.node.exceptions.PackageJsonNotFoundException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;

import java.io.File;

import static org.apache.commons.lang3.reflect.FieldUtils.writeField;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.inOrder;

public class DependenciesMojoTest extends AbstractNpmMojoTest {

	@Rule
	public ExpectedException thrown = none();

	@Override
	protected String mojoName() {
		return "dependencies";
	}

	@Test
	public void it_should_execute_mojo() throws Exception {
		DependenciesMojo mojo = createMojo("mojo", false);

		Log logger = createLogger();
		writeField(mojo, "log", logger, true);

		mojo.execute();

		InOrder inOrder = inOrder(logger);
		inOrder.verify(logger).info("  == dependencies");
		inOrder.verify(logger).info("     react -- 0.13.3");
		inOrder.verify(logger).info("     jquery -- 2.11");
		inOrder.verify(logger).info("  == devDependencies");
		inOrder.verify(logger).info("     gulp -- 3.9.0");
	}

	@Test
	public void it_should_execute_mojo_without_dependencies() throws Exception {
		DependenciesMojo mojo = createMojo("mojo-without-dependencies", false);

		Log logger = createLogger();
		writeField(mojo, "log", logger, true);

		mojo.execute();

		InOrder inOrder = inOrder(logger);
		inOrder.verify(logger).info("  == dependencies");
		inOrder.verify(logger).info("     No dependencies.");
		inOrder.verify(logger).info("  == devDependencies");
		inOrder.verify(logger).info("     No dependencies.");
	}

	@Test
	public void it_should_fail_if_package_json_does_not_exist() throws Exception {
		thrown.expect(PackageJsonNotFoundException.class);

		DependenciesMojo mojo = createMojo("mojo-with-parameters", false);
		writeField(mojo, "workingDirectory", new File("."), true);

		Log logger = createLogger();
		writeField(mojo, "log", logger, true);

		mojo.execute();
	}
}
