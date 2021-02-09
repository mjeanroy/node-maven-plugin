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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.github.mjeanroy.maven.plugins.node.commons.lang.Objects.firstNonNull;
import static com.github.mjeanroy.maven.plugins.node.mojos.Assets.*;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

/**
 * Build Mojo.
 *
 * <p>
 *
 * Basically, it only runs {@code npm run build}, execution will be logged to the console.
 *
 * <p>
 *
 * This mojo will run automatically during the compile phase and does not
 * require online connection.
 */
@Mojo(
	name = BuildMojo.GOAL_NAME,
	defaultPhase = LifecyclePhase.GENERATE_RESOURCES
)
public class BuildMojo extends AbstractNpmScriptMojo {

	/**
	 * The maven goal name.
	 * This is the name that will be used in the {@code pom.xml} file.
	 */
	static final String GOAL_NAME = "build";

	/**
	 * The default {@code npm} script command (default is the maven goal name).
	 * @see BuildMojo#GOAL_NAME
	 */
	private static final String DEFAULT_SCRIPT = GOAL_NAME;

	/**
	 * Set {@code build} mojo to custom npm script.
	 */
	@Parameter(defaultValue = "${npm.script.build}")
	private String buildScript;

	/**
	 * Flag to skip mojo execution.
	 */
	@Parameter(defaultValue = "${npm.skip.build}")
	private boolean skipBuild;

	/**
	 * Create Mojo.
	 */
	public BuildMojo() {
		super();
	}

	@Override
	String getGoalName() {
		return GOAL_NAME;
	}

	@Override
	String getScript() {
		return firstNonNull(buildScript, DEFAULT_SCRIPT);
	}

	@Override
	boolean shouldSkip() {
		return skipBuild;
	}

	@Override
	Collection<String> getDefaultIncrementalBuildIncludes() {
		return buildAssets();
	}

	@Override
	Collection<String> getDefaultIncrementalBuildExcludes() {
		return buildIgnoreAssets();
	}
}
