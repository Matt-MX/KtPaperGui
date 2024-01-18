package com.mattmx.ktgui.commands

import com.mattmx.ktgui.utils.not
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class DummyCommandExecutor(
    val cmd: SimpleCommandBuilder
) : CommandExecutor, TabCompleter {
//    private val cooldowns = hashMapOf<String, Date>()

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        commandLabel: String,
        args: Array<out String>
    ): Boolean {
        val current = if (args.isEmpty()) ""
        else args[if (args.size - 1 > 0) args.size - 1 else 0]

        cmd.getCommand(args.toMutableList())?.also {
            if (sender !is Player && it.playerOnly) {
                sender.sendMessage(!"&cPlayer only command.")
                return false
            }
            if (it.hasPermission(sender)) {
                // Check for cooldown restrictions
//                if (cooldowns.containsKey(sender.name) && cooldowns[sender.name]!!.after(Date())) {
//                    it.cooldownCallback(CommandInvocation(sender, args.toList(), current, commandLabel, cooldowns[sender.name]))
//                    return@also
//                }

                it.executeFor(sender, args.toList(), current, commandLabel)

//                if (it.cooldown != null) {
//                    val now = Date().time
//                    val cooldownExpire = now + it.cooldown!!.toMillis()
//
//                    cooldowns[sender.name] = Date(cooldownExpire)
//                }
            } else {
                cmd.noPermissions?.let { it1 -> it1(CommandInvocation(sender, args.toList(), current, commandLabel)) }
                sender.sendMessage(!"&cYou do not have permissions to execute this command.")
            }
        } ?: run {
            cmd.unknown(sender, args.toList(), current, commandLabel)
        }
        return false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        val current = if (args.isEmpty()) ""
        else args[if (args.size - 1 > 0) args.size - 1 else 0]
        var argss = args.toMutableList()
        argss = if (argss.size - 1 < 0) mutableListOf("") else argss.subList(0, argss.size - 1)
        cmd.getCommand(argss)?.let {
            return it.getSuggestions(CommandInvocation(sender, args.toList(), current, alias)).toMutableList()
        }
        return mutableListOf()
    }

}