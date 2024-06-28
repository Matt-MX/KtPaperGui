import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
    id("org.ajoberstar.grgit") version "4.1.0"
}

val paper_version: String by rootProject

repositories {
    mavenCentral()
    maven("https://maven.pvphub.me/releases")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    shadow(implementation(project(":api", "reobf"))!!)
    shadow(implementation("co.pvphub:ProtocolLibDsl:-SNAPSHOT")!!)
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")

    paperweight.paperDevBundle(paper_version)
}

tasks.test {
    useJUnitPlatform()
}

sourceSets["main"].resources.srcDir("src/resources/")

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

fun getCheckedOutGitCommitHash(): String = grgit.head().abbreviatedId

val commitHash = getCheckedOutGitCommitHash()

tasks {
    withType<ProcessResources> {
        val props = "version" to "${rootProject.version}-commit-$commitHash"
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    build {
        dependsOn(shadowJar)
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
}