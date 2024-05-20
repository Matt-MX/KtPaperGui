package com.mattmx.ktgui.guiconfig

import com.mattmx.ktgui.utils.InstancePackageClassCache
import com.mattmx.ktgui.components.button.GuiButton
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

/**
 * Helper class to cache instances of config files belonging to plugins.
 *
 * We can use this for [configGui] function to easily read [GuiButton] from config files.
 */
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