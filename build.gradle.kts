plugins {
    alias(libs.plugins.runPaper)
}

runPaper.folia.registerTask()
rootProject.version = "2.4.2-alpha"

subprojects {
    allprojects {
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
}

tasks {
    runServer {
        dependsOn(":plugin:assemble")
        val mcVersion = libs.versions.paperApi.get().split("-")[0]
        minecraftVersion(mcVersion)

        pluginJars("./plugin/build/libs/ktgui-plugin-${rootProject.version}-dev-all.jar")

        downloadPlugins {
            hangar("ViaVersion", "5.0.1")
            hangar("ViaBackwards", "5.0.1")
            hangar("PlaceholderAPI", "2.11.6")
            url("https://download.luckperms.net/1552/bukkit/loader/LuckPerms-Bukkit-5.4.137.jar")
        }
    }
}