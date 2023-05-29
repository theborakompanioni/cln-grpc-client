[![Build](https://github.com/theborakompanioni/cln-grpc-client/actions/workflows/build.yml/badge.svg)](https://github.com/theborakompanioni/cln-grpc-client/actions/workflows/build.yml)
[![GitHub Release](https://img.shields.io/github/release/theborakompanioni/cln-grpc-client.svg?maxAge=3600)](https://github.com/theborakompanioni/cln-grpc-client/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.theborakompanioni/bitcoin-jsonrpc-client-core.svg?maxAge=3600)](https://search.maven.org/#search|g%3A%22io.github.theborakompanioni%22)
[![License](https://img.shields.io/github/license/theborakompanioni/cln-grpc-client.svg?maxAge=2592000)](https://github.com/theborakompanioni/cln-grpc-client/blob/master/LICENSE)


<p align="center">
    <img src="https://github.com/theborakompanioni/cln-grpc-client/blob/master/docs/assets/images/logo-dark.svg#gh-light-mode-only" alt="Logo" width="256" />
    <img src="https://github.com/theborakompanioni/cln-grpc-client/blob/master/docs/assets/images/logo-light.svg#gh-dark-mode-only" alt="Logo" width="256" />
</p>


cln-grpc-client
===


## Table of Contents

- [Install](#install)
- [Development](#development)
- [Contributing](#contributing)
- [Resources](#resources)
- [License](#license)


## Install

[Download](https://search.maven.org/#search|g%3A%22io.github.theborakompanioni%22) from Maven Central.

### Gradle
```groovy
repositories {
    mavenCentral()
}
```

```groovy
implementation "io.github.theborakompanioni:cln-grpc-client-core:${clnGrpcClientVersion}"
```

### Maven
```xml
<dependency>
    <groupId>io.github.theborakompanioni</groupId>
    <artifactId>cln-grpc-client-core</artifactId>
    <version>${clnGrpcClient.version}</version>
</dependency>
```


## Development

### Requirements
- java >=17
- docker

### Build
```shell script
./gradlew build -x test
```
 
### Test
```shell script
./gradlew test integrationTest --rerun-tasks
```

### Dependency Verification
Gradle is used for checksum and signature verification of dependencies.

```shell script
# write metadata for dependency verification
./gradlew --write-verification-metadata pgp,sha256 --export-keys
```

See [Gradle Userguide: Verifying dependencies](https://docs.gradle.org/current/userguide/dependency_verification.html)
for more information.

### Checkstyle
[Checkstyle](https://github.com/checkstyle/checkstyle) with adapted [google_checks](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml)
is used for checking Java source code for adherence to a Code Standard.

```shell script
# check for code standard violations with checkstyle
./gradlew checkstyleMain --rerun-tasks
```

### SpotBugs
[SpotBugs](https://spotbugs.github.io/) is used for static code analysis.

```shell script
# invoke static code analysis with spotbugs
./gradlew spotbugsMain --rerun-tasks
```


## Contributing
All contributions and ideas are always welcome. For any question, bug or feature request, 
please create an [issue](https://github.com/theborakompanioni/cln-grpc-client/issues). 
Before you start, please read the [contributing guidelines](contributing.md).


## Resources

- Bitcoin: https://bitcoin.org/en/getting-started
- Lightning Network: https://lightning.network
---
- cln (GitHub): https://github.com/ElementsProject/lightning ([Docker](https://hub.docker.com/r/polarlightning/clightning))
- Protocol Buffers: https://protobuf.dev/
- gRPC: https://grpc.io/

## License

The project is licensed under the Apache License. See [LICENSE](LICENSE) for details.
