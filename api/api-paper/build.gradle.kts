plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    `maven-publish`
}

dependencies {
    implementation(project(":api:api-core"))
    compileOnly(libs.paper.api)
}

version = findProperty("paperApiVersion")!!

sourceSets["main"].resources.srcDir("src/resources/")

kotlin {
    jvmToolchain(JavaVersion.VERSION_17.ordinal)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveBaseName.set("ktgui-paper")
        archiveVersion.set(version.toString())
        mergeServiceFiles()
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
            artifactId = "ktgui-paper"
            version = version.toString()
        }
    }
}