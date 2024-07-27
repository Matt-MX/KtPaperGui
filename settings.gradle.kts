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
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0" // Auto added by idea
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
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

// TODO Remove?
//gradleEnterprise {
//    if (System.getenv("CI") != null) {
//        buildScan {
//            publishAlways()
//            termsOfServiceUrl = "https://gradle.com/terms-of-service"
//            termsOfServiceAgree = "yes"
//        }
//    }
//}

include("api")
include("plugin")
include("api:api-core")
findProject(":api:api-core")?.name = "api-core"
include("api:api-paper")
findProject(":api:api-paper")?.name = "api-paper"
include("api:api-config")
findProject(":api:api-config")?.name = "api-config"
