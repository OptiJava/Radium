
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.3"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "io.github.optijava"
version = "1.0.0-alpha.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

application {
    mainClass.set("io.github.optijava.ApplicationKt")

    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

val processFrontend: TaskProvider<Task> = tasks.register("processFrontend") {
    doFirst() {
        println("Processing frontend files...")

        Runtime.getRuntime().exec("rm -rf ./src/main/resources/static")

        val pro = ProcessBuilder("bash", "-c", "cd ./Radium && npm run build").redirectErrorStream(true).start()
        while (pro.isAlive) {
            Thread.sleep(200)
        }

        Runtime.getRuntime().exec("mv ./Radium/dist ./src/main/resources")
        Runtime.getRuntime().exec("mv ./src/main/resources/dist ./src/main/resources/static")

        println("Process successfully!")
    }
}

tasks.named("compileKotlin").get().dependsOn(processFrontend)
