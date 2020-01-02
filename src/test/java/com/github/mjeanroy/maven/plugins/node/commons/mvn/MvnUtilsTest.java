/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

package com.github.mjeanroy.maven.plugins.node.commons.mvn;

import com.github.mjeanroy.maven.plugins.node.model.ProxyConfig;
import com.github.mjeanroy.maven.plugins.node.tests.builders.ProxyTestBuilder;
import org.apache.maven.settings.Proxy;
import org.junit.Test;

import java.util.List;

import static com.github.mjeanroy.maven.plugins.node.commons.mvn.MvnUtils.findHttpActiveProfiles;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class MvnUtilsTest {

	@Test
	public void it_should_get_active_http_proxies() {
		Proxy p1 = new ProxyTestBuilder().withProtocol("http").withHost("foo1").build();
		Proxy p2 = new ProxyTestBuilder().withProtocol("https").withHost("foo2").build();

		Proxy p3 = new ProxyTestBuilder().withProtocol("http").withHost("foo3").withActive(false).build();
		Proxy p4 = new ProxyTestBuilder().withProtocol("https").withHost("foo4").withActive(false).build();
		Proxy p5 = new ProxyTestBuilder().withProtocol("socks").withHost("foo5").withActive(true).build();

		List<ProxyConfig> configs = findHttpActiveProfiles(asList(p1, p2, p3, p4, p5));

		assertThat(configs)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.extracting("host")
			.containsExactly("foo1", "foo2");
	}
}
