# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [23.5.2] - 2023-07-01
Release based on CLN [v23.05.2](https://github.com/ElementsProject/lightning/releases/tag/v23.05.2).

### Added
- added dependency to `io.grpc:grpc-api` to use classes like `StatusRuntimeException`, etc.

### Changed
- upgrade: update grpc from v1.54.1 to v1.56.0
- upgrade: update protobuf from v3.21.12 to v3.22.3

## [23.5.1] - 2023-06-24
Initial release based on CLN [v23.05.1](https://github.com/ElementsProject/lightning/releases/tag/v23.05.1).


[Unreleased]: https://github.com/theborakompanioni/cln-grpc-client/compare/23.5.2...HEAD
[23.5.2]: https://github.com/theborakompanioni/cln-grpc-client/compare/23.5.1...23.5.2
[23.5.1]: https://github.com/theborakompanioni/cln-grpc-client/releases/tag/23.5.1
