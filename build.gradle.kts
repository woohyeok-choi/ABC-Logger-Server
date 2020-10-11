import com.google.protobuf.gradle.*

plugins {
    id("com.github.johnrengelman.shadow") version "5.2.0"
    java
    idea
    application
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    id("com.google.protobuf") version "0.8.13"
}

application {
    mainClassName = "kaist.iclab.abclogger.MainKt"
    group = "kaist.iclab.abclogger"
    version = "1.0.0"
}


sourceSets {
    main {
        java.srcDir("src/main/kotlin")
        proto.srcDir("grpc")
    }

    test {
        java.srcDir("src/test/kotlin")
    }
}

repositories {
    google()
    jcenter()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}


dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    /**
     * gRPC
     */
    implementation("io.grpc:grpc-protobuf:1.32.1")
    implementation("io.grpc:grpc-stub:1.32.1")
    implementation("io.grpc:grpc-kotlin-stub:0.1.5")
    runtimeOnly("io.grpc:grpc-netty-shaded:1.32.1")

    /**
     *
     * MongoDb Driver and Toolkit (KMongo)
     */
    implementation("org.litote.kmongo:kmongo:4.1.2")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.1.2")

    /**
     * Mail
     */
    implementation("com.sun.mail:javax.mail:1.6.2")

    /**
     * RxJava3
     */
    implementation("io.reactivex.rxjava3:rxjava:3.0.0-RC8")

    /**
     * Logging
     */
    implementation("log4j:log4j:1.2.17")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.13.3")

    /**
     * Legacy Data Insert
     */
    implementation("com.squareup.moshi:moshi:1.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")

    /**
     * Test
     */
    testImplementation("io.kotest:kotest-runner-junit5:4.2.5") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core:4.2.5") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property:4.2.5") // for kotest property test
}

java {
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks

compileKotlin.kotlinOptions {
    jvmTarget = "13"
}

val shadowJar: com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar by tasks

shadowJar.archiveBaseName.set("abc-logger-server")
shadowJar.destinationDirectory.set(file("jars"))


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.13.0"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.32.1"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:0.1.5"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }

}

tasks.register<Exec>("initMongoDb") {
    commandLine("./test/test-run.bat")
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

configurations.all {
    if (name.contains("kapt") || name.contains("proto", ignoreCase = true)) {
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
    }
}
