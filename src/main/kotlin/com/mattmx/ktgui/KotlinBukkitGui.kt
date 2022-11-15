package com.mattmx.ktgui

import com.mattmx.ktgui.commands.simpleCommand
import com.mattmx.ktgui.dsl.onEvent
import com.mattmx.ktgui.examples.*
import com.mattmx.ktgui.extensions.color
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class KotlinBukkitGui : JavaPlugin() {
    override fun onEnable() {
        plugin = this
        version = description.version
        log = this.logger
        GuiManager.init(this)
        GuiManager.register("example_normal", CustomGUI())
        GuiManager.register("example_java", JavaGuiExample())
        GuiManager.register("example_config", ConfigScreenExample())
        GuiManager.register("example_pages", MultiPageExample())
        GuiManager.register("example_conversation", ConversationGuiExample())

        simpleCommand {
            name = "ktgui"
            description = "The KtBukkitGui example command."
            permission = "ktgui.command"
            suggestSubCommands = true

            unknownSubcommand { source, args, alias ->
                source.sendMessage("&cUnknown sub command.".color())
            }

            executes { source, args, alias ->
                source.sendMessage("&#7F52FFBy MattMX, running KtGui v${KotlinBukkitGui.version}!".color())
            }

            subCommands += simpleCommand {
                name = "debug"
                permission = "ktgui.command.debug"

                executes { source, args, alias ->
                    val builder = arrayListOf<String>()
                    builder.add("&#7F52FFDebug information: &#E24462Registered GUIs")
                    GuiManager.guis.forEach { (id, gui) ->
                        builder.add(" &#7F52FFId: &#E24462${id} &#7F52FFButtons: &#E24462${gui.size()}")
                    }
                    GuiManager.players.forEach { (uuid, gui) ->
                        builder.add(" &#7F52FFUser: &#E24462${Bukkit.getPlayer(uuid)?.name} Class: &#E24462${gui}")
                    }
                    builder.forEach { source.sendMessage(it.color()) }
                }
            }

            subCommands += simpleCommand {
                name = "example"
                permission = "ktgui.command.example"
                suggestSubCommands = true
                playerOnly = true
                
                unknownSubcommand { source, _, _ ->
                    source.sendMessage("&cInvalid example gui name.".color())
                }

                subCommands += simpleCommand {
                    name = "normal"
                    executes { source, _, _ -> GuiManager.guis["example_normal"]?.createCopyAndOpen(source as Player) }
                }
                subCommands += simpleCommand {
                    name = "java"
                    executes { source, _, _ -> GuiManager.guis["example_java"]?.createCopyAndOpen(source as Player) }
                }
                subCommands += simpleCommand {
                    name = "java_conversation"
                    executes { source, _, _ -> JavaConversationExample.builder.build(source as Player).begin() }
                }
                subCommands += simpleCommand {
                    name = "config"
                    executes { source, _, _ -> GuiManager.guis["example_config"]?.createCopyAndOpen(source as Player) }
                }
                subCommands += simpleCommand {
                    name = "pages"
                    executes { source, _, _ -> GuiManager.guis["example_pages"]?.createCopyAndOpen(source as Player) }
                }
                subCommands += simpleCommand {
                    name = "conversation"
                    executes { source, _, _ -> GuiManager.guis["example_conversation"]?.open(source as Player) }
                }
                subCommands += simpleCommand {
                    name = "anvil"
                    executes { source, _, _ -> AnvilInputGuiExample.gui(source as Player).open(source as Player) }
                }
                subCommands += simpleCommand {
                    name = "animated_scoreboard"
                    executes { source, _, _ -> AnimatedScoreboardExample.toggle(source as Player) }
                }
                subCommands += simpleCommand {
                    name = "scoreboard"
                    executes { source, _, _ -> ScoreboardExample.toggle(source as Player) }
                }
                subCommands += simpleCommand { 
                    name = "furnace"
                    executes { source, _, _ -> DynamicExample.furnaceInventoryExample(source as Player) }
                }
                subCommands += simpleCommand { 
                    name = "builder"
                    executes { source, _, _ -> DynamicExample.serverChangerExample(source as Player) }
                }
                subCommands += simpleCommand { 
                    name = "yaml"
                    executes { source, _, _ -> DynamicExample.poorYamlExample(source as Player) }
                }
                subCommands += simpleCommand { 
                    name = "dsl"
                    executes { source, _, _ -> GuiDslExample.open(source as Player) }
                }
            }
        }.register()
    }

    companion object {
        var protocollib: Boolean = false
        var papi: Boolean = false
        var plugin: JavaPlugin? = null
        lateinit var version: String
        lateinit var log: Logger
    }
}