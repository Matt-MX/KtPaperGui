package com.mattmx.ktgui.guiconfig

import com.mattmx.ktgui.utils.InstancePackageClassCache
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class GuiConfigManager {
    val cache = InstancePackageClassCache<FileConfiguration>()

    inline fun <reified T : JavaPlugin> setConfigFile(file: FileConfiguration) {
        cache.cacheInstance(T::class.java, file)
    }

    inline fun <reified T : Any> getConfigFile() =
        cache.getInstance(T::class.java)

    fun <T : Any> getConfigFile(clazz: Class<T>) =
        cache.getInstance(clazz)
}