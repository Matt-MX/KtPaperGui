plugins {
    alias(libs.plugins.paperweight) apply false
}

rootProject.version = "2.4.4-alpha"

subprojects {
    allprojects {
        repositories {
            mavenLocal()
            mavenCentral()
            maven("https://maven.pvphub.me/releases")
            maven("https://repo.papermc.io/repository/maven-public/")
            maven("https://repo.dmulloy2.net/repository/public/")
            maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
            maven("https://jitpack.io")
        }
    }
}