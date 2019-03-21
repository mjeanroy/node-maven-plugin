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

import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;

import java.io.File;

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.writePrivate;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractNpmMojoTest {

	@Rule
	public TestResources resources = new TestResources();

	@Rule
	public MojoRule mojoRule = new MojoRule();

	CommandResult createResult(boolean success) {
		CommandResult result = mock(CommandResult.class);
		when(result.isSuccess()).thenReturn(success);
		when(result.isFailure()).thenReturn(!success);
		when(result.getStatus()).thenReturn(success ? 0 : 1);
		return result;
	}

	@SuppressWarnings("unchecked")
	<T> T createMojo(String projectName, boolean hasConfiguration) throws Exception {
		File baseDir = resources.getBasedir(projectName);
		File pom = new File(baseDir, "pom.xml");
		Log logger = createLogger();
		Mojo mojo = hasConfiguration ? mojoRule.lookupMojo(mojoName(), pom) : mojoRule.lookupEmptyMojo(mojoName(), pom);

		writePrivate(mojo, "workingDirectory", baseDir);
		writePrivate(mojo, "log", logger);

		return (T) mojo;
	}

	abstract String mojoName();

	String script() {
		return mojoName();
	}

	Log createLogger() {
		return mock(Log.class);
	}
}
