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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static com.github.mjeanroy.maven.plugins.node.commons.ObjectUtils.firstNonNull;

/**
 * Clean Mojo.
 * Basically, it only runs `npm run-script clean`.
 * Executed will be logged to the console.
 *
 * This mojo will run automatically during the clean phase and does not
 * require online connection.
 */
@Mojo(
		name = "clean",
		defaultPhase = LifecyclePhase.CLEAN,
		requiresOnline = false
)
public class CleanMojo extends AbstractNpmScriptMojo {

	/**
	 * Set clean mojo to custom npm script.
	 */
	@Parameter(defaultValue = "${npm.script.clean}", required = false)
	private String script;

	/**
	 * Flag to skip mojo execution.
	 */
	@Parameter(defaultValue = "${npm.skip.clean}", required = false)
	private boolean skip;

	/**
	 * Create Mojo.
	 */
	public CleanMojo() {
		super();
	}

	@Override
	protected String getScript() {
		return firstNonNull(script, "clean");
	}

	@Override
	protected boolean isSkipped() {
		return skip;
	}
}
