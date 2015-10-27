/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.maven.plugins.node.commons;

import com.github.mjeanroy.maven.plugins.node.model.ProxyConfig;
import org.apache.maven.settings.Proxy;
import org.junit.Test;

import java.util.List;

import static com.github.mjeanroy.maven.plugins.node.commons.ProxyUtils.findHttpActiveProfiles;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProxyConfigUtilsTest {

	@Test
	public void it_should_get_active_http_proxies() {
		Proxy p1 = createProxy("http", "foo1", true);
		Proxy p2 = createProxy("https", "foo2", true);

		Proxy p3 = createProxy("http", "foo3", false);
		Proxy p4 = createProxy("https", "foo4", false);
		Proxy p5 = createProxy("socks", "foo5", true);

		List<ProxyConfig> configs = findHttpActiveProfiles(asList(p1, p2, p3, p4, p5));

		assertThat(configs)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.extracting("host")
			.containsExactly("foo1", "foo2");
	}

	private Proxy createProxy(String protocol, String host, boolean active) {
		Proxy proxy = mock(Proxy.class);
		when(proxy.getProtocol()).thenReturn(protocol);
		when(proxy.isActive()).thenReturn(active);
		when(proxy.getHost()).thenReturn(host);
		return proxy;
	}
}
