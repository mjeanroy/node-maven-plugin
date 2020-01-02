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

import static com.github.mjeanroy.maven.plugins.node.commons.lang.Objects.firstNonNull;

/**
 * Publish Mojo.
 *
 * <p>
 *
 * Basically, it only runs {@code npm publish} to deploy package to remote registry (official
 * npm registry, or private enterprise registry, based on what is configured in `npmrc`).
 *
 * <p>
 *
 * Execution will be logged to the console.
 *
 * <p>
 *
 * This mojo will run automatically during the deploy phase and
 * <strong>require</strong> online connection.
 */
@Mojo(
	name = PublishMojo.GOAL_NAME,
	defaultPhase = LifecyclePhase.DEPLOY,
	requiresOnline = true
)
public class PublishMojo extends AbstractNpmScriptMojo {

	/**
	 * The maven goal name.
	 * This is the name that will be used in the {@code pom.xml} file.
	 */
	static final String GOAL_NAME = "publish";

	/**
	 * The default {@code npm} script command (default is the maven goal name).
	 * @see PublishMojo#GOAL_NAME
	 */
	private static final String DEFAULT_SCRIPT = GOAL_NAME;

	/**
	 * Set {@code publish} mojo to custom npm script.
	 */
	@Parameter(defaultValue = "${npm.script.publish}")
	private String publishScript;

	/**
	 * Flag to skip mojo execution.
	 */
	@Parameter(defaultValue = "${npm.skip.publish}")
	private boolean skipPublish;

	/**
	 * Create Mojo.
	 */
	public PublishMojo() {
		super();
	}

	@Override
	String getScript() {
		return firstNonNull(publishScript, DEFAULT_SCRIPT);
	}

	@Override
	String getScriptParameterName() {
		return "publishScript";
	}

	@Override
	boolean shouldSkip() {
		return skipPublish;
	}
}
