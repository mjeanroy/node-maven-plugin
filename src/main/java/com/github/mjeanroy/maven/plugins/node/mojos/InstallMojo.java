package com.github.mjeanroy.maven.plugins.node.mojos;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Install Mojo.
 * Basically, it only runs `npm install` to install
 * mandatory dependencies.
 * Executed will be logged to the console.
 *
 * This mojo will run automatically during the initialize phase and
 * **require** online connection.
 */
@Mojo(
		name = "install",
		defaultPhase = LifecyclePhase.INITIALIZE,
		requiresOnline = true
)
public class InstallMojo extends AbstractNpmMojo {

	/**
	 * Create Mojo.
	 */
	public InstallMojo() {
		super("install");
	}
}
