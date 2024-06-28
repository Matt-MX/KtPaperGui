plugins {
<<<<<<< HEAD
    alias(libs.plugins.updateDeps) apply true
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.grgit) apply false
=======
    kotlin("jvm") version "1.7.10"
    id("io.papermc.paperweight.userdev") version "1.7.1" apply false
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
}

val version = "2.4.0"

<<<<<<< HEAD
rootProject.version = version
=======
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
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
