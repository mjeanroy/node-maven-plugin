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

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.readPrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.writePrivate;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class BuildMojoTest extends AbstractNpmScriptIncrementalMojoTest<BuildMojo> {

	@Override
	String mojoName() {
		return "build";
	}

	@Override
	void overrideScript(BuildMojo mojo, String script) {
		writePrivate(mojo, "buildScript", script);
	}

	@Override
	void enableSkip(BuildMojo mojo) {
		writePrivate(mojo, "skipBuild", true);
	}

	@Test
	public void it_should_write_input_state_after_build() throws Exception {
		BuildMojo mojo = lookupMojo("mojo-with-eslint");

		mojo.execute();

		verifyStateFile(mojo, asList(
				"/index.js::18565ddcc053760b5c8e41de0f692d1a",
				"/package.json::243dbc1eb941ea6d7339d2d42b0fa05e",
				"/src/hello-world.js::23e84ea87061dfba92cb42373b34ee82"
		));
	}

	@Test
	public void it_should_write_input_state_from_ts_project_after_build() throws Exception {
		BuildMojo mojo = lookupMojo("mojo-with-tslint");

		mojo.execute();

		verifyStateFile(mojo, asList(
				"/index.ts::67d3d4750c38b23b8255b9148f72e0af",
				"/package.json::243dbc1eb941ea6d7339d2d42b0fa05e",
				"/src/hello-world.ts::56edefd6256658c7605b003051a9cbdd"
		));
	}

	@Test
	public void it_should_run_mojo_after_incremental_build() throws Exception {
		BuildMojo mojo = lookupMojo("mojo-with-tslint");

		mojo.execute();
		resetMojo(mojo);
		mojo.execute();

		Log log = readPrivate(mojo, "log");
		verify(log).info("Command npm run build already done, no changes detected, skipping.");

		CommandExecutor executor = readPrivate(mojo, "executor");
		verifyZeroInteractions(executor);
	}
}
