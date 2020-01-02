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

package com.github.mjeanroy.maven.plugins.node.commands;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandResultTest {

	@Test
	public void it_should_create_result() {
		CommandResult result = new CommandResult(0);
		assertThat(result.getStatus()).isZero();
	}

	@Test
	public void it_should_check_if_result_is_a_success() {
		CommandResult r1 = new CommandResult(0);
		assertThat(r1.getStatus()).isZero();
		assertThat(r1.isSuccess()).isTrue();

		CommandResult r2 = new CommandResult(1);
		assertThat(r2.getStatus()).isEqualTo(1);
		assertThat(r2.isSuccess()).isFalse();
	}

	@Test
	public void it_should_check_if_result_is_a_failure() {
		CommandResult r1 = new CommandResult(1);
		assertThat(r1.getStatus()).isEqualTo(1);
		assertThat(r1.isFailure()).isTrue();

		CommandResult r2 = new CommandResult(0);
		assertThat(r2.getStatus()).isZero();
		assertThat(r2.isFailure()).isFalse();
	}

	@Test
	public void it_should_display_string_representation() {
		CommandResult result = new CommandResult(1);
		assertThat(result.toString())
				.isNotNull()
				.isNotEmpty()
				.isEqualTo("Status: 1");
	}

	@Test
	public void it_should_implement_equals() {
		CommandResult r1 = new CommandResult(1);
		CommandResult r2 = new CommandResult(1);
		CommandResult r3 = new CommandResult(1);
		CommandResult r4 = new CommandResult(0);

		// By Type
		assertThat(r1).isNotEqualTo(0);

		// Status equality
		assertThat(r1).isNotEqualTo(r4);
		assertThat(r4).isNotEqualTo(r1);

		// Symmetric
		assertThat(r1).isEqualTo(r1);

		// Reflexive
		assertThat(r1).isEqualTo(r2);
		assertThat(r2).isEqualTo(r1);

		// Transiting
		assertThat(r1).isEqualTo(r2);
		assertThat(r2).isEqualTo(r3);
		assertThat(r1).isEqualTo(r3);
	}

	@Test
	public void it_should_implement_hash_code() {
		CommandResult r1 = new CommandResult(1);
		CommandResult r2 = new CommandResult(1);
		assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
	}
}
