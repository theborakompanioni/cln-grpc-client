# This justfile requires https://github.com/casey/just

# Load environment variables from `.env` file.
set dotenv-load
# Fail the script if the env file is not found.
set dotenv-required

project_dir := justfile_directory()

# print available targets
[group("project-agnostic")]
default:
    @just --list --justfile {{justfile()}}

# evaluate and print all just variables
[group("project-agnostic")]
evaluate:
    @just --evaluate

# print system information such as OS and architecture
[group("project-agnostic")]
system-info:
    @echo "architecture: {{arch()}}"
    @echo "os: {{os()}}"
    @echo "os family: {{os_family()}}"

# clean (remove) the build artifacts
[group("development")]
clean:
    @./gradlew clean

# compile the project
[group("development")]
build *args='':
    @./gradlew build -x test {{args}}

# list dependency tree of this project
[group("development")]
dependencies:
    @./gradlew dependencyTree

# run unit tests
[group("development")]
test *args='':
    @./gradlew test {{args}}

# run integration tests
[group("development")]
test-integration:
    @./gradlew integrationTest --rerun-tasks --no-parallel

# update metadata for dependency verification
[group("development")]
update-verification:
    @./gradlew dependencies --write-verification-metadata pgp,sha256 --export-keys --write-locks

# regenerate proto definitions
[group("development")]
rebuild-proto:
    @./gradlew cleanClnProtoDirs generateProto --rerun-tasks --no-parallel
