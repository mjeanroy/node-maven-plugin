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

package com.github.mjeanroy.maven.plugins.node.model;

import com.github.mjeanroy.maven.plugins.node.exceptions.ProxyException;
import org.apache.maven.settings.Proxy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static com.github.mjeanroy.maven.plugins.node.commons.PreConditions.notNull;

/**
 * Proxy Configuration.
 */
public class ProxyConfig {

	/**
	 * Extract configuration from given maven proxy settings.
	 *
	 * @param proxy Proxy.
	 * @return Proxy configuration.
	 */
	public static ProxyConfig proxyConfiguration(Proxy proxy) {
		return new ProxyConfig(
			proxy.getProtocol(),
			proxy.getUsername(),
			proxy.getPassword(),
			proxy.getHost(),
			proxy.getPort()
		);
	}

	/**
	 * Proxy protocol.
	 * Set to "http" or "https.
	 */
	private final String protocol;

	/**
	 * Proxy Username.
	 * Needed to authenticate against proxy.
	 */
	private final String username;

	/**
	 * Proxy Password.
	 * Needed to authenticate against proxy.
	 */
	private final String password;

	/**
	 * Proxy Host.
	 */
	private final String host;

	/**
	 * Proxy Port.
	 */
	private final int port;

	/**
	 * Create proxy configuration.
	 *
	 * @param username Proxy username.
	 * @param password Proxy password.
	 * @param host Proxy host.
	 * @param port Proxy port.
	 */
	private ProxyConfig(String protocol, String username, String password, String host, int port) {
		this.protocol = notNull(protocol, "Proxy protocol must not be null");
		this.host = notNull(host, "Proxy host must not be null");
		this.port = port;
		this.username = username;
		this.password = password;
	}

	/**
	 * Check if authentication settings is active.
	 *
	 * @return True if authentication is needed, false otherwise.
	 */
	public boolean hasAuthentication() {
		return username != null && !username.isEmpty();
	}

	/**
	 * Check if proxy is secure (https) or not.
	 *
	 * @return True if proxy is secure, false otherwise.
	 */
	public boolean isSecure() {
		return protocol.toLowerCase().equals("https");
	}

	/**
	 * Serialize proxy configuration to valid URI string.
	 *
	 * @return URI String.
	 */
	public String toUri() {
		String authentication = hasAuthentication() ? username + ":" + password : null;

		try {
			return new URI("http", authentication, host, port, null, null, null).toString();
		}
		catch (URISyntaxException e) {
			throw new ProxyException(e);
		}
	}

	@Override
	public String toString() {
		return toUri();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof ProxyConfig) {
			ProxyConfig p = (ProxyConfig) o;
			return Objects.equals(username, p.username) &&
				Objects.equals(password, p.password) &&
				Objects.equals(host, p.host) &&
				Objects.equals(port, p.port);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, password, host, port);
	}
}
