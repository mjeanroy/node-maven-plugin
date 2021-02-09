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

package com.github.mjeanroy.maven.plugins.node.commons.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import static com.github.mjeanroy.maven.plugins.node.tests.FileTestUtils.absolutePath;
import static com.github.mjeanroy.maven.plugins.node.tests.FileTestUtils.getFileFromClasspath;
import static com.github.mjeanroy.maven.plugins.node.tests.StringTestUtils.join;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class FilesTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void it_should_normalize_file_path() {
		assertThat(Files.getNormalizeAbsolutePath(null)).isEqualTo(null);
		assertThat(Files.getNormalizeAbsolutePath(new File("/foo.txt"))).isEqualTo(absolutePath("/foo.txt"));
		assertThat(Files.getNormalizeAbsolutePath(new File("/./foo.txt"))).isEqualTo(absolutePath("/foo.txt"));
		assertThat(Files.getNormalizeAbsolutePath(new File("/bar/.././foo.txt"))).isEqualTo(absolutePath("/foo.txt"));
	}

	@Test
	public void it_should_join_path_to_file() {
		File root = getFileFromClasspath("/");
		File file = Files.join(root, "success.sh");
		assertThat(file).isNotNull().exists().hasName("success.sh");
	}

	@Test
	public void it_should_join_sub_path_to_file() {
		File root = getFileFromClasspath("/");
		File file = Files.join(root, "fixtures","success.sh");
		assertThat(file).isNotNull().doesNotExist().hasName("success.sh");
		assertThat(file.getParentFile()).isNotNull().doesNotExist().hasName("fixtures");
	}

	@Test
	public void it_should_write_lines_to_file() throws Exception {
		Charset charset = Charset.defaultCharset();
		List<String> lines = asList(
				"line 1",
				"line 2"
		);

		File dir = temporaryFolder.newFolder("test");
		File out = new File(dir, "test.txt");

		Files.writeLines(lines, out, charset);

		assertThat(out).exists().hasContent(join(lines, System.lineSeparator()));
	}

	@Test
	public void it_should_write_lines_to_file_creating_parent_directory_if_it_does_not_exist() throws Exception {
		Charset charset = Charset.defaultCharset();
		List<String> lines = asList(
				"line 1",
				"line 2"
		);

		File root = temporaryFolder.newFolder("test");
		File dir = new File(root, "dir");
		File out = new File(dir, "test.txt");

		Files.writeLines(lines, out, charset);

		assertThat(out).exists().hasContent(join(lines, System.lineSeparator()));
	}

	@Test
	public void it_should_delete_file_if_it_exists() throws Exception {
		File file = temporaryFolder.newFile("test.txt");
		assertThat(file).exists();

		Files.deleteFile(file);
		assertThat(file).doesNotExist();
	}

	@Test
	public void it_should_not_fail_to_delete_file_if_it_does_not_exist() throws Exception {
		File dir = temporaryFolder.newFolder("test");
		File file = new File(dir, "test.txt");
		assertThat(file).doesNotExist();

		Files.deleteFile(file);
		assertThat(file).doesNotExist();
	}
}
