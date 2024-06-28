plugins {
    alias(libs.plugins.kotlinJvm) apply true
}

dependencies {
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.paper.api) // TODO remove
    compileOnly(libs.placeholder.api)
    compileOnly(libs.kotlin.stdlib)
}

version = findProperty("apiVersion")!!

kotlin {
    jvmToolchain(JavaVersion.VERSION_17.ordinal)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withJavadocJar()
    withSourcesJar()
}