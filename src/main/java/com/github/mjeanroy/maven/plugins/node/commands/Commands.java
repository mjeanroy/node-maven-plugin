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

package com.github.mjeanroy.maven.plugins.node.commands;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.github.mjeanroy.maven.plugins.node.commons.EnvUtils.isWindows;
import static com.github.mjeanroy.maven.plugins.node.commons.ObjectUtils.firstNonNull;

/**
 * Static factories for commons commands.
 */
public final class Commands {

	// Ensure non instantiation.
	private Commands() {
	}

	/**
	 * Create new `npm` command with
	 * Executable path should be given as the first argument.
	 * If path is null, then npm executable should be globally available.
	 *
	 * @param path Path to npm executable file (optional, can be null).
	 * @return New npm command.
	 */
	public static Command npm(String path) {
		return wrap(new Command(firstNonNull(path, "npm")));
	}

	/**
	 * Create new `node` command.
	 * Executable path should be given as the first argument.
	 * If path is null, then node executable should be globally available.
	 *
	 * @param path Path to node executable file (optional, can be null).
	 * @return New node command.
	 */
	public static Command node(String path) {
		return wrap(new Command(firstNonNull(path, "node")));
	}

	/**
	 * Create new `nvm` command.
	 * Executable path should be given as the first argument.
	 * If path is null, then nvm executable should be globally available.
	 *
	 * @param path Path to nvm executable file (optional, can be null).
	 * @return New node command.
	 */
	public static Command nvm(String path) {
		String sh = firstNonNull(path, "~/.nvm/nvm.sh") + ";";
		Command nvm = new Command("/bin/bash");
		nvm.addArgument("-c");
		nvm.addArgument(sh);
		nvm.addArgument("nvm");
		return wrap(nvm);
	}

	private static Command wrap(Command command) {
		return isWindows() ? new MsDos(command) : command;
	}

	private static class MsDos extends Command {
		private final Command cmd;

		private MsDos(Command cmd) {
			super("cmd");
			super.addArgument("/C");
			this.cmd = cmd;
		}

		@Override
		public String getName() {
			return cmd.getName();
		}

		@Override
		public void addArgument(CommandArg argument) {
			cmd.addArgument(argument);
		}

		@Override
		public void addArgument(String argument) {
			cmd.addArgument(argument);
		}

		@Override
		public Collection<String> getArguments() {
			List<String> args = new LinkedList<>(super.getArguments());
			args.add(cmd.getExecutable());
			args.addAll(cmd.getArguments());
			return args;
		}

		@Override
		public String toString() {
			return cmd.toString();
		}
	}
}
