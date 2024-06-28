plugins {
    alias(libs.plugins.kotlinJvm) apply true
    alias(libs.plugins.shadow) apply true
    alias(libs.plugins.grgit) apply true
    `maven-publish`
}

dependencies {
    shadow(implementation(project(":api"))!!)
    shadow(implementation("co.pvphub:ProtocolLibDsl:-SNAPSHOT")!!)
    compileOnly(libs.protocolLib)
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.paper.api)
    compileOnly(libs.placeholder.api)
}

tasks.test {
    useJUnitPlatform()
}

sourceSets["main"].resources.srcDir("src/resources/")

kotlin {
    jvmToolchain(JavaVersion.VERSION_17.ordinal)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
        exclude { it.name.startsWith("kotlin") }
        mergeServiceFiles()
    }
}