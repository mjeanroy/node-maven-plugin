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

package com.github.mjeanroy.maven.plugins.node.mojos;

import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.readPrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.writePrivate;
import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class TestMojoTest extends AbstractNpmScriptMojoTest<TestMojo> {

	@Override
	String mojoName() {
		return "test";
	}

	@Override
	String skipMessage(String npmClient) {
		return skipMessage();
	}

	@Override
	String scriptParameterName() {
		return "testScript";
	}

	@Override
	void enableSkip(TestMojo mojo) {
		writePrivate(mojo, "skipTest", true);
	}

	@Test
	public void it_should_skip_tests_with_skipTests() throws Exception {
		TestMojo mojo = lookupMojo("mojo", singletonMap(
				"skipTests", true
		));

		mojo.execute();

		verifyTestsHaveBeenSkipped(mojo);
	}

	@Test
	public void it_should_skip_tests_with_mavenTestSkip() throws Exception {
		TestMojo mojo = lookupMojo("mojo", singletonMap(
				"mavenTestSkip", true
		));

		mojo.execute();

		verifyTestsHaveBeenSkipped(mojo);
	}

	@Test
	public void it_should_skip_tests_with_mavenTestSkipExec() throws Exception {
		TestMojo mojo = lookupMojo("mojo", singletonMap(
				"mavenTestSkipExec", true
		));

		mojo.execute();

		verifyTestsHaveBeenSkipped(mojo);
	}

	private void verifyTestsHaveBeenSkipped(TestMojo mojo) {
		verifyExecutorNotRunned(mojo);
		verifySkipMessageOutput(mojo);
	}

	private void verifyExecutorNotRunned(TestMojo mojo) {
		CommandExecutor executor = readPrivate(mojo, "executor");
		verifyZeroInteractions(executor);
	}

	private void verifySkipMessageOutput(TestMojo mojo) {
		Log logger = readPrivate(mojo, "log");
		verify(logger).info(skipMessage());
	}

	private String skipMessage() {
		return "Tests are skipped.";
	}
}
