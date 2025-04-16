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