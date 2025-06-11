plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    kotlin("kapt")

    `maven-publish`
}

repositories {
    mavenCentral()
}

version = rootProject.version

dependencies {
    compileOnly(libs.velocity.api)
    kapt(libs.velocity.api)

    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlin.reflect)
    implementation(project(":ktgui-core:ktgui-core-brigadier"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}