import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
    id("org.ajoberstar.grgit") version "4.1.0"
}

repositories {
    mavenCentral()
    maven("https://maven.pvphub.me/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    shadow(implementation(project(":api"))!!)
    shadow(implementation("co.pvphub:ProtocolLibDsl:-SNAPSHOT")!!)
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
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
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("ktgui-plugin")
    archiveClassifier.set("all")
    archiveVersion.set(rootProject.version.toString())
    exclude("kotlin.*")
    mergeServiceFiles()
}