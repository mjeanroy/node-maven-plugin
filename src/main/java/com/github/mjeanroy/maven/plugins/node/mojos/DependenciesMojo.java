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

import com.github.mjeanroy.maven.plugins.node.model.PackageJson;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.Map;

/**
 * Dependencies Mojo.
 *
 * <p>
 *
 * This mojo will be display list of npm dependencies with associated version.
 *
 * <p>
 *
 * This mojo will not run by default and does not require online connection.
 */
@Mojo(
	name = DependenciesMojo.GOAL_NAME
)
public class DependenciesMojo extends AbstractNpmMojo {

	/**
	 * The maven goal name.
	 * This is the name that will be used in the {@code pom.xml} file.
	 */
	static final String GOAL_NAME = "dependencies";

	@Override
	public void execute() {
		File packageJsonFile = lookupPackageJson();
		PackageJson packageJson = parsePackageJson(packageJsonFile);

		// Display list of dependencies
		getLog().info("  == dependencies");
		displayDependencies(packageJson.getDependencies());

		// Display list of devDependencies
		getLog().info("  == devDependencies");
		displayDependencies(packageJson.getDevDependencies());
	}

	/**
	 * Display list of dependencies.
	 *
	 * <p>
	 *
	 * If collection does not contains any dependencies, a log with this
	 * information will be displayed, otherwise all dependencies with associated
	 * version will be displayed.
	 *
	 * @param dependencies Map of dependencies (entry is dependency name, value is dependency version).
	 */
	private void displayDependencies(Map<String, String> dependencies) {
		if (dependencies.isEmpty()) {
			getLog().info("     No dependencies.");
		} else {
			for (Map.Entry<String, String> entry : dependencies.entrySet()) {
				getLog().info("     " + entry.getKey() + " -- " + entry.getValue());
			}
		}
	}
}
