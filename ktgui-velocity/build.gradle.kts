plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    alias(libs.plugins.runVelocity)
    kotlin("kapt") version "2.1.0"
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    compileOnly(libs.velocity.api)
    kapt(libs.velocity.api)
    compileOnly(libs.packet.events.api)

    implementation(project(":ktgui-core"))
    implementation(project(":ktgui-packet-events"))
}

kotlin {
    jvmToolchain(21)
}

tasks {
    runVelocity {
        // Configure the Velocity version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        velocityVersion(libs.versions.velocity.get())
    }

    test {
        useJUnitPlatform()
    }
}