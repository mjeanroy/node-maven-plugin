## node-maven-plugin

[![Build Status](https://travis-ci.org/mjeanroy/node-maven-plugin.svg?branch=master)](https://travis-ci.org/mjeanroy/node-maven-plugin)

### Introduction

This plugin allow you to run `npm` commands into your maven project.

### Why ?

Most npm project follow define some classic build:
- Dependencies installation (npm and maybe bower dependencies).
- Clean previous build files.
- Linting (using tools such as `jshint`, `eslint`, `csslint`, etc.).
- Tests (using `karma`, `jest` or anything else).
- Integration tests (maybe `protactor` ?).
- Building (concatenation, minification, transpilation, etc.).

These steps can be easily integrated into the build lifecycle of maven:

| Maven Phase        | Plugin goal      |
|--------------------|------------------|
| initialize         | node:install     |
| initialize         | node:bower       |
| clean              | node:clean       |
| process-sources    | node:lint        |
| test               | node:test        |
| integration-test   | node:test-e2e    |
| compile            | node:build       |

This is exactly what this plugin does !

### Instruction

By default, each plugin goal is plugged into the build lifecycle (as described above).
It can be used very easily:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.groupid</groupId>
  <artifactId>project-artifactid</artifactId>
  <version>0.1.0-SNAPSHOT</version>
  <packaging>war</packaging>

  <build>
    <plugins>
      <plugin>
        <groupId>com.github.mjeanroy</groupId>
        <artifactId>node-maven-plugin</artifactId>
        <version>0.1.1</version>
        <executions>
          <execution>
            <id>run-npm</id>
            <goals>
              <goal>check</goal>
              <goal>pre-clean</goal>
              <goal>install</goal>
              <goal>bower</goal>
              <goal>clean</goal>
              <goal>lint</goal>
              <goal>test</goal>
              <goal>build</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

Here is a short description of each goal:

| Goal               |                                           |
|--------------------|-------------------------------------------|
| check              | Check that node and npm are available     |
| pre-clean          | Run `npm install`                         |
| install            | Run `npm install`                         |
| bower              | Run `npm run-script bower`                |
| clean              | Run `npm run-script clean`                |
| test               | Run `npm test`                            |
| integration-test   | Run `npm run-script test-e2e`             |
| build              | Run `npm run-script build`                |

*Important*: `npm install` is run during `pre-clean` phase and `initialize` phase because each phase is
bound to a different step (and there's good chances that you need to install npm dependencies before
running clean script).

Note that each script should be defined in `package.json` file:

```json
{
  "name": "project-artifactid",
  "version": "0.1.0-SNAPSHOT",
  "scripts": {
    "clean": "gulp clean",
    "bower": "bower install",
    "test": "gulp test",
    "test-e2e": "gulp test-e2e",
    "build": "gulp build"
  }
}
```

### Options

Plugin can use these options:

| Option               |                                                                                                                                                   |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| workingDirectory     | Directory where package.json should be available (default is project directory)                                                                   |
| node.path            | Path to node executable (by default, node should be available globally)                                                                           |
| npm.path             | Path npm executable (by default, npm should be available globally)                                                                                |
| color                | If set to true (default), argument `--no-color` is appended to each script command.                                                               |
| failOnError          | If set to true, build will not fail if npm command fail (default is true).                                                                        |
| failOnMissingScript  | If set to true, missing npm command will not fail the build (default is true).                                                                    |
| ignoreProxies        | If set to false, maven proxy settings will be appended to npm commands (default is true, since proxies should probably defined in `.npmrc` file). |

*Important*: Argument `--maven` is automatically appended to each script command (any script can check this argument to set default options on different plugins).

### License

MIT License.

### Contributing

If you found a bug or you thing something is missing, feel free to contribute and submit an issue or a pull request.
