plugins {
    alias(libs.plugins.paperweight) apply true
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    `maven-publish`
}

repositories {
    mavenCentral()
}

version = rootProject.version

dependencies {
    paperweight.paperDevBundle(libs.versions.paperApi.get())
//    compileOnly(libs.paper.api)
    compileOnly(libs.placeholder.api)

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