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

import com.github.mjeanroy.maven.plugins.node.commands.Command;
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import java.io.File;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class TestE2EMojoTest extends AbstractNpmScriptMojoTest<TestE2EMojo> {

	@Override
	protected String mojoName() {
		return "test-e2e";
	}

	@Test
	public void it_should_skip_tests() throws Exception {
		TestE2EMojo mojo = createMojo("mojo-with-parameters", true);
		writeField(mojo, "skipTests", true, true);

		CommandExecutor executor = (CommandExecutor) readField(mojo, "executor", true);
		Log logger = (Log) readField(mojo, "log", true);

		mojo.execute();

		verify(executor, never()).execute(any(File.class), any(Command.class), any(NpmLogger.class));
		verify(logger).info("Npm test-e2e is skipped.");
	}
}
