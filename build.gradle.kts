plugins {
    kotlin("jvm") version "1.7.10"
}

val version = "2.3.1"

rootProject.version = version

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
        compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
        compileOnly("me.clip:placeholderapi:2.11.1")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }
}