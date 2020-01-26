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

package com.github.mjeanroy.maven.plugins.node.models;

import com.github.mjeanroy.maven.plugins.node.model.PackageJson;
import com.github.mjeanroy.maven.plugins.node.tests.builders.PackageJsonTestBuilder;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PackageJsonTest {

	@Test
	public void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(PackageJson.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

	@Test
	public void it_should_implement_to_string() {
		PackageJson packageJson = new PackageJsonTestBuilder()
				.withName("pkg")
				.withVersion("0.0.0")
				.addDependency("jquery", "1.0.0")
				.addDevDependency("lodash", "1.0.0")
				.addScript("test", "gulp test")
				.build();

		// @formatter:off
		assertThat(packageJson).hasToString(
				"PackageJson{" +
						"name=\"pkg\", " +
						"version=\"0.0.0\", " +
						"devDependencies={lodash=1.0.0}, " +
						"dependencies={jquery=1.0.0}, " +
						"scripts={test=gulp test}" +
				"}"
		);
		// @formatter:on
	}
}
