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

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import java.io.File;

import static com.github.mjeanroy.maven.plugins.node.tests.FileTestUtils.join;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.readPrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.writePrivate;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class LintMojoTest extends AbstractNpmScriptIncrementalMojoTest<LintMojo> {

	@Override
	String mojoName() {
		return "lint";
	}

	@Override
	void overrideScript(LintMojo mojo, String script) {
		writePrivate(mojo, "lintScript", script);
	}

	@Override
	void enableSkip(LintMojo mojo) {
		writePrivate(mojo, "skipLint", true);
	}

	@Test
	public void it_should_write_input_state_after_build() throws Exception {
		LintMojo mojo = lookupMojo("mojo-with-eslint");
		File workingDirectory = readPrivate(mojo, "workingDirectory");

		mojo.execute();

		verifyStateFile(mojo, asList(
				join(workingDirectory, ".eslintignore"),
				join(workingDirectory, ".eslintrc"),
				join(workingDirectory, "index.js"),
				join(workingDirectory, "package.json"),
				join(workingDirectory, "src", "hello-world.js")
		));
	}

	@Test
	public void it_should_write_input_from_tslint_state_after_build() throws Exception {
		LintMojo mojo = lookupMojo("mojo-with-tslint");
		File workingDirectory = readPrivate(mojo, "workingDirectory");

		mojo.execute();

		verifyStateFile(mojo, asList(
				join(workingDirectory, "index.ts"),
				join(workingDirectory, "package.json"),
				join(workingDirectory, "src", "hello-world.ts"),
				join(workingDirectory, "tslint.json")
		));
	}

	@Test
	public void it_should_run_mojo_after_incremental_build() throws Exception {
		LintMojo mojo = lookupMojo("mojo-with-tslint");

		mojo.execute();
		resetMojo(mojo);
		mojo.execute();

		verify(readPrivate(mojo, "log", Log.class)).info("Command npm run lint already done, no changes detected, skipping.");
		verifyZeroInteractions(readPrivate(mojo, "executor"));
	}
}
