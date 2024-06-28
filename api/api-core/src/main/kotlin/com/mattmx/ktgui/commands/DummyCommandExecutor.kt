package com.mattmx.ktgui.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class DummyCommandExecutor(
    cmd: SimpleCommandBuilder
) : DummyCommand(cmd), CommandExecutor, TabCompleter {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        commandLabel: String,
        args: Array<out String>
    ): Boolean {
        return execute(sender, commandLabel, args)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return tabComplete(sender, alias, args)
    }

}