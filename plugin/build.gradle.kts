import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://maven.pvphub.me/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    implementation(project(":api"))
    implementation("co.pvphub:ProtocolLibDsl:-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets["main"].resources.srcDir("src/resources/")

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks {
    withType<ProcessResources> {
        val props = "version" to rootProject.version
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

val compile = tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("ktgui-plugin")
    mergeServiceFiles()
}