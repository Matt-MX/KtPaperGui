import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true

    `maven-publish`
}

dependencies {
    compileOnly(libs.paper.api)
    shadow(implementation(project(":api"))!!)
    compileOnly(libs.placeholder.api)
}

sourceSets["main"].resources.srcDir("src/resources/")

tasks {
    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
        archiveBaseName.set("ktgui")
    }

    shadowJar {
        archiveBaseName.set("ktgui-plugin")
        archiveClassifier.set("")
        archiveVersion.set(rootProject.version.toString())

        minimize {
            exclude("kotlin/**")
        }

        mergeServiceFiles()
    }

    processResources {
        val props = "version" to "${rootProject.version}-commit-${getCurrentCommitHash()}"
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(21)
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