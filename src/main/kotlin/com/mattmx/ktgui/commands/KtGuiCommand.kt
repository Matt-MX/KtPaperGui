package com.mattmx.ktgui.commands

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.KotlinBukkitGui
import com.mattmx.ktgui.examples.*
import com.mattmx.ktgui.extensions.color
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.stream.Stream

val preprefix = "&8&l⤷ &#7f52ff"
val prefix = "&8&l⤷ &#7f52ffK&#984fd8t&#b14bb1G&#c94889u&#e24462i &8» &#7f52ff"

class KtGuiCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) {
            when (args[0].lowercase()) {
                "example" -> {
                    if (sender !is Player) {
                        sender.sendMessage("${preprefix}Player only command.".color())
                        return false
                    }
                    if (args.size > 1) {
                        when (args[1].lowercase()) {
                            "normal" -> GuiManager.guis["example_normal"]?.createCopyAndOpen(sender)
                            "java" -> GuiManager.guis["example_java"]?.createCopyAndOpen(sender)
                            "java_conversation" -> JavaConversationExample.builder.build(sender).begin()
                            "config" -> GuiManager.guis["example_config"]?.createCopyAndOpen(sender)
                            "pages" -> GuiManager.guis["example_pages"]?.createCopyAndOpen(sender)
                            "conversation" -> GuiManager.guis["example_conversation"]?.open(sender)
                            "anvil" -> GuiManager.guis["example_anvil"]?.open(sender)
                            "animated_scoreboard" -> AnimatedScoreboardExample.toggle(sender)
                            "scoreboard" -> ScoreboardExample.toggle(sender)
                            "furnace" -> DynamicExample.furnaceInventoryExample(sender)
                            "builder" -> DynamicExample.serverChangerExample(sender)
                            "yaml" -> DynamicExample.poorYamlExample(sender)
                            "dsl" -> GuiDslExample.open(sender)
                            else -> sender.sendMessage("${prefix}Invalid example gui name.".color())
                        }
                    } else {
                        sender.sendMessage("${prefix}Invalid example gui name.".color())
                    }
                }

                "debug" -> {
                    val builder = arrayListOf<String>()
                    builder.add("${prefix}Debug information: &#E24462Registered GUIs")
                    GuiManager.guis.forEach { (id, gui) ->
                        builder.add("${preprefix}Id: &#E24462${id} &#7F52FFButtons: &#E24462${gui.size()}")
                    }
                    GuiManager.players.forEach { (uuid, gui) ->
                        builder.add("${preprefix}User: &#E24462${Bukkit.getPlayer(uuid)?.name} Class: &#E24462${gui}")
                    }
                    builder.forEach { sender.sendMessage(it.color()) }
                }
            }
        } else {
            sender.sendMessage("${preprefix}By MattMX, running KtGui v${KotlinBukkitGui.version}!".color())
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
                return Stream.of(
                    "normal",
                    "builder",
                    "java",
                    "java_conversation",
                    "furnace",
                    "yaml",
                    "config",
                    "pages",
                    "conversation",
                    "anvil",
                    "scoreboard",
                    "animated_scoreboard",
                    "dsl"
                )
                    .filter { it.startsWith(current) }
                    .toList()
            }
        }
        return null
    }
}