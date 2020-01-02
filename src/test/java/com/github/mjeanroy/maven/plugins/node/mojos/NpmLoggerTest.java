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

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class NpmLoggerTest {

	private Log log;
	private NpmLogger npmLogger;

	@Before
	public void setUp() {
		log = mock(Log.class);
		npmLogger = NpmLogger.npmLogger(log);
	}

	@Test
	public void it_should_use_error_level() {
		String line = "npm ERR! There is likely additional logging output above.";

		npmLogger.process(line);

		verify(log).error(line);
		verify(log, never()).warn(anyString());
		verify(log, never()).info(anyString());
	}

	@Test
	public void it_should_use_error_level_with_yarn_error() {
		String line = "error Command \"start\" not found.";

		npmLogger.process(line);

		verify(log).error(line);
		verify(log, never()).warn(anyString());
		verify(log, never()).info(anyString());
	}

	@Test
	public void it_should_use_warn_level() {
		String line = "npm WARN Local package.json exists, but node_modules missing, did you mean to install?";

		npmLogger.process(line);

		verify(log).warn(line);
		verify(log, never()).error(anyString());
		verify(log, never()).info(anyString());
	}

	@Test
	public void it_should_use_warn_level_with_yarn_warning() {
		String line = "warning Command \"start\" not found.";

		npmLogger.process(line);

		verify(log).warn(line);
		verify(log, never()).error(anyString());
		verify(log, never()).info(anyString());
	}

	@Test
	public void it_should_use_info_level_by_default() {
		String line = "> gulp clean";

		npmLogger.process(line);

		verify(log).info(line);
		verify(log, never()).error(anyString());
		verify(log, never()).warn(anyString());
	}
}
