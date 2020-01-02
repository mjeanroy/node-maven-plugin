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

package com.github.mjeanroy.maven.plugins.node.commons.mvn;

import com.github.mjeanroy.maven.plugins.node.model.ProxyConfig;
import org.apache.maven.settings.Proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.mjeanroy.maven.plugins.node.model.ProxyConfig.proxyConfiguration;

/**
 * Static Proxy Utilities.
 */
public final class MvnUtils {

	/**
	 * The {@code "http"} protocol value.
	 */
	private static final String HTTP_SCHEME = "http";

	/**
	 * The {@code "https"} protocol value.
	 */
	private static final String HTTPS_SCHEME = "https";

	// Ensure non instantiation.
	private MvnUtils() {
	}

	/**
	 * Return configuration for active profiles.
	 *
	 * <p>
	 *
	 * Note that {@code npm} does not support socks proxy, so filter against http or https
	 * protocol is enough.
	 *
	 * @param proxies Proxies.
	 * @return Active configurations.
	 */
	public static List<ProxyConfig> findHttpActiveProfiles(Collection<Proxy> proxies) {
		List<ProxyConfig> configs = new ArrayList<>(proxies.size());
		for (Proxy proxy : proxies) {
			if (proxy.isActive()) {
				String protocol = proxy.getProtocol().toLowerCase();
				if (protocol.equals(HTTP_SCHEME) || protocol.equals(HTTPS_SCHEME)) {
					configs.add(proxyConfiguration(proxy));
				}
			}
		}

		return configs;
	}
}
