plugins {
    kotlin("jvm") version "1.7.10"
}

val version = "2.4.0"

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
        compileOnly("me.clip:placeholderapi:2.11.1")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    tasks.assemble {
        dependsOn("reobfJar")
    }

}