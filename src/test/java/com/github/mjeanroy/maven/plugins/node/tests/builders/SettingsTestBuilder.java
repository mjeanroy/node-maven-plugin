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
import org.apache.maven.settings.Settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Fluent Builder for {@link Proxy}.
 */
public class SettingsTestBuilder {

	/**
	 * Create new {@link Settings} instance with given proxy entries.
	 *
	 * @param proxy Proxy entry.
	 * @param others Other proxy entries.
	 * @return Maven Settings.
	 */
	public static Settings newSettings(Proxy proxy, Proxy... others) {
		return new SettingsTestBuilder().addProxy(proxy, others).build();
	}

	/**
	 * List of available proxies.
	 *
	 * @see Settings#setProxies(List)
	 */
	private final List<Proxy> proxies;

	/**
	 * Create settings with default values.
	 */
	public SettingsTestBuilder() {
		this.proxies = new ArrayList<>();
	}

	/**
	 * Add new proxy entries.
	 *
	 * @param proxy Proxy entry.
	 * @param others Other proxy entries.
	 * @return The builder.
	 */
	public SettingsTestBuilder addProxy(Proxy proxy, Proxy... others) {
		proxies.add(proxy);
		Collections.addAll(proxies, others);
		return this;
	}

	/**
	 * Add new proxy entries.
	 *
	 * @param proxies Proxy entries.
	 * @return The builder.
	 */
	public SettingsTestBuilder addProxy(Collection<Proxy> proxies) {
		this.proxies.addAll(proxies);
		return this;
	}

	/**
	 * Build {@link Proxy} instance.
	 *
	 * @return The new instance.
	 */
	public Settings build() {
		Settings settings = new Settings();
		settings.setProxies(new ArrayList<>(proxies));
		return settings;
	}
}
