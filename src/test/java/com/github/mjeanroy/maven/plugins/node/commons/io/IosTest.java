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

package com.github.mjeanroy.maven.plugins.node.commons.io;

import org.junit.Test;

import java.io.File;
import java.util.Map;

import static com.github.mjeanroy.maven.plugins.node.tests.FileTestUtils.getFileFromClasspath;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class IosTest {

	@Test
	public void it_should_compute_md5_hash_of_given_file() {
		File file = getFileFromClasspath("/test.json");
		String md5 = Ios.md5(file);
		assertThat(md5).isEqualTo("bb6b928e501ba98b81d7b686590b1c59");
	}

	@Test
	public void it_should_compute_md5_hash_of_given_files() {
		File file1 = getFileFromClasspath("/test.json");
		File file2 = getFileFromClasspath("/error.sh");
		File file3 = getFileFromClasspath("/success.sh");

		Map<String, String> md5 = Ios.md5(asList(file1, file2, file3));

		assertThat(md5).hasSize(3)
			.contains(
					entry(file1.getAbsolutePath(), "bb6b928e501ba98b81d7b686590b1c59"),
					entry(file2.getAbsolutePath(), "3447717aff3f978dab310b05d2a6fccb"),
					entry(file3.getAbsolutePath(), "b00de0da9a17d258d728fa296578ff98")
			);
	}
}
