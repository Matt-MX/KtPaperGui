package com.mattmx.ktgui

import com.mattmx.ktgui.commands.KtGuiCommand
import com.mattmx.ktgui.examples.CustomGUI
import com.mattmx.ktgui.utils.GitUpdateChecker
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class KotlinBukkitGui : JavaPlugin() {
    override fun onEnable() {
        version = description.version
        log = this.logger
        Bukkit.getScheduler().runTaskAsynchronously(this) { ->
            GitUpdateChecker("https://api.github.com/repos/Matt-MX/KtBukkitGui/releases/latest", version,
                { outdated, latest ->
                    if (outdated) {
                        log.info("Running an outdated version (v$version) Latest available (v$latest)")
                        log.info("Download here: https://github.com/Matt-MX/KtBukkitGui/releases/latest")
                    } else log.info("Running latest version! (v$version)")
                }, { _ -> log.info("Unable to check for latest version.") })
        }
        GuiManager.init(this)
        GuiManager.register("example_normal", CustomGUI())
        Bukkit.getPluginCommand("ktgui")?.setExecutor(KtGuiCommand())
    }

    companion object {
        val papi: Boolean = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
        lateinit var version: String
        lateinit var log: Logger
    }
}