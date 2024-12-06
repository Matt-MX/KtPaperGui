plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    id(libs.plugins.runVelocity.get().pluginId)
    alias(libs.plugins.runPaper)
    kotlin("kapt") version "2.1.0"
    `maven-publish`
}

runPaper.disablePluginJarDetection()
runPaper.detectPluginJar = false

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

        runDirectory(file("runVelocity"))

        downloadPlugins {
            github("retrooper", "packetevents", "v2.6.0", "packetevents-velocity-2.6.0.jar")
        }
    }

    runServer {
        val mcVersion = libs.versions.paperApi.get().split("-")[0]
        minecraftVersion(mcVersion)

        runDirectory(file("runBackend"))

        downloadPlugins {
            hangar("ViaVersion", "5.0.1")
            hangar("ViaBackwards", "5.0.1")
        }
    }

    test {
        useJUnitPlatform()
    }
}