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

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

/**
 * Static Asset Utilities.
 */
final class Assets {

	// Ensure non instantiation.
	private Assets() {
	}

	/**
	 * Files related to NPM and NPM installation.
	 */
	private static final List<String> NPM_ASSETS = asList(
			"**/package.json",
			"**/package-lock.json",
			"**/.npmrc"
	);

	/**
	 * Files related to YARN and YARN installation.
	 */
	private static final List<String> YARN_ASSETS = asList(
			"**/package.json",
			"**/yarn.lock",
			"**/.yarnrc",
			"**/.pnp.js"
	);

	/**
	 * Files related to bower.
	 */
	private static final List<String> BOWER_ASSETS = asList(
			"**/bower.json",
			"**/.bowerrc"
	);

	/**
	 * Files related to LERNA.
	 */
	private static final List<String> LERNA_ASSETS = asList(
			"**/lerna.json",
			"**/.lernarc"
	);

	/**
	 * Files related to linter configurations.
	 */
	private static final List<String> LINTER_ASSETS = asList(
			"**/tslint*",
			"**/.eslint*",
			"**/.jshint*"
	);

	/**
	 * Files related to build tools that will change outputs (such as transpilation tools, etc.).
	 */
	private static final List<String> TOOLS_ASSETS = asList(
			"**/.browserslistrc",
			"**/browserslistrc",
			"**/.babelrc*"
	);

	/**
	 * Files related to JS/TS sources and related frameworks.
	 */
	private static final List<String> JS_ASSETS = asList(
			"**/*.js",
			"**/*.jsx",
			"**/*.cjs",
			"**/*.mjs",

			"**/*.ts",
			"**/*.tsx",
			"**/*.vue"
	);

	/**
	 * Files related to stylesheets files, including pre-preprocessors tools.
	 */
	private static final List<String> STYLESHEETS_ASSETS = asList(
			"**/*.css",
			"**/*.sass",
			"**/*.scss",
			"**/*.less"
	);

	/**
	 * Files related to templating files, including raw HTML and other template engines.
	 */
	private static final List<String> TEMPLATE_ASSETS = asList(
			"**/*.html",
			"**/*.htm",
			"**/*.hbs",
			"**/*.mustache"
	);

	/**
	 * Files related to static sources, such as images.
	 */
	private static final List<String> STATIC_ASSETS = asList(
			"**/*.svg",
			"**/*.png",
			"**/*.jpg",
			"**/*.jpeg",
			"**/*.gif",
			"**/*.ico"
	);

	/**
	 * Files related to FONTS.
	 */
	private static final List<String> FONT_ASSETS = asList(
			"**/*.otf",
			"**/*.eot",
			"**/*.ttf",
			"**/*.woff",
			"**/*.woff2"
	);

	/**
	 * Files related to tools configurationn and others.
	 */
	private static final List<String> OTHER_ASSETS = asList(
			"**/*.json",
			"**/*.yml",
			"**/*.yaml",
			"**/*.xml",
			"**/*.env",
			"**/*.graphql"
	);

	/**
	 * Files related to tools tests.
	 */
	private static final List<String> TEST_ASSETS = asList(
			"**/*.spec.js",
			"**/*.test.js",
			"**/*.spec.ts",
			"**/*.test.ts",
			"**/*.test.json",
			"**/*.spec.json",

			"**/__tests__/*.js",
			"**/__tests__/*.ts"
	);

	/**
	 * List of files that may involved during a package installation.
	 *
	 * @return List of assets to analyze.
	 */
	static Collection<String> installAssets() {
		Set<String> assets = new LinkedHashSet<>();
		assets.addAll(NPM_ASSETS);
		assets.addAll(YARN_ASSETS);
		assets.addAll(BOWER_ASSETS);
		assets.addAll(LERNA_ASSETS);
		return unmodifiableCollection(assets);
	}

	/**
	 * List of files that may involved during a bower installation.
	 *
	 * @return List of assets to analyze.
	 */
	static Collection<String> bowerAssets() {
		return unmodifiableCollection(BOWER_ASSETS);
	}

	/**
	 * List of files that may involved during a linting.
	 *
	 * @return List of assets to analyze.
	 */
	static Collection<String> linterAssets() {
		return unmodifiableCollection(LINTER_ASSETS);
	}

	/**
	 * List of files that may involved during a linting.
	 *
	 * @return List of assets to analyze.
	 */
	static Collection<String> lintAssets() {
		Set<String> assets = new LinkedHashSet<>();

		// In case of a dependency update, or a script update.
		assets.addAll(NPM_ASSETS);
		assets.addAll(YARN_ASSETS);

		// Linters configuration.
		assets.addAll(LINTER_ASSETS);

		// Lint sources.
		assets.addAll(JS_ASSETS);
		return unmodifiableCollection(assets);
	}

	/**
	 * List of files that may involved during a package build.
	 *
	 * @return List of assets to analyze.
	 */
	static Collection<String> buildAssets() {
		Set<String> assets = new LinkedHashSet<>();

		// In case of a dependency update, or a script update.
		assets.addAll(installAssets());

		// In case of a change in babel (or other tools) configuration.
		assets.addAll(TOOLS_ASSETS);

		// Sources
		assets.addAll(JS_ASSETS);
		assets.addAll(STYLESHEETS_ASSETS);
		assets.addAll(TEMPLATE_ASSETS);
		assets.addAll(STATIC_ASSETS);
		assets.addAll(FONT_ASSETS);
		assets.addAll(OTHER_ASSETS);

		return unmodifiableCollection(assets);
	}

	/**
	 * List of files that may be scanned in build assets but should be ignore because
	 * it is not really involved in a build step (test sources for example).
	 *
	 * @return List of assets to analyze.
	 */
	static Collection<String> buildIgnoreAssets() {
		Set<String> assets = new LinkedHashSet<>();
		assets.add("**/pom.xml");
		assets.addAll(testAssets());
		assets.addAll(linterAssets());
		return unmodifiableCollection(assets);
	}

	/**
	 * List of files that may involved during tests.
	 *
	 * @return List of assets to analyze.
	 */
	static Collection<String> testAssets() {
		return unmodifiableCollection(TEST_ASSETS);
	}
}
