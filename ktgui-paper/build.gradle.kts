plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    alias(libs.plugins.runPaper)
    `maven-publish`
}

version = rootProject.version

repositories {
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

val mcVersion = libs.versions.paperApi.get()

dependencies {
//    paperweight.paperDevBundle(mcVersion)
    compileOnly(libs.paper.api)
    compileOnly(libs.placeholder.api)
    compileOnly(libs.packet.events.spigot)

    implementation(project(":ktgui-core"))
    implementation(project(":ktgui-packet-events"))
    implementation(project(":ktgui-paper:ktgui-paper-command"))
    implementation(project(":ktgui-core:ktgui-core-brigadier"))
    implementation(kotlin("reflect"))

    implementation(libs.kotlinx.coroutines.reactive)
}

kotlin {
    jvmToolchain(21)
}

tasks {
    runServer {
        minecraftVersion(mcVersion.split("-")[0])

        downloadPlugins {
            hangar("ViaVersion", "5.5.1")
            hangar("ViaBackwards", "5.5.1")
            modrinth("packetevents", "2.10.0")
        }
    }

    test {
        useJUnitPlatform()
    }
}