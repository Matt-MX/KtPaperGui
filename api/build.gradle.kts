plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    `maven-publish`
}

<<<<<<< HEAD
dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.placeholder.api)
    implementation(libs.kotlin.reflect)
    compileOnly(libs.kotlin.stdlib)
=======
val paper_version: String by rootProject

repositories {
    mavenCentral()
}

dependencies {
//    compileOnly(kotlin("reflect"))
    shadow(implementation(kotlin("reflect"))!!)
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.10")

    paperweight.paperDevBundle(paper_version)
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
}

tasks.test {
    useJUnitPlatform()
}

version = rootProject.version

sourceSets["main"].resources.srcDir("src/resources/")

<<<<<<< HEAD
kotlin {
    jvmToolchain(JavaVersion.VERSION_17.ordinal)
=======
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
<<<<<<< HEAD
//        exclude {
////            it.path.startsWith("kotlin") && !it.path.contains("reactive")
//            it.name.startsWith("kotlin")
//        }
        archiveBaseName.set("ktgui")
        mergeServiceFiles()
=======
    }
    assemble {
        dependsOn(reobfJar)
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
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