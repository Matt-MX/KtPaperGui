plugins {
    alias(libs.plugins.kotlinJvm) apply true
    `maven-publish`
}

version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.mojang.brigadier)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}