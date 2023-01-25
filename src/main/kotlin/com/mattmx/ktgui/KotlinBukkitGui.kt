package com.mattmx.ktgui

import com.mattmx.ktgui.commands.simpleCommand
import com.mattmx.ktgui.dsl.event
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
        InfiniteGuiExample.instance = InfiniteGuiExample()

        event<PlayerJoinEvent> {
            player.sendMessage("&7Welcome back, &f${player.name}&7!".color())
        }

        simpleCommand {
            name = "ktgui"
            description = "The KtBukkitGui example command."
            permission = "ktgui.command"
            suggestSubCommands = true
            playerOnly = true

            unknownSubcommand {
                it.source.sendMessage("&cUnknown sub command.".color())
            }

            executes {
                it.source.sendMessage("&#7F52FFBy MattMX, running KtGui v${KotlinBukkitGui.version}!".color())
            }

            subCommands += simpleCommand {
                name = "debug"
                permission = "ktgui.command.debug"

                executes {
                    val builder = arrayListOf<String>()
                    builder.add("&#7F52FFDebug information: &#E24462Registered GUIs")
                    GuiManager.guis.forEach { (id, gui) ->
                        builder.add(" &#7F52FFId: &#E24462${id} &#7F52FFButtons: &#E24462${gui.size()}")
                    }
                    GuiManager.players.forEach { (uuid, gui) ->
                        builder.add(" &#7F52FFUser: &#E24462${Bukkit.getPlayer(uuid)?.name} Class: &#E24462${gui}")
                    }
                    it.source.sendMessage(*builder.map { l -> l.color() }.toTypedArray())
                }
            }

            subCommands += simpleCommand {
                name = "example"
                permission = "ktgui.command.example"
                suggestSubCommands = true
                playerOnly = true
                
                unknownSubcommand {
                    it.source.sendMessage("&cInvalid example gui name.".color())
                }

                subCommands += simpleCommand {
                    name = "normal"
                    executes { GuiManager.guis["example_normal"]?.createCopyAndOpen(it.source as Player) }
                }
                subCommands += simpleCommand {
                    name = "java"
                    executes { GuiManager.guis["example_java"]?.createCopyAndOpen(it.source as Player) }
                }
                subCommands += simpleCommand {
                    name = "java_conversation"
                    executes { JavaConversationExample.builder.build(it.source as Player).begin() }
                }
                subCommands += simpleCommand {
                    name = "config"
                    executes { GuiManager.guis["example_config"]?.createCopyAndOpen(it.source as Player) }
                }
                subCommands += simpleCommand {
                    name = "pages"
                    executes { GuiManager.guis["example_pages"]?.createCopyAndOpen(it.source as Player) }
                }
                subCommands += simpleCommand {
                    name = "conversation"
                    executes { GuiManager.guis["example_conversation"]?.open(it.source as Player) }
                }
                subCommands += simpleCommand {
                    name = "anvil"
                    executes { AnvilInputGuiExample.gui(it.source as Player).open(it.source) }
                }
                subCommands += simpleCommand {
                    name = "animated_scoreboard"
                    executes { AnimatedScoreboardExample.toggle(it.source as Player) }
                }
                subCommands += simpleCommand {
                    name = "scoreboard"
                    executes { ScoreboardExample.toggle(it.source as Player) }
                }
                subCommands += simpleCommand { 
                    name = "furnace"
                    executes { DynamicExample.furnaceInventoryExample(it.source as Player) }
                }
                subCommands += simpleCommand { 
                    name = "builder"
                    executes { DynamicExample.serverChangerExample(it.source as Player) }
                }
                subCommands += simpleCommand { 
                    name = "yaml"
                    executes { DynamicExample.poorYamlExample(it.source as Player) }
                }
                subCommands += simpleCommand { 
                    name = "dsl"
                    executes { GuiDslExample.open(it.source as Player) }
                }
                subCommands += simpleCommand {
                    name = "infinite"
                    executes { InfiniteGuiExample.instance.open(it.player()) }
                }
                subCommands += simpleCommand {
                    name = "pattern"
                    executes { GuiPatternExample.gui.open(it.player()) }
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