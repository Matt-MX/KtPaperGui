plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    alias(libs.plugins.paperweight) apply true
    alias(libs.plugins.runPaper)
    `maven-publish`
}

repositories {
    maven("https://repo.codemc.io/repository/maven-releases/")
}

val mcVersion = libs.versions.paperApi.get()

dependencies {
    paperweight.paperDevBundle(mcVersion)
    compileOnly(libs.placeholder.api)
    compileOnly(libs.packet.events.spigot)

    implementation(project(":ktgui-core"))
    implementation(project(":ktgui-packet-events"))
    implementation(project(":ktgui-paper:ktgui-paper-command"))
    implementation(project(":ktgui-core:ktgui-core-brigadier"))
    implementation(kotlin("reflect"))
}

kotlin {
    jvmToolchain(21)
}

tasks {
    runServer {
        minecraftVersion(mcVersion.split("-")[0])

        downloadPlugins {
            hangar("ViaVersion", "5.2.0")
            hangar("ViaBackwards", "5.2.0")
            github("retrooper", "packetevents", "v2.7.0", "packetevents-spigot-2.7.0.jar")
        }
    }

    test {
        useJUnitPlatform()
    }
}