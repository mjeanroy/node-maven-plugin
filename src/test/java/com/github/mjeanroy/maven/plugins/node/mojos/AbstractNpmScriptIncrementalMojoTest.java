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

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.github.mjeanroy.maven.plugins.node.tests.DigestTestUtils.computeMd5;
import static com.github.mjeanroy.maven.plugins.node.tests.FileTestUtils.join;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.readPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;

public abstract class AbstractNpmScriptIncrementalMojoTest<T extends AbstractNpmScriptMojo> extends AbstractNpmScriptMojoTest<T> {

	/**
	 * Verify the state file generated by this mojo execution.
	 *
	 * @param mojo The mojo.
	 * @param entries Expected file entries.
	 * @throws Exception If something bas happen while reading state file.
	 */
	void verifyStateFile(T mojo, Collection<File> entries) throws Exception {
		File stateFile = stateFile(mojo);
		assertThat(stateFile).exists();

		List<String> lines = Files.readAllLines(stateFile.toPath(), Charset.defaultCharset());
		assertThat(lines).hasSameSizeAs(entries);

		Collections.sort(lines);

		int i = 0;
		for (File entry : entries) {
			assertThat(lines.get(i)).isEqualTo(
					entry.getAbsolutePath() + "::" + computeMd5(entry)
			);

			i++;
		}
	}

	/**
	 * Get the state file.
	 *
	 * @param mojo The mojo.
	 * @return The state file.
	 */
	File stateFile(T mojo) {
		File workingDirectory = readPrivate(mojo, "workingDirectory");
		return join(workingDirectory, "target", "node-maven-plugin", script());
	}

	/**
	 * Reset mojo to its initial state.
	 *
	 * @param mojo The mojo to reset.
	 */
	void resetMojo(T mojo) {
		mojo.setPluginContext(new HashMap<>());
		reset(
				readPrivate(mojo, "log"),
				readPrivate(mojo, "executor")
		);
	}
}
