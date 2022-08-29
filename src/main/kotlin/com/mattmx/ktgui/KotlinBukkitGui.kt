package com.mattmx.ktgui

import com.mattmx.ktgui.commands.KtGuiCommand
import com.mattmx.ktgui.examples.CustomGUI
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class KotlinBukkitGui : JavaPlugin() {
    override fun onEnable() {
        // register example guis
        version = description.version
        log = this.logger
        GuiManager.init(this)
        GuiManager.register("example_normal", CustomGUI())
        Bukkit.getPluginCommand("ktgui")?.setExecutor(KtGuiCommand())
    }

    companion object {
        val papi : Boolean = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
        lateinit var version : String
        lateinit var log : Logger
    }
}