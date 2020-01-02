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

package com.github.mjeanroy.maven.plugins.node.tests.builders;

import org.apache.maven.settings.Proxy;

/**
 * Fluent Builder for {@link Proxy}.
 */
public class ProxyTestBuilder {

	/**
	 * Create new proxy instance.
	 *
	 * @param protocol Proxy Protocol.
	 * @param host Proxy Host.
	 * @param port Proxy Port.
	 * @param username Proxy Username.
	 * @param password Proxy Password.
	 * @return The new proxy instance.
	 */
	public static Proxy newProxy(String protocol, String host, int port, String username, String password) {
		return new ProxyTestBuilder().withProtocol(protocol).withHost(host).withPort(port).withUsername(username).withPassword(password).build();
	}

	/**
	 * Create new proxy instance with default values.
	 *
	 * @return The new proxy instance.
	 */
	public static Proxy defaultHttpProxy() {
		return newProxy("http", "localhost", 8080, "mjeanroy", "Azerty123!");
	}

	/**
	 * Create new proxy instance with default values.
	 *
	 * @return The new proxy instance.
	 */
	public static Proxy defaultHttpsProxy() {
		return newProxy("https", "localhost", 8080, "mjeanroy", "Azerty123!");
	}

	/**
	 * Proxy Protocol.
	 *
	 * @see Proxy#setProtocol(String) 
	 */
	private String protocol;

	/**
	 * Proxy Host.
	 *
	 * @see Proxy#setHost(String) 
	 */
	private String host;

	/**
	 * Proxy Port.
	 *
	 * @see Proxy#setPort(int)
	 */
	private int port;

	/**
	 * Proxy Username.
	 *
	 * @see Proxy#setUsername(String)
	 */
	private String username;

	/**
	 * Proxy Password.
	 *
	 * @see Proxy#setPassword(String) 
	 */
	private String password;

	/**
	 * Active State.
	 *
	 * @see Proxy#isActive()
	 */
	private boolean active;

	/**
	 * Create proxy with defaut configuration.
	 */
	public ProxyTestBuilder() {
		this.protocol = "http";
		this.host = "localhost";
		this.port = 80;
		this.username = "mjeanroy";
		this.password = "Azerty123!";
		this.active = true;
	}

	/**
	 * Set new {@link #protocol}
	 *
	 * @param protocol New {@link #protocol}
	 * @return The builder.
	 */
	public ProxyTestBuilder withProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	/**
	 * Set new {@link #host}
	 *
	 * @param host New {@link #host}
	 * @return The builder.
	 */
	public ProxyTestBuilder withHost(String host) {
		this.host = host;
		return this;
	}

	/**
	 * Set new {@link #port}
	 *
	 * @param port New {@link #port}
	 * @return The builder.
	 */
	public ProxyTestBuilder withPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * Set new {@link #username}
	 *
	 * @param username New {@link #username}
	 * @return The builder.
	 */
	public ProxyTestBuilder withUsername(String username) {
		this.username = username;
		return this;
	}

	/**
	 * Set new {@link #password}
	 *
	 * @param password New {@link #password}
	 * @return The builder.
	 */
	public ProxyTestBuilder withPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * Set new {@link #active} state
	 *
	 * @param active New {@link #active} value
	 * @return The builder.
	 */
	public ProxyTestBuilder withActive(boolean active) {
		this.active = active;
		return this;
	}

	/**
	 * Build {@link Proxy} instance.
	 *
	 * @return The new instance.
	 */
	public Proxy build() {
		Proxy proxy = new Proxy();
		proxy.setProtocol(protocol);
		proxy.setHost(host);
		proxy.setPort(port);
		proxy.setUsername(username);
		proxy.setPassword(password);
		proxy.setActive(active);
		return proxy;
	}
}
