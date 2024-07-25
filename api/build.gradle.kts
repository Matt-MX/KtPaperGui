plugins {
    alias(libs.plugins.paperweight) apply true
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    `maven-publish`
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paperApi.get())
    compileOnly(libs.placeholder.api)

    compileOnly(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
}

version = rootProject.version

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

    assemble {
        dependsOn("reobfJar")
    }
}

kotlin {
    jvmToolchain(17)
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