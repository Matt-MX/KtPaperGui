plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    alias(libs.plugins.grgit) apply true
    `maven-publish`
<<<<<<< HEAD
=======
    id("org.ajoberstar.grgit") version "4.1.0"
}

val paper_version: String by rootProject

repositories {
    mavenCentral()
    maven("https://maven.pvphub.me/releases")
    maven("https://repo.dmulloy2.net/repository/public/")
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
}

dependencies {
    shadow(implementation(project(":api", "reobf"))!!)
    shadow(implementation("co.pvphub:ProtocolLibDsl:-SNAPSHOT")!!)
<<<<<<< HEAD
    compileOnly(libs.protocolLib)
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.paper.api)
    compileOnly(libs.placeholder.api)
=======
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")

    paperweight.paperDevBundle(paper_version)
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
}

tasks.test {
    useJUnitPlatform()
}

sourceSets["main"].resources.srcDir("src/resources/")

<<<<<<< HEAD
kotlin {
    jvmToolchain(JavaVersion.VERSION_17.ordinal)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
=======
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
}

fun getCheckedOutGitCommitHash(): String = grgit.head().abbreviatedId

val commitHash = getCheckedOutGitCommitHash()

tasks {
    processResources {
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
<<<<<<< HEAD
        exclude { it.name.startsWith("kotlin") }
=======

        minimize {
            exclude("kotlin/**")
        }

>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
        mergeServiceFiles()
    }
}