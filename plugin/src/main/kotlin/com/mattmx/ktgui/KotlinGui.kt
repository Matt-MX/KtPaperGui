package com.mattmx.ktgui

import com.mattmx.ktgui.commands.simpleCommand
import com.mattmx.ktgui.examples.*
import com.mattmx.ktgui.utils.not
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class KotlinGui : JavaPlugin() {
    override fun onEnable() {
        plugin = this

        protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null

        version = pluginMeta.version
        log = this.logger
        GuiManager.init(this)

        val mainColor = "&#7F52FF"
        val subColor = "&#E24462"
        // todo impl command
    }

    companion object {
        var protocolLib: Boolean = false
        var plugin: JavaPlugin? = null
        lateinit var version: String
        lateinit var log: Logger
    }
}