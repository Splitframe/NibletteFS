plugins {
    kotlin("multiplatform") version "1.8.10"
    application
}

group = "de.splitframe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

val exposedVersion = "0.40.1"
val koinVersion = "3.3.2"

kotlin {
    jvm("backend") {
        compilations.all {
            kotlinOptions.jvmTarget = "16"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js("frontend", IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val backendMain by getting {
            dependencies {
//                testImplementation(kotlin("test"))
                implementation("org.mariadb.jdbc:mariadb-java-client:3.1.2")
                implementation("org.flywaydb:flyway-core:9.12.0")
                implementation("org.flywaydb:flyway-mysql:9.12.0")
                implementation("com.mysql:mysql-connector-j:8.0.32")
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.slf4j:slf4j-api:2.0.6")
                implementation("org.slf4j:slf4j-simple:2.0.6")
                implementation("ch.qos.logback:logback-core:1.4.5")
                implementation("org.pircbotx:pircbotx:2.3")
                implementation("io.ktor:ktor-server-core:2.2.3")
                implementation("io.ktor:ktor-server-netty:2.2.3")
                implementation("io.insert-koin:koin-ktor:3.3.1")
            }
        }
        val backendTest by getting
        val frontendMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.385")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.385")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.10.4-pre.385")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:6.3.0-pre.385")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-redux:4.1.2-pre.385")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux:7.2.6-pre.385")
            }
        }
        val frontendTest by getting
    }
}

application {
    mainClass.set("de.splitframe.application.ServerKt")
}

tasks.named<Copy>("backendProcessResources") {
    val frontendBrowserDistribution = tasks.named("frontendBrowserDistribution")
    from(frontendBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("backendJar"))
    classpath(tasks.named<Jar>("backendJar"))
}