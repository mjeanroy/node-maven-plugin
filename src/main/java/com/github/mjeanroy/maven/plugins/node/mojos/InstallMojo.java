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
import static com.github.mjeanroy.maven.plugins.node.mojos.Assets.installAssets;

/**
 * Install Mojo.
 *
 * <p>
 *
 * Basically, it only runs {@code npm install} to install
 * mandatory dependencies.
 *
 * <p>
 *
 * If install command has already been executed (during {@code pre-clean} phase), it will be
 * automatically skipped.
 *
 * <p>
 *
 * Execution will be logged to the console.
 *
 * <p>
 *
 * This mojo will run automatically during the initialize phase and
 * <strong>require</strong> online connection.
 */
@Mojo(
	name = InstallMojo.GOAL_NAME,
	defaultPhase = LifecyclePhase.INITIALIZE,
	requiresOnline = true,
	threadSafe = true
)
public class InstallMojo extends AbstractNpmScriptMojo {

	/**
	 * The maven goal name.
	 * This is the name that will be used in the {@code pom.xml} file.
	 */
	public static final String GOAL_NAME = "install";

	/**
	 * The default {@code npm} script command (default is the maven goal name).
	 * @see InstallMojo#GOAL_NAME
	 */
	private static final String DEFAULT_SCRIPT = GOAL_NAME;

	/**
	 * Set {@code install} mojo to custom npm script.
	 */
	@Parameter(defaultValue = "${npm.script.install}")
	private String installScript;

	/**
	 * Flag to skip mojo execution.
	 */
	@Parameter(defaultValue = "${npm.skip.install}")
	private boolean skipInstall;

	/**
	 * Create Mojo.
	 */
	public InstallMojo() {
		super();
	}

	@Override
	String getGoalName() {
		return GOAL_NAME;
	}

	@Override
	String getScript() {
		return firstNonNull(installScript, DEFAULT_SCRIPT);
	}

	@Override
	boolean shouldSkip() {
		return skipInstall;
	}

	@Override
	Collection<String> getDefaultIncrementalBuildIncludes() {
		return installAssets();
	}

	@Override
	LockStrategy lockStrategy() {
		return LockStrategy.WRITE;
	}
}
