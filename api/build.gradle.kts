import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

dependencies {
    compileOnly(kotlin("reflect"))
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