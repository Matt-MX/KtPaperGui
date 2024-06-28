plugins {
    alias(libs.plugins.updateDeps) apply true
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.grgit) apply false
}

val version = "2.4.0"

rootProject.version = version