rootProject.name = "ktgui"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    //id("com.gradle.develocity") version("3.17.5")
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.pvphub.me/releases")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://jitpack.io")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

include("api")
include("plugin")

//gradleEnterprise {
//    if (System.getenv("CI") != null) {
//        buildScan {
//            publishAlways()
//            termsOfServiceUrl = "https://gradle.com/terms-of-service"
//            termsOfServiceAgree = "yes"
//        }
//    }
//}