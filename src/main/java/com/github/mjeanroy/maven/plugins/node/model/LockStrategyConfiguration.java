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

package com.github.mjeanroy.maven.plugins.node.model;

import com.github.mjeanroy.maven.plugins.node.commons.lang.ToStringBuilder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * The lock strategy configuration.
 */
public final class LockStrategyConfiguration {

	/**
	 * Lock to use on {@code install} goal.
	 */
	private LockStrategy install;

	/**
	 * Lock to use on {@code bower} goal.
	 */
	private LockStrategy bower;

	/**
	 * Lock to use on {@code preClean} goal.
	 */
	private LockStrategy preClean;

	/**
	 * Lock to use on {@code clean} goal.
	 */
	private LockStrategy clean;

	/**
	 * Lock to use on {@code lint} goal.
	 */
	private LockStrategy lint;

	/**
	 * Lock to use on {@code build} goal.
	 */
	private LockStrategy build;

	/**
	 * Lock to use on {@code package} goal (named as {@code pkg} since {@code package} is a reserved keyword in java).
	 */
	private LockStrategy pkg;

	/**
	 * Lock to use on {@code prepare} goal.
	 */
	private LockStrategy prepare;

	/**
	 * Lock to use on {@code test} goal.
	 */
	private LockStrategy test;

	/**
	 * Lock to use on {@code testE2E} goal.
	 */
	private LockStrategy testE2E;

	/**
	 * Lock to use on {@code verify} goal.
	 */
	private LockStrategy verify;

	/**
	 * Lock to use on {@code start} goal.
	 */
	private LockStrategy start;

	/**
	 * Lock to use on {@code publish} goal.
	 */
	private LockStrategy publish;

	/**
	 * Create default configuration.
	 */
	public LockStrategyConfiguration() {
	}

	/**
	 * Get {@link #install}
	 *
	 * @return {@link #install}
	 */
	public LockStrategy getInstall() {
		return install;
	}

	/**
	 * Set {@link #install}
	 *
	 * @param install New {@link #install}
	 */
	public void setInstall(LockStrategy install) {
		this.install = install;
	}

	/**
	 * Get {@link #bower}
	 *
	 * @return {@link #bower}
	 */
	public LockStrategy getBower() {
		return bower;
	}

	/**
	 * Set {@link #bower}
	 *
	 * @param bower New {@link #bower}
	 */
	public void setBower(LockStrategy bower) {
		this.bower = bower;
	}

	/**
	 * Get {@link #preClean}
	 *
	 * @return {@link #preClean}
	 */
	public LockStrategy getPreClean() {
		return preClean;
	}

	/**
	 * Set {@link #preClean}
	 *
	 * @param preClean New {@link #preClean}
	 */
	public void setPreClean(LockStrategy preClean) {
		this.preClean = preClean;
	}

	/**
	 * Get {@link #clean}
	 *
	 * @return {@link #clean}
	 */
	public LockStrategy getClean() {
		return clean;
	}

	/**
	 * Set {@link #clean}
	 *
	 * @param clean New {@link #clean}
	 */
	public void setClean(LockStrategy clean) {
		this.clean = clean;
	}

	/**
	 * Get {@link #lint}
	 *
	 * @return {@link #lint}
	 */
	public LockStrategy getLint() {
		return lint;
	}

	/**
	 * Set {@link #lint}
	 *
	 * @param lint New {@link #lint}
	 */
	public void setLint(LockStrategy lint) {
		this.lint = lint;
	}

	/**
	 * Get {@link #build}
	 *
	 * @return {@link #build}
	 */
	public LockStrategy getBuild() {
		return build;
	}

	/**
	 * Set {@link #build}
	 *
	 * @param build New {@link #build}
	 */
	public void setBuild(LockStrategy build) {
		this.build = build;
	}

	/**
	 * Get {@link #pkg}
	 *
	 * @return {@link #pkg}
	 */
	public LockStrategy getPkg() {
		return pkg;
	}

	/**
	 * Set {@link #pkg}
	 *
	 * @param pkg New {@link #pkg}
	 */
	public void setPkg(LockStrategy pkg) {
		this.pkg = pkg;
	}

