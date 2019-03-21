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
 * Bower Mojo.
 *
 * <p>
 *
 * Basically, it only runs {@code npm run bower} to install
 * bower dependencies (execution will be logged to the console).
 *
 * <p>
 *
 * This mojo will run automatically during the initialize phase and
 * <strong>require</strong> online connection.
 */
@Mojo(
	name = BowerMojo.GOAL_NAME,
	defaultPhase = LifecyclePhase.INITIALIZE,
	requiresOnline = true
)
public class BowerMojo extends AbstractNpmScriptMojo {

	/**
	 * The maven goal name.
	 * This is the name that will be used in the {@code pom.xml} file.
	 */
	static final String GOAL_NAME = "bower";

	/**
	 * The maven goal name.
	 * This is the name that will be used in the {@code pom.xml} file.
	 */
	private static final String DEFAULT_SCRIPT = GOAL_NAME;

	/**
	 * Set {@code bower} mojo to custom npm script.
	 */
	@Parameter(defaultValue = "${npm.script.bower}")
	private String bowerScript;

	/**
	 * Flag to skip mojo execution.
	 */
	@Parameter(defaultValue = "${npm.skip.bower}")
	private boolean skip;

	/**
	 * Create Mojo.
	 */
	public BowerMojo() {
		super();
	}

	@Override
	String getScript() {
		return firstNonNull(bowerScript, DEFAULT_SCRIPT);
	}

	@Override
	String getScriptParameterName() {
		return "bowerScript";
	}

	@Override
	boolean isSkipped() {
		return skip;
	}
}
