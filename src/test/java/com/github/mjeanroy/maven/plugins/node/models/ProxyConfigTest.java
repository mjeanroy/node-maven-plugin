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

package com.github.mjeanroy.maven.plugins.node.models;

import com.github.mjeanroy.maven.plugins.node.model.ProxyConfig;
import org.apache.maven.settings.Proxy;
import org.junit.Test;

import static com.github.mjeanroy.maven.plugins.node.model.ProxyConfig.proxyConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProxyConfigTest {

	@Test
	public void it_should_create_proxy_configuration() {
		Proxy proxy = mock(Proxy.class);
		when(proxy.getProtocol()).thenReturn("http");
		when(proxy.getHost()).thenReturn("squid.local");
		when(proxy.getPort()).thenReturn(8080);
		when(proxy.getUsername()).thenReturn(null);
		when(proxy.getPassword()).thenReturn(null);

		ProxyConfig config = proxyConfiguration(proxy);

		assertThat(config.isSecure()).isFalse();
		assertThat(config.hasAuthentication()).isFalse();
		assertThat(config.toArgument()).isEqualTo("http://squid.local:8080");
		assertThat(config.toString()).isEqualTo("http://squid.local:8080");
	}

	@Test
	public void it_should_create_authenticated_proxy_configuration() {
		Proxy proxy = mock(Proxy.class);
		when(proxy.getProtocol()).thenReturn("http");
		when(proxy.getHost()).thenReturn("squid.local");
		when(proxy.getPort()).thenReturn(8080);
		when(proxy.getUsername()).thenReturn("mjeanroy");
		when(proxy.getPassword()).thenReturn("foobar");

		ProxyConfig config = proxyConfiguration(proxy);

		assertThat(config.isSecure()).isFalse();
		assertThat(config.hasAuthentication()).isTrue();
		assertThat(config.toArgument()).isEqualTo("http://mjeanroy:foobar@squid.local:8080");
		assertThat(config.toString()).isEqualTo("http://mjeanroy:********@squid.local:8080");
	}

	@Test
	public void it_should_create_secure_proxy_configuration() {
		Proxy proxy = mock(Proxy.class);
		when(proxy.getProtocol()).thenReturn("https");
		when(proxy.getHost()).thenReturn("squid.local");
		when(proxy.getPort()).thenReturn(8080);
		when(proxy.getUsername()).thenReturn("mjeanroy");
		when(proxy.getPassword()).thenReturn("foobar");

		ProxyConfig config = proxyConfiguration(proxy);

		assertThat(config.isSecure()).isTrue();
		assertThat(config.hasAuthentication()).isTrue();
		assertThat(config.toArgument()).isEqualTo("http://mjeanroy:foobar@squid.local:8080");
		assertThat(config.toString()).isEqualTo("http://mjeanroy:********@squid.local:8080");
	}
}
