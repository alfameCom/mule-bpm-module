# Mule BPM Module

[![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/alfameCom/mule-bpm-module/blob/master/LICENSE.txt)
![Java CI with Maven](https://github.com/alfameCom/mule-bpm-module/workflows/Java%20CI%20with%20Maven/badge.svg)

Mule BPM Module extends Mule 4 by adding support for process-oriented integrations.

The actual Business Process Management (BPM) support is based on [Flowable Engine](https://github.com/flowable/flowable-engine) and is not currently interchangeable to other engines.

This module provides standard Mule XML API and [Java API](mule-bpm-api), which is also used internally by all XML based operations.

## Prerequisites

Before you begin, ensure you have met the following requirements:
* You have installed the latest version of [OpenJDK 8](https://adoptopenjdk.net)
* You have installed the latest version of [Maven 3](http://maven.apache.org)
* You have installed IDE for Mule 4 development
  * [Mulesoft Anypoint Studion version 7](https://www.mulesoft.com/lp/dl/studio)
  * [Intelli/J IDEA](https://www.jetbrains.com/idea/) with [Mule 4 Runtime plugin](https://plugins.jetbrains.com/plugin/10822-mule-4-runtime-)

## Using Mule BPM Module

To use Mule BPM Module, follow these steps:

Add Maven Central's snapshot repository to your application pom.xml.
```
<repository>
	<id>snapshots-repo</id>
	<url>https://oss.sonatype.org/content/repositories/snapshots</url>
	<releases>
		<enabled>false</enabled>
	</releases>
	<snapshots>
		<enabled>true</enabled>
	</snapshots>
</repository>
```


Add this dependency to your application pom.xml:

```
<dependency>
	<groupId>com.alfame.esb.bpm</groupId>
	<artifactId>mule-bpm-module</artifactId>
	<version>2.1.2-SNAPSHOT</version>
	<classifier>mule-plugin</classifier>
</dependency>
```

To model BPMN 2.0 processes use [Flowable Modeler](http://localhost:8080/flowable-modeler) after running:
```
docker run -p8080:8080 flowable/all-in-one
```

> Hint from Flowable: default credentials are admin/test.

More details about usage and implementation of Mule BPM Module can be found from [mule-bpm-module/README.md](mule-bpm-module/README.md).

## Building Mule BPM Module yourself

To install Mule BPM Module, follow these steps:
```
git clone git@github.com:alfameCom/mule-bpm-module.git
cd mule-bpm-module
mvn clean install
```

## Contributing to Mule BPM Module
To contribute to Mule BPM Module, follow these steps:

1. Fork this repository.
2. Create a branch: `git checkout -b <branch_name>`
3. Make your changes and commit them: `git commit -m '<commit_message>'`
4. Push to the original branch: `git push origin <project_name>/<location>`
5. Create the pull request.

Alternatively see the GitHub documentation on [creating a pull request](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request).

## License

This project uses the following license: [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

## Contact us

If you’re experiencing problems or need help, please create a Github Issue.

Regarding consulting or commercial requests, email us at [mule-bpm-module@alfame.com](mule-bpm-module@alfame.com).
