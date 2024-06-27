plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    `maven-publish`
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.placeholder.api)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
}

tasks.test {
    useJUnitPlatform()
}

version = rootProject.version

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
//        exclude {
////            it.path.startsWith("kotlin") && !it.path.contains("reactive")
//            it.name.startsWith("kotlin")
//        }
        archiveBaseName.set("ktgui")
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
            artifactId = "ktgui"
            version = rootProject.version.toString()
        }
    }
}