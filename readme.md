[![Build](https://github.com/theborakompanioni/cln-grpc-client/actions/workflows/build.yml/badge.svg)](https://github.com/theborakompanioni/cln-grpc-client/actions/workflows/build.yml)
[![GitHub Release](https://img.shields.io/github/release/theborakompanioni/cln-grpc-client.svg?maxAge=3600)](https://github.com/theborakompanioni/cln-grpc-client/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.theborakompanioni/cln-grpc-client-core.svg?maxAge=3600)](https://central.sonatype.com/artifact/io.github.theborakompanioni/cln-grpc-client-core)
[![License](https://img.shields.io/github/license/theborakompanioni/cln-grpc-client.svg?maxAge=2592000)](https://github.com/theborakompanioni/cln-grpc-client/blob/master/LICENSE)


<p align="center">
    <img src="https://github.com/theborakompanioni/cln-grpc-client/blob/master/docs/assets/images/logo-dark.svg#gh-light-mode-only" alt="Logo" width="256" />
    <img src="https://github.com/theborakompanioni/cln-grpc-client/blob/master/docs/assets/images/logo-light.svg#gh-dark-mode-only" alt="Logo" width="256" />
</p>


cln-grpc-client
===

A gRPC client for [Core Lightning (CLN)](https://github.com/ElementsProject/lightning).

### `cln-grpc-client-starter`
A module containing [a Spring Boot Starter is also available](https://github.com/theborakompanioni/bitcoin-spring-boot-starter/blob/devel/cln-grpc-client/readme.md).


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
# update buildscript dependency locks
./gradlew dependencies --write-locks
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

### Troubleshooting

#### `ManagedChannelProvider$ProviderNotFoundException`
```
Caused by: io.grpc.ManagedChannelProvider$ProviderNotFoundException: No functional channel service provider found. Try adding a dependency on the grpc-okhttp, grpc-netty, or grpc-netty-shaded artifact
```

add a channel service provider implementation, e.g.
```groovy
implementation "io.grpc:grpc-netty-shaded:${grpcVersion}"
```

Hint: The above section should currently not apply, as `grpc-netty-shaded` is included as dependency.
However, this dependency might be removed in future releases.

#### Error loading shared library `libio_grpc_netty_shaded_netty_tcnative-*.so`

See: https://github.com/grpc/grpc-java/blob/master/SECURITY.md#netty

## Contributing
All contributions and ideas are always welcome. For any question, bug or feature request,
please create an [issue](https://github.com/theborakompanioni/cln-grpc-client/issues).
Before you start, please read the [contributing guidelines](contributing.md).


## Resources

- Bitcoin: https://bitcoin.org/en/getting-started
- Lightning Network: https://lightning.network
---
- cln (GitHub): https://github.com/ElementsProject/lightning ([Docker](https://hub.docker.com/r/polarlightning/clightning))
- cln protobuf: https://github.com/ElementsProject/lightning/tree/master/cln-grpc/proto
- Protocol Buffers: https://protobuf.dev/
- gRPC: https://grpc.io/
- grpc-java (GitHub): https://github.com/grpc/grpc-java
- grpc-java Security: https://github.com/grpc/grpc-java/blob/master/SECURITY.md

## License

The project is licensed under the Apache License. See [LICENSE](LICENSE) for details.
