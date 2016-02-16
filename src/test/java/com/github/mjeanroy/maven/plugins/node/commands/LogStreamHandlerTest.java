/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Mickael Jeanroy
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

package com.github.mjeanroy.maven.plugins.node.commands;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LogStreamHandlerTest {

	@Test
	public void it_should_log_warn() {
		String message = "npm WARN message";
		Log logger = mock(Log.class);
		LogStreamHandler handler = new LogStreamHandler(logger);

		handler.processLine(message, 0);

		verify(logger).warn(message);
	}

	@Test
	public void it_should_log_error() {
		String message = "npm ERR! Test failed";
		Log logger = mock(Log.class);
		LogStreamHandler handler = new LogStreamHandler(logger);

		handler.processLine(message, 0);

		verify(logger).error(message);
	}

	@Test
	public void it_should_log_info() {
		String message = "Running: npm install --no-color --maven";
		Log logger = mock(Log.class);
		LogStreamHandler handler = new LogStreamHandler(logger);

		handler.processLine(message, 0);

		verify(logger).info(message);
	}

}
