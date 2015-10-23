package com.github.mjeanroy.maven.plugins.node.mojos;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Build Mojo.
 * Basically, it only runs `npm run-script build`.
 * Executed will be logged to the console.
 *
 * This mojo will run automatically during the compile phase and does not
 * require online connection.
 */
@Mojo(
		name = "build",
		defaultPhase = LifecyclePhase.COMPILE,
		requiresOnline = false
)
public class BuildMojo extends AbstractNpmMojo {

	public BuildMojo() {
		super("build");
	}
}
