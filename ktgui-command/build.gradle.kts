plugins {
    alias(libs.plugins.kotlinJvm) apply true
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.adventure)
    compileOnly(libs.adventure.minimessage)

    implementation(project(":ktgui-core"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}