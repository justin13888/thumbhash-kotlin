import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotestMultiplatform)
    alias(libs.plugins.kotlinxBenchmark)
    kotlin("plugin.allopen") version libs.versions.kotlin
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "com.justin13888.thumbhash"
version = "1.0.0"

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
//    mingwX64()
    linuxX64()
    linuxArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.benchmark.runtime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.property)
            }
        }
        val jvmTest by getting {
            dependencies {
//                implementation(libs.junit.jupiter)
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    // Enable verbose output
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    // Force more detailed output
    systemProperty("kotest.assertions.output.max.size", "1000")
    systemProperty("kotest.assertions.multi.line.diff.enabled", "true")
}

android {
    namespace = "com.justin13888.thumbhash"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

allOpen {
    // Required to ensure benchmark classes and methods are open
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("jvm")
        register("android")
        register("macosX64")
        register("macosArm64")
        register("iosX64")
        register("iosArm64")
        register("iosSimulatorArm64")
        // register("mingwX64")
        register("linuxX64")
        register("linuxArm64")
    }
    configurations {
        named("main") {
            warmups = 1
            iterations = 1
            iterationTime = 1000
            iterationTimeUnit = "ms"
            outputTimeUnit = "ms"
        }
    } // TODO: Finalize any missing changes
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "library", version.toString())

    pom {
        name = "Thumbhash Kotlin"
        description = "A Kotlin implementation for Thumbhash."
        inceptionYear = "2025"
        url = "https://github.com/justin13888/thumbhash-kotlin"
        licenses {
            license {
                name = "MPL-2.0"
                url = "https://www.mozilla.org/en-US/MPL/2.0/"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "justin13888"
                name = "Justin"
                url = "https://github.com/justin13888"
            }
        }
        scm {
            url = "https://github.com/justin13888/thumbhash-kotlin"
            connection = "https://github.com/justin13888/thumbhash-kotlin.git"
            developerConnection = "https://github.com/justin13888/thumbhash-kotlin.git"
        }
    }
}