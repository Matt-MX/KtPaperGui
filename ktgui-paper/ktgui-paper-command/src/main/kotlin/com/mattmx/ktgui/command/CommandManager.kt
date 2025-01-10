package com.mattmx.ktgui.command

import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.PaperCommands
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

object CommandManager : AbstractCommandManager<RegisteredPaperCommand>() {
    private lateinit var paperCommands: PaperCommands
    private lateinit var rootNode: CommandNode<*>

    fun inject() {
        setInstance(this)
        paperCommands = PaperCommands.INSTANCE
        rootNode = paperCommands.dispatcherInternal.root as CommandNode<*>
    }

    override fun register(command: RegisteredPaperCommand) {
        super.register(command)
        command.enabledStatus.set(true)

        if (::paperCommands.isInitialized) {
            synchronized(rootNode) {
                paperCommands.setCurrentContext(command.plugin)
                paperCommands.setValid()

                paperCommands.register(command.root as LiteralCommandNode<CommandSourceStack>)

                paperCommands.setCurrentContext(null)
                paperCommands.invalidate()


                Bukkit.getOnlinePlayers().forEach { player -> player.updateCommands() }
            }
        }
    }

    fun findByPlugin(plugin: JavaPlugin) = commands.filter { it.plugin == plugin }

    override fun unregister(command: RegisteredPaperCommand) {
        command.enabledStatus.set(false)

        if (::paperCommands.isInitialized) {
            synchronized(rootNode) {
                rootNode.removeCommand(command.root.name)
                Bukkit.getOnlinePlayers().forEach { player -> player.updateCommands() }
            }
        }
    }
}