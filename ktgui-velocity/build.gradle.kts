plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    alias(libs.plugins.runVelocity)
//    alias(libs.plugins.runPaper)
    kotlin("kapt")

    `maven-publish`
}

version = rootProject.version

//runPaper.disablePluginJarDetection()
//runPaper.detectPluginJar = false

repositories {
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    compileOnly(libs.velocity.api)
    kapt(libs.velocity.api)
    compileOnly(libs.packet.events.api)

    implementation(project(":ktgui-core"))
    implementation(project(":ktgui-packet-events"))
    implementation(kotlin("reflect"))
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
            github("retrooper", "packetevents", "v2.7.0", "packetevents-velocity-2.7.0.jar")
        }
    }

//    runServer {
//        val mcVersion = libs.versions.paperApi.get().split("-")[0]
//        minecraftVersion(mcVersion)
//
//        runDirectory(file("runBackend"))
//
//        downloadPlugins {
//            hangar("ViaVersion", "5.2.0")
//            hangar("ViaBackwards", "5.2.0")
//        }
//    }

    test {
        useJUnitPlatform()
    }
}