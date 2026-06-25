## node-maven-plugin

[![Build Status](https://travis-ci.org/mjeanroy/node-maven-plugin.svg?branch=master)](https://travis-ci.org/mjeanroy/node-maven-plugin)

### Introduction

This plugin allow you to run `npm` commands into your maven project.

### Why?

Most npm project are standards and define some classic phase:
- Dependencies installation (npm/yarn/pnpm).
- Clean previous build files (using `rimraf`, `del`, etc.).
- Linting (using tools such as `jshint`, `eslint`, `csslint`, etc.).
- Tests (using `karma`, `jest` or anything else).
- Integration tests (with `protactor`, `webdriver.io` or any other functional testing framework).
- Building (concatenation, minification, transpilation, etc. with `webpack` or any other module bundler).

These steps can be easily integrated into the build lifecycle of maven:

| Maven Phase        | Plugin goal      |
|--------------------|------------------|
| initialize         | node:install     |
| clean              | node:clean       |
| process-sources    | node:lint        |
| compile            | node:build       |
| test               | node:test        |
| integration-test   | node:test-e2e    |
| deploy             | node:publish     |

This is exactly what this plugin does!

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
        <version>0.2.0</version>
        <executions>
          <execution>
            <id>run-npm</id>
            <goals>
              <goal>check</goal>
              <goal>pre-clean</goal>
              <goal>install</goal>
              <goal>clean</goal>
              <goal>lint</goal>
              <goal>test</goal>
              <goal>build</goal>
              <goal>publish</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

If you want to bound each phase, here is a configuration using `extensions` flag:

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
        <version>0.2.0</version>
        <extensions>true</extensions>
        <configuration>
          <failOnMissingScript>false</failOnMissingScript>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

Here is a short description of each goal:

| Goal         | Phase              |                                                                        |
|--------------|--------------------|------------------------------------------------------------------------|
| check        | `validate`         | Check that `node` and `npm` are available (and npm.client if enabled). |
| pre-clean    | `pre-clean`        | Run `npm install` (or `${npm.client} install`).                        |
| install      | `initialize`       | Run `npm install` (or `${npm.client} install`).                        |
| lint         | `process-sources`  | Run `npm run lint` (or `${npm.client} run lint`).                      |
| clean        | `clean`            | Run `npm run clean` (or `${npm.client} run clean`).                    |
| build        | `compile`          | Run `npm run build` (or `${npm.client} run build`).                    |
| test         | `test`             | Run `npm test` (or `${npm.client} test`).                              |
| test-e2e     | `integration-test` | Run `npm run test-e2e` (or `${npm.client} run test-e2e`).              |
| publish      | `deploy`           | Run `npm publish` (or `${npm.client} publish`).                        |
| start        | `process-classes`  | Run `npm start` (or `${npm.client} start`).                            |
| dependencies |                    | Display `npm` (or `${npm.client}`) dependencies.                       |

*Important*: `npm install` (or `${npm.client} install`) is run during `pre-clean` phase **and** `initialize` phase because each phase is
bound to a different step, and there's good chances that you need to install npm dependencies before
running clean script (no worry, it will run one time only during the build).

Note that each script should be defined in `package.json` file:

```json
{
  "name": "project-artifactid",
  "version": "0.1.0-SNAPSHOT",
  "scripts": {
    "clean": "gulp clean",
    "test": "gulp test",
    "test-e2e": "gulp test-e2e",
    "build": "gulp build",
    "start": "gulp serve"
  }
}
```

### Options

Plugin can use these options:

| Option              | Default              |                                                                                                                                                        |
|---------------------|----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| workingDirectory    | `${project.basedir}` | Directory where package.json should be available.                                                                                                      |
| node.path           | `node`               | Path to `node` executable (by default, node should be available globally)                                                                              |
| npm.client          | `npm`                | Path to `npm` client executable (by default, npm should be available globally). Npm client can be yarn, pnpm, or path to npm client.                   |
| color               | `false`              | If set to `true` (default), argument `--no-color` is appended to each script command.                                                                  |
| failOnError         | `true`               | If set to `true`, build will not fail if npm command fail.                                                                                             |
| failOnMissingScript | `true`               | If set to `true`, missing npm command will not fail the build.                                                                                         |
| ignoreProxies       | `true`               | If set to `false` , maven proxy settings will be appended to npm commands (default is `true`, since proxies should probably defined in `.npmrc` file). |
| addMavenArgument    | `true`               | If set to `true` , add `--maven` argument to each npm command                                                                                          |

*Important*: Argument `--maven` is automatically appended to each script command (any script can check this argument to set default options on different plugins).

### License

MIT License.

### Contributing

If you found a bug or you thing something is missing, feel free to contribute and submit an issue or a pull request.
