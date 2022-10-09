package com.mattmx.ktgui

import com.mattmx.ktgui.commands.KtGuiCommand
import com.mattmx.ktgui.examples.ConfigScreenExample
import com.mattmx.ktgui.examples.CustomGUI
import com.mattmx.ktgui.examples.JavaGuiExample
import com.mattmx.ktgui.examples.MultiPageExample
import com.mattmx.ktgui.utils.GitUpdateChecker
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class KotlinBukkitGui : JavaPlugin() {
    override fun onEnable() {
        plugin = this
        version = description.version
        log = this.logger
        GuiManager.init(this)
        GuiManager.register("example_normal", CustomGUI())
        GuiManager.register("example_java", JavaGuiExample())
        GuiManager.register("example_config", ConfigScreenExample())
        GuiManager.register("example_pages", MultiPageExample())
        Bukkit.getPluginCommand("ktgui")?.setExecutor(KtGuiCommand())
    }

    companion object {
        val papi: Boolean = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
        var plugin: JavaPlugin? = null
        lateinit var version: String
        lateinit var log: Logger
    }
}