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

package com.github.mjeanroy.maven.plugins.node.mojos;

import com.github.mjeanroy.maven.plugins.node.commands.Command;
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import com.github.mjeanroy.maven.plugins.node.loggers.NpmLogger;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.io.File;

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.readPrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.writePrivate;
import static java.util.Collections.singletonMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class TestE2EMojoTest extends AbstractNpmScriptMojoTest<TestE2EMojo> {

	@Override
	String mojoName() {
		return "test-e2e";
	}

	@Override
	String scriptParameterName() {
		return "testE2EScript";
	}

	@Override
	void enableSkip(TestE2EMojo mojo) {
		writePrivate(mojo, "skipTestE2E", true);
	}

	@Test
	public void it_should_skip_tests_with_mavenTestSkip() throws Exception {
		TestE2EMojo mojo = lookupMojo("mojo", singletonMap(
				"mavenTestSkip", true
		));

		mojo.execute();

		verifyTestsSkipped(mojo);
	}

	@Test
	public void it_should_skip_tests_with_mavenTestSkipExec() throws Exception {
		TestE2EMojo mojo = lookupMojo("mojo", singletonMap(
				"mavenTestSkipExec", true
		));

		mojo.execute();

		verifyTestsSkipped(mojo);
	}

	@Test
	public void it_should_skip_tests_with_skipITs() throws Exception {
		TestE2EMojo mojo = lookupMojo("mojo", singletonMap(
				"skipITs", true
		));

		mojo.execute();

		verifyTestsSkipped(mojo);
	}

	private void verifyTestsSkipped(TestE2EMojo mojo) {
		verifyExecutorNotRunned(mojo);
		verifySkipTestOutput(mojo);
	}

	private void verifyExecutorNotRunned(TestE2EMojo mojo) {
		CommandExecutor executor = readPrivate(mojo, "executor");
		verify(executor, never()).execute(any(File.class), any(Command.class), any(NpmLogger.class), ArgumentMatchers.<String, String>anyMap());
	}

	private void verifySkipTestOutput(TestE2EMojo mojo) {
		Log logger = readPrivate(mojo, "log");
		verify(logger).info("Command 'npm run test-e2e' is skipped.");
	}
}
