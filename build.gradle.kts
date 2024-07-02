plugins {
    kotlin("jvm") version "1.7.10"
    id("io.papermc.paperweight.userdev") version "1.7.1" apply false
}

val version = "2.4.1"

rootProject.version = version

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.papermc.paperweight.userdev")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    dependencies {
//        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
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