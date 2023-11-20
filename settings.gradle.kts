rootProject.name = "ktgui"
include("api")
include("plugin")

plugins {
    id("com.gradle.enterprise") version("3.15")
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}