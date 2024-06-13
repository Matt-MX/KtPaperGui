import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    `maven-publish`
}

val paper_version: String by rootProject

repositories {
    mavenCentral()
}

dependencies {
//    compileOnly(kotlin("reflect"))
    shadow(implementation(kotlin("reflect"))!!)
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.10")

    paperweight.paperDevBundle(paper_version)
}

tasks.test {
    useJUnitPlatform()
}

version = rootProject.version

sourceSets["main"].resources.srcDir("src/resources/")

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
    }
    assemble {
        dependsOn(reobfJar)
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("ktgui")
    mergeServiceFiles()
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
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
        create<MavenPublication>("ktgui") {
            from(components["java"])
            groupId = "com.mattmx"
            artifactId = "ktgui"
            version = rootProject.version.toString()
        }
    }
}