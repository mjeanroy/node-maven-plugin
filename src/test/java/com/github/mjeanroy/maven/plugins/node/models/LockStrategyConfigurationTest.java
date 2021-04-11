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

package com.github.mjeanroy.maven.plugins.node.models;

import com.github.mjeanroy.maven.plugins.node.model.LockStrategy;
import com.github.mjeanroy.maven.plugins.node.model.LockStrategyConfiguration;
import com.github.mjeanroy.maven.plugins.node.mojos.*;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LockStrategyConfigurationTest {

	@Test
	public void it_should_create_default_configuration() {
		LockStrategyConfiguration configuration = new LockStrategyConfiguration();
		assertThat(configuration.getInstall()).isNull();
		assertThat(configuration.getBower()).isNull();
		assertThat(configuration.getPreClean()).isNull();
		assertThat(configuration.getClean()).isNull();
		assertThat(configuration.getLint()).isNull();
		assertThat(configuration.getPrepare()).isNull();
		assertThat(configuration.getBuild()).isNull();
		assertThat(configuration.getPkg()).isNull();
		assertThat(configuration.getTest()).isNull();
		assertThat(configuration.getTestE2E()).isNull();
		assertThat(configuration.getStart()).isNull();
		assertThat(configuration.getVerify()).isNull();
		assertThat(configuration.getPublish()).isNull();
	}

	@Test
	public void it_should_get_strategy_of_given_goal() {
		LockStrategyConfiguration configuration = new LockStrategyConfiguration();
		configuration.setInstall(LockStrategy.WRITE);
		configuration.setBower(LockStrategy.WRITE);
		configuration.setPreClean(LockStrategy.WRITE);
		configuration.setClean(LockStrategy.WRITE);
		configuration.setLint(LockStrategy.WRITE);
		configuration.setBuild(LockStrategy.READ);
		configuration.setPkg(LockStrategy.READ);
		configuration.setPrepare(LockStrategy.READ);
		configuration.setTest(LockStrategy.READ);
		configuration.setTestE2E(LockStrategy.READ);
		configuration.setStart(LockStrategy.WRITE);
		configuration.setVerify(LockStrategy.READ);
		configuration.setPublish(LockStrategy.READ);

		assertThat(configuration.getStrategy(InstallMojo.GOAL_NAME)).isEqualTo(LockStrategy.WRITE);
		assertThat(configuration.getStrategy(BowerMojo.GOAL_NAME)).isEqualTo(LockStrategy.WRITE);
		assertThat(configuration.getStrategy(PreCleanMojo.GOAL_NAME)).isEqualTo(LockStrategy.WRITE);
		assertThat(configuration.getStrategy(CleanMojo.GOAL_NAME)).isEqualTo(LockStrategy.WRITE);
		assertThat(configuration.getStrategy(LintMojo.GOAL_NAME)).isEqualTo(LockStrategy.WRITE);
		assertThat(configuration.getStrategy(BuildMojo.GOAL_NAME)).isEqualTo(LockStrategy.READ);
		assertThat(configuration.getStrategy(PackageMojo.GOAL_NAME)).isEqualTo(LockStrategy.READ);
		assertThat(configuration.getStrategy(PrepareMojo.GOAL_NAME)).isEqualTo(LockStrategy.READ);
		assertThat(configuration.getStrategy(TestMojo.GOAL_NAME)).isEqualTo(LockStrategy.READ);
		assertThat(configuration.getStrategy(TestE2EMojo.GOAL_NAME)).isEqualTo(LockStrategy.READ);
		assertThat(configuration.getStrategy(StartMojo.GOAL_NAME)).isEqualTo(LockStrategy.WRITE);
		assertThat(configuration.getStrategy(VerifyMojo.GOAL_NAME)).isEqualTo(LockStrategy.READ);
		assertThat(configuration.getStrategy(PublishMojo.GOAL_NAME)).isEqualTo(LockStrategy.READ);

		// Special Case
		assertThat(configuration.getStrategy("package")).isEqualTo(LockStrategy.READ);
	}

	@Test
	public void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(LockStrategyConfiguration.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

	@Test
	public void it_should_implement_to_string() {
		LockStrategyConfiguration configuration = new LockStrategyConfiguration();
		configuration.setInstall(LockStrategy.WRITE);
		configuration.setBower(LockStrategy.WRITE);
		configuration.setPreClean(LockStrategy.WRITE);
		configuration.setClean(LockStrategy.WRITE);
		configuration.setLint(LockStrategy.WRITE);
		configuration.setBuild(LockStrategy.READ);
		configuration.setPkg(LockStrategy.READ);
		configuration.setPrepare(LockStrategy.READ);
		configuration.setTest(LockStrategy.READ);
		configuration.setTestE2E(LockStrategy.READ);
		configuration.setStart(LockStrategy.WRITE);
		configuration.setVerify(LockStrategy.READ);
		configuration.setPublish(LockStrategy.READ);

		// @formatter:off
		assertThat(configuration).hasToString(
				"LockStrategyConfiguration{" +
					"install=WRITE, " +
					"bower=WRITE, " +
					"preClean=WRITE, " +
					"clean=WRITE, " +
					"lint=WRITE, " +
					"prepare=READ, " +
					"build=READ, " +
					"pkg=READ, " +
					"test=READ, " +
					"testE2E=READ, " +
					"start=WRITE, " +
					"verify=READ, " +
					"publish=READ" +
				"}"
		);
		// @formatter:on
	}
}
