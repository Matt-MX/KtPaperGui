package com.mattmx.ktguis.commands

import com.mattmx.ktguis.GuiManager
import com.mattmx.ktguis.KotlinBukkitGui
import com.mattmx.ktguis.utils.Chat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.lang.StringBuilder
import java.util.stream.Stream

val preprefix = "&8&l⤷ &#7f52ff"
val prefix = "&8&l⤷ &#7f52ffK&#984fd8t&#b14bb1G&#c94889u&#e24462i &8» &#7f52ff"

class KtGuiCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) {
            when (args[0].lowercase()) {
                "example" -> {
                    if (sender !is Player) {
                        sender.sendMessage(Chat.format("${preprefix}Player only command."))
                        return false
                    }
                    if (args.size > 1) {
                        when (args[1].lowercase()) {
                            "normal" -> GuiManager.guis["example_normal"]?.createAndOpen(sender)
                            "builder" -> GuiManager.guis["example_builder"]?.createAndOpen(sender)
                            else -> sender.sendMessage(Chat.format("${prefix}Invalid example gui name."))
                        }
                    } else {
                        sender.sendMessage(Chat.format("${prefix}Invalid example gui name."))
                    }
                }
                "debug" -> {
                    val builder = arrayListOf<String>()
                    builder.add("${prefix}Debug information: &#E24462Registered GUIs")
                    GuiManager.guis.forEach { (id, gui) ->
                        builder.add("${preprefix}Id: &#E24462${id} &#7F52FFButtons: &#E24462${gui.size()}")
                    }
                    GuiManager.players.forEach { (uuid, gui) ->
                        builder.add("${preprefix}UUID: &#E24462${uuid} Class: &#E24462${gui.javaClass.name}")
                    }
                    builder.forEach { sender.sendMessage(Chat.format(it)) }
                }
            }
        } else {
            sender.sendMessage("${preprefix}By MattMX, running KtGui v${KotlinBukkitGui.version}!")
        }
        return false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {
        val current = args[args.size - 1]
        if (args.size == 1) {
            return Stream.of("example", "debug")
                .filter { it.startsWith(current) }
                .toList()
        } else if (args.size == 2) {
            if (args[0].lowercase() == "example") {
                return Stream.of("normal", "builder")
                    .filter { it.startsWith(current) }
                    .toList()
            }
        }
        return null
    }
}