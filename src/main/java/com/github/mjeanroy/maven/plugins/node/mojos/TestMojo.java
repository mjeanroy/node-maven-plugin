/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static com.github.mjeanroy.maven.plugins.node.commons.ObjectUtils.firstNonNull;

/**
 * Test Mojo.
 * Basically, it only runs `npm test`.
 * Execution will be logged to the console.
 *
 * This mojo will run automatically during the test phase and does not
 * require online connection.
 */
@Mojo(
		name = "test",
		defaultPhase = LifecyclePhase.TEST,
		requiresOnline = false
)
public class TestMojo extends AbstractNpmScriptMojo {

	/**
	 * Check if unit tests must be skipped.
	 * By default, unit tests are skipped if maven.test.skip property is set to true.
	 */
	@Parameter(defaultValue = "${maven.test.skip}", required = false)
	private boolean skipTests;

	/**
	 * Set test mojo to custom npm script.
	 */
	@Parameter(defaultValue = "${npm.script.test}", required = false)
	private String script;

	/**
	 * Create Mojo.
	 */
	public TestMojo() {
		super();
	}

	@Override
	protected String getScript() {
		return firstNonNull(script, "test");
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!skipTests) {
			super.execute();
		} else {
			getLog().info("Tests are skipped.");
		}
	}
}
