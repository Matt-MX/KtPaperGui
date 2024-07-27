plugins {
    alias(libs.plugins.updateDeps) apply true
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.paperweight) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.grgit) apply false
}

rootProject.version = findProperty("pluginVersion")!!