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

import com.github.mjeanroy.maven.plugins.node.model.LockStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Collection;

import static com.github.mjeanroy.maven.plugins.node.commons.lang.Objects.firstNonNull;
import static com.github.mjeanroy.maven.plugins.node.mojos.Assets.*;

/**
 * Prepare Mojo.
 *
 * <p>
 *
 * Basically, it only runs {@code npm run build}, execution will be logged to the console.
 *
 * <p>
 *
 * This mojo will run automatically during the prepare-resources phase (i.e just before the compile phase) and does not
 * require online connection.
 */
@Mojo(
	name = PrepareMojo.GOAL_NAME,
	defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
	threadSafe = true
)
public class PrepareMojo extends AbstractNpmScriptMojo {

	/**
	 * The maven goal name.
	 * This is the name that will be used in the {@code pom.xml} file.
	 */
	public static final String GOAL_NAME = "prepare";

	/**
	 * The default {@code npm} script command (default is the maven goal name).
	 * @see PrepareMojo#GOAL_NAME
	 */
	private static final String DEFAULT_SCRIPT = GOAL_NAME;

	/**
	 * Set {@code prepare} mojo to custom npm script.
	 */
	@Parameter(defaultValue = "${npm.script.prepare}")
	private String prepareScript;

	/**
	 * Flag to skip mojo execution.
	 */
	@Parameter(defaultValue = "${npm.skip.prepare}")
	private boolean skipPrepare;

	/**
	 * Create Mojo.
	 */
	public PrepareMojo() {
		super();
	}

	@Override
	String getGoalName() {
		return GOAL_NAME;
	}

	@Override
	String getScript() {
		return firstNonNull(prepareScript, DEFAULT_SCRIPT);
	}

	@Override
	boolean shouldSkip() {
		return skipPrepare;
	}

	@Override
	Collection<String> getDefaultIncrementalBuildIncludes() {
		return buildAssets();
	}

	@Override
	Collection<String> getDefaultIncrementalBuildExcludes() {
		return buildIgnoreAssets();
	}
	@Override
	LockStrategy lockStrategy() {
		return LockStrategy.READ;
	}
}
