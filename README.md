# Thumbhash for Kotlin

This is a Kotlin implementation of [Thumbhash](https://github.com/evanw/thumbhash), a compact representation of a placeholder for images. `thumb-hash` kotlin is a multiplatform library aimed to have accelerated decoding and encoding for various mobile and desktop platforms.

## Development

Prerequisite: Install IntelliJ IDEA or similar for Kotlin development.

1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Build the project using `./gradlew build`.
4. Run the tests using `./gradlew test`.
    - JVM compatibility test: `./gradlew jvmTest`
    - Run specific tests: `./gradlew jvmTest --tests "com.justin13888.thumbhash.ThumbHashTest"`
5. Lint with Ktlint:

   ```bash
   # Check
   ./gradlew ktlintCheck
   # Fix
   ./gradlew ktlintFormat
   ```

## Benchmarking

Benchmarks use [kotlinx-benchmark](https://github.com/Kotlin/kotlinx-benchmark). All the Gradle tasks for each target is standard.

For example, run for JVM using:

```bash
./gradlew jvmBenchmark
```

[//]: # (TODO: Document all the other targets for convenience)

## Use Case Validation

`thumbhash-kotlin` is implemented and provides extended APIs for various use cases with built-in accelerations.

In particular, two well-developed use cases exist:

1. Rendering in Jetpack Compose (Android).
2. Consistent implementation for platforms supported by Kotlin/Native (iOS, macOS, MinGW, Linux, etc.).

Additionally, its API and runtime behaviours are verified to be consistent with the original thumbhash implementations for Java (most similar to Kotlin) and Rust (useful for server use).

## License

This project is licensed under the [Mozilla Public License 2.0](https://www.mozilla.org/en-US/MPL/2.0/).
