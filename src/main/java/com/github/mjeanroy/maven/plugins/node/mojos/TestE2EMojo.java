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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static com.github.mjeanroy.maven.plugins.node.commons.ObjectUtils.firstNonNull;

/**
 * Test End 2 End Mojo.
 * Basically, it only runs `npm run-script test-e2e`.
 * Execution will be logged to the console.
 *
 * This mojo will run automatically during the test phase and does not
 * require online connection.
 */
@Mojo(
	name = TestE2EMojo.GOAL_NAME,
	defaultPhase = LifecyclePhase.INTEGRATION_TEST,
	requiresOnline = false
)
public class TestE2EMojo extends AbstractNpmScriptMojo {

	/**
	 * The maven goal name.
	 * This is the name that will be used in the {@code pom.xml} file.
	 */
	static final String GOAL_NAME = "test-e2e";

	/**
	 * The default {@code npm} script command (default is the maven goal name).
	 * @see TestE2EMojo#GOAL_NAME
	 */
	private static final String DEFAULT_SCRIPT = GOAL_NAME;

	/**
	 * Set test-e2e mojo to custom npm script.
	 */
	@Parameter(defaultValue = "${npm.script.testE2E}", required = false)
	private String script;

	/**
	 * Check if end to end tests must be skipped.
	 * By default, end to end tests are skipped if maven.test.skip property is set to true.
	 */
	@Parameter(defaultValue = "${maven.test.skip}", required = false)
	private boolean skipTests;

	/**
	 * Flag to skip mojo execution.
	 */
	@Parameter(defaultValue = "${npm.skip.testE2E}", required = false)
	private boolean skip;

	/**
	 * Create Mojo.
	 */
	public TestE2EMojo() {
		super();
	}

	@Override
	protected String getScript() {
		return firstNonNull(script, DEFAULT_SCRIPT);
	}

	@Override
	protected boolean isSkipped() {
		return skipTests || skip;
	}
}
