rootProject.name = "ktgui"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("com.gradle.enterprise") version("3.15")
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

include("api")
include("plugin:reobf")

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}

include("ktgui-core")
include("ktgui-paper")
include("ktgui-packet-events")
include("ktgui-velocity")
include("ktgui-command")
include("ktgui-brigadier")
include("ktgui-paper:ktgui-paper-command")
findProject(":ktgui-paper:ktgui-paper-command")?.name = "ktgui-paper-command"