	/**
	 * Get {@link #prepare}
	 *
	 * @return {@link #prepare}
	 */
	public LockStrategy getPrepare() {
		return prepare;
	}

	/**
	 * Set {@link #prepare}
	 *
	 * @param prepare New {@link #prepare}
	 */
	public void setPrepare(LockStrategy prepare) {
		this.prepare = prepare;
	}

	/**
	 * Get {@link #test}
	 *
	 * @return {@link #test}
	 */
	public LockStrategy getTest() {
		return test;
	}

	/**
	 * Set {@link #test}
	 *
	 * @param test New {@link #test}
	 */
	public void setTest(LockStrategy test) {
		this.test = test;
	}

	/**
	 * Get {@link #testE2E}
	 *
	 * @return {@link #testE2E}
	 */
	public LockStrategy getTestE2E() {
		return testE2E;
	}

	/**
	 * Set {@link #testE2E}
	 *
	 * @param testE2E New {@link #testE2E}
	 */
	public void setTestE2E(LockStrategy testE2E) {
		this.testE2E = testE2E;
	}

	/**
	 * Get {@link #start}
	 *
	 * @return {@link #start}
	 */
	public LockStrategy getStart() {
		return start;
	}

	/**
	 * Set {@link #start}
	 *
	 * @param start New {@link #start}
	 */
	public void setStart(LockStrategy start) {
		this.start = start;
	}

	/**
	 * Get {@link #verify}
	 *
	 * @return {@link #verify}
	 */
	public LockStrategy getVerify() {
		return verify;
	}

	/**
	 * Set {@link #verify}
	 *
	 * @param verify New {@link #verify}
	 */
	public void setVerify(LockStrategy verify) {
		this.verify = verify;
	}

	/**
	 * Get {@link #publish}
	 *
	 * @return {@link #publish}
	 */
	public LockStrategy getPublish() {
		return publish;
	}

	/**
	 * Set {@link #publish}
	 *
	 * @param publish New {@link #publish}
	 */
	public void setPublish(LockStrategy publish) {
		this.publish = publish;
	}

	/**
	 * Get the locking strategy for the given goal.
	 *
	 * @param goal Goal.
	 * @return Strategy, may be {@code null}.
	 */
	public LockStrategy getStrategy(String goal) {
		return toMap().get(goal.toLowerCase(Locale.ROOT));
	}

	private Map<String, LockStrategy> toMap() {
		Map<String, LockStrategy> map = new HashMap<>();
		map.put("install", install);
		map.put("bower", bower);
		map.put("pre-clean", preClean);
		map.put("clean", clean);
		map.put("lint", lint);
		map.put("prepare", prepare);
		map.put("build", build);
		map.put("package", pkg);
		map.put("test", test);
		map.put("test-e2e", testE2E);
		map.put("start", start);
		map.put("verify", verify);
		map.put("publish", publish);
		return map;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof LockStrategyConfiguration) {
			LockStrategyConfiguration c = (LockStrategyConfiguration) o;
			return Objects.equals(install, c.install)
					&& Objects.equals(bower, c.bower)
					&& Objects.equals(preClean, c.preClean)
					&& Objects.equals(clean, c.clean)
					&& Objects.equals(lint, c.lint)
					&& Objects.equals(prepare, c.prepare)
					&& Objects.equals(build, c.build)
					&& Objects.equals(pkg, c.pkg)
					&& Objects.equals(test, c.test)
					&& Objects.equals(testE2E, c.testE2E)
					&& Objects.equals(start, c.start)
					&& Objects.equals(verify, c.verify)
					&& Objects.equals(publish, c.publish);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				install,
				bower,
				preClean,
				clean,
				lint,
				prepare,
				build,
				pkg,
				test,
				testE2E,
				start,
				verify,
				publish
		);
	}

	@Override
	public String toString() {
		return ToStringBuilder.builder(getClass())
				.append("install", install)
				.append("bower", bower)
				.append("preClean", preClean)
				.append("clean", clean)
				.append("lint", lint)
				.append("prepare", prepare)
				.append("build", build)
				.append("pkg", pkg)
				.append("test", test)
				.append("testE2E", testE2E)
				.append("start", start)
				.append("verify", verify)
				.append("publish", publish)
				.build();
	}
}
