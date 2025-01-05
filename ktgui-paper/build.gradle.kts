plugins {
    alias(libs.plugins.paperweight) apply true
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paperApi.get())
    compileOnly(libs.placeholder.api)
    compileOnly(libs.packet.events.api)

    implementation(project(":ktgui-core"))
    implementation(project(":ktgui-packet-events"))
    implementation(kotlin("reflect"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}