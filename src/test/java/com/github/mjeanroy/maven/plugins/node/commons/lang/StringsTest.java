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

package com.github.mjeanroy.maven.plugins.node.commons.lang;

import org.junit.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class StringsTest {

	@Test
	public void it_should_trim_value() {
		assertThat(Strings.trim(null)).isNull();
		assertThat(Strings.trim("")).isEqualTo("");
		assertThat(Strings.trim(" value ")).isEqualTo("value");
	}

	@Test
	public void it_should_join_inputs() {
		assertThat(Strings.join(Collections.<String>emptyList(), ",")).isEqualTo("");
		assertThat(Strings.join(singletonList("one"), ",")).isEqualTo("one");
		assertThat(Strings.join(asList("one", "two", "three"), ",")).isEqualTo("one,two,three");
	}

	@Test
	public void it_should_capitalize_input() {
		assertThat(Strings.capitalize(null)).isNull();
		assertThat(Strings.capitalize("")).isEmpty();
		assertThat(Strings.capitalize("x")).isEqualTo("X");
		assertThat(Strings.capitalize("test")).isEqualTo("Test");
	}

	@Test
	public void it_should_un_captitalize_input() {
		assertThat(Strings.uncapitalize(null)).isNull();
		assertThat(Strings.uncapitalize("")).isEmpty();
		assertThat(Strings.uncapitalize("x")).isEqualTo("x");
		assertThat(Strings.uncapitalize("X")).isEqualTo("x");
		assertThat(Strings.uncapitalize("test")).isEqualTo("test");

		assertThat(Strings.uncapitalize("Bower")).isEqualTo("bower");
		assertThat(Strings.uncapitalize("Build")).isEqualTo("build");
		assertThat(Strings.uncapitalize("Clean")).isEqualTo("clean");
		assertThat(Strings.uncapitalize("Install")).isEqualTo("install");
		assertThat(Strings.uncapitalize("Lint")).isEqualTo("lint");
		assertThat(Strings.uncapitalize("Package")).isEqualTo("package");
		assertThat(Strings.uncapitalize("PreClean")).isEqualTo("preClean");
		assertThat(Strings.uncapitalize("Prune")).isEqualTo("prune");
		assertThat(Strings.uncapitalize("Publish")).isEqualTo("publish");
		assertThat(Strings.uncapitalize("Start")).isEqualTo("start");
		assertThat(Strings.uncapitalize("TestE2E")).isEqualTo("testE2E");
		assertThat(Strings.uncapitalize("Test")).isEqualTo("test");
	}
}
