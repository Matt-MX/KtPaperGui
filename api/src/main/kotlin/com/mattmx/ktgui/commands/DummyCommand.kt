package com.mattmx.ktgui.commands

import com.mattmx.ktgui.utils.not
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DummyCommand(
    val cmd: SimpleCommandBuilder
) : Command(cmd.name, cmd.description ?: "", "null", cmd.aliases.toList()) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        val current = if (args.isEmpty()) ""
        else args[if (args.size - 1 > 0) args.size - 1 else 0]
        cmd.getCommand(args.toMutableList())?.also {
            if (sender !is Player && it.playerOnly) {
                sender.sendMessage(!"&cPlayer only command.")
                return false
            }
            if (it.hasPermission(sender)) it.executeFor(sender, args.toList(), current, commandLabel)
            else {
                cmd.noPermissions?.invoke(CommandInvocation(sender, args.toList(), current, commandLabel))
                this.permissionMessage?.let { it1 -> sender.sendMessage(it1) }
            }
        } ?: run {
            cmd.unknown(sender, args.toList(), current, commandLabel)
        }
        return false
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val current = if (args.isEmpty()) ""
        else args[if (args.size - 1 > 0) args.size - 1 else 0]
        var argss = args.toMutableList()
        argss = if (argss.size - 1 < 0) mutableListOf("") else argss.subList(0, argss.size - 1)
        cmd.getCommand(argss)?.let {
            return it.getSuggetions(CommandInvocation(sender, args.toList(), current, alias)).toMutableList()
        }
        return mutableListOf()
    }
}