import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    alias(libs.plugins.paperweight) apply false
    alias(libs.plugins.kotlinJvm) apply true
    `maven-publish`
}

repositories {
    mavenCentral()
}

group = "com.mattmx"

version = (findProperty("version")?.toString() ?: "-SNAPSHOT")
    .replace("\$commit", getCurrentCommitHash())

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

    allprojects {
        repositories {
            mavenLocal()
            mavenCentral()
            maven("https://maven.pvphub.me/releases")
            maven("https://repo.papermc.io/repository/maven-public/")
            maven("https://repo.dmulloy2.net/repository/public/")
            maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
            maven("https://jitpack.io")
        }
    }

    publishing {
        if (project.name == "api") return@publishing
        if (project.name == "plugin") return@publishing
        if (project.name == "ktgui") return@publishing

        repositories {
            maven {
                name = "pvphub-releases"
                url = uri("https://maven.pvphub.me/releases")
                credentials {
                    username = System.getenv("PVPHUB_MAVEN_USERNAME")
                    password = System.getenv("PVPHUB_MAVEN_SECRET")
                }
            }
        }

        publications {
            create<MavenPublication>(project.name) {
                from(components["java"])
                groupId = "com.mattmx"
                artifactId = project.name
                version = rootProject.version.toString()
            }
        }
    }
}

fun getCurrentCommitHash(): String {
    val process = ProcessBuilder("git", "rev-parse", "HEAD").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val commitHash = reader.readLine()
    reader.close()
    process.waitFor()
    if (process.exitValue() == 0) {
        return commitHash?.substring(0, 7) ?: ""
    } else {
        throw IllegalStateException("Failed to retrieve the commit hash.")
    }
}