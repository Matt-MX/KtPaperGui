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

        InfiniteGuiExample.instance = InfiniteGuiExample()

        val mainColor = "&#7F52FF"
        val subColor = "&#E24462"
        simpleCommand {
            name = "ktgui"
            description = "The KtBukkitGui example command."
            permission = "ktgui.command"
            suggestSubCommands = true
            playerOnly = true

            unknownSubcommand {
                it.source.sendMessage(!"&cUnknown sub command.")
            }

            executes {
                it.source.sendMessage(!"&#7F52FFBy MattMX :3, running KtGui v$version!")
            }

            subCommands += simpleCommand {
                name = "debug"
                permission = "ktgui.command.debug"

                executes {
                    /**
                     * TODO
                     * the debug command should allow a player to inspect and follow a player through a gui tree.
                     * it should update when the player changes gui, and allow the user to watch the gui too.
                     */
                }
            }
        }.register(false)
    }

    companion object {
        var protocolLib: Boolean = false
        var plugin: JavaPlugin? = null
        lateinit var version: String
        lateinit var log: Logger
    }
}