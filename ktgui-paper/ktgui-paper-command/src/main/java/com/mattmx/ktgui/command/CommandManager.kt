package com.mattmx.ktgui.command

import com.mojang.brigadier.tree.CommandNode
import io.papermc.paper.command.brigadier.PaperCommands
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

object CommandManager {
    private val commands = mutableSetOf<RegisteredPaperCommand>()
    private lateinit var paperCommands: PaperCommands

    fun inject() {
        paperCommands = PaperCommands.INSTANCE
    }

    fun register(command: RegisteredPaperCommand) {
        this.commands.add(command)
        command.enabledStatus.set(true)

        if (::paperCommands.isInitialized) {
            paperCommands.setCurrentContext(command.plugin)
            paperCommands.setValid()

            paperCommands.register(command.root)

            paperCommands.setCurrentContext(null)
            paperCommands.invalidate()

            Bukkit.getOnlinePlayers().forEach { player -> player.updateCommands() }
        }
    }

    fun findByPlugin(plugin: JavaPlugin) = commands.filter { it.plugin == plugin }

    fun unregister(command: RegisteredPaperCommand) {
        command.enabledStatus.set(false)

        if (::paperCommands.isInitialized) {
            (paperCommands.dispatcherInternal.root as CommandNode<*>).removeCommand(command.root.name)
            Bukkit.getOnlinePlayers().forEach { player -> player.updateCommands() }
        }
    }

    fun dispose(command: RegisteredPaperCommand) {
        unregister(command)
        commands.remove(command)
    }
}