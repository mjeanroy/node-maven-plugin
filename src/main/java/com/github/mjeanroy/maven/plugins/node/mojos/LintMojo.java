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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Collection;

import static com.github.mjeanroy.maven.plugins.node.commons.lang.Objects.firstNonNull;
import static com.github.mjeanroy.maven.plugins.node.mojos.Assets.lintAssets;
import static java.util.Arrays.asList;

/**
 * Lint Mojo.
 *
 * <p>
 *
 * Basically, it only runs {@code npm run lint}.
 *
 * <p>
 *
 * This command should be used to run linters such as jshint/eslint or jscs.
 *
 * <p>
 *
 * Execution will be logged to the console.
 *
 * <p>
 *
 * This mojo will run automatically during the process-sources phase and does not
 * require online connection.
 */
@Mojo(
	name = LintMojo.GOAL_NAME,
	defaultPhase = LifecyclePhase.PROCESS_SOURCES
)
public class LintMojo extends AbstractNpmScriptMojo {

	/**
	 * The maven goal name.
	 * This is the name that will be used in the {@code pom.xml} file.
	 */
	static final String GOAL_NAME = "lint";

	/**
	 * The default {@code npm} script command (default is the maven goal name).
	 * @see LintMojo#GOAL_NAME
	 */
	private static final String DEFAULT_SCRIPT = GOAL_NAME;

	/**
	 * Set {@code lint} mojo to custom npm script.
	 */
	@Parameter(defaultValue = "${npm.script.lint}")
	private String lintScript;

	/**
	 * Flag to skip mojo execution.
	 */
	@Parameter(defaultValue = "${npm.skip.lint}")
	private boolean skipLint;

	/**
	 * Create Mojo.
	 */
	public LintMojo() {
		super();
	}

	@Override
	String getGoalName() {
		return GOAL_NAME;
	}

	@Override
	String getScript() {
		return firstNonNull(lintScript, DEFAULT_SCRIPT);
	}

	@Override
	boolean shouldSkip() {
		return skipLint;
	}

	@Override
	Collection<String> getDefaultIncrementalBuildIncludes() {
		return lintAssets();
	}
}
