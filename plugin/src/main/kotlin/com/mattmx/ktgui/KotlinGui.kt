package com.mattmx.ktgui

import com.mattmx.ktgui.commands.declarative.arg.suggestsTopLevel
import com.mattmx.ktgui.commands.declarative.argument
import com.mattmx.ktgui.commands.declarative.div
import com.mattmx.ktgui.commands.declarative.invoke
import com.mattmx.ktgui.commands.rawCommand
import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import com.mattmx.ktgui.designer.GuiDesigner
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.examples.*
import com.mattmx.ktgui.scheduling.sync
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.pretty
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.util.logging.Logger

class KotlinGui : JavaPlugin() {
    override fun onEnable() {
        plugin = this

        protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null

        version = pluginMeta.version
        log = this.logger
        GuiManager.init(this)
        saveDefaultConfig()

        GuiManager.guiConfigManager.setConfigFile<KotlinGui>(config)

        val mainColor = "&#7F52FF"
        val subColor = "&#E24462"

        val animatedScoreboard = AnimatedScoreboardExample()
        val scoreboardExample = ScoreboardExample()
        val examples = hashMapOf(
            "animated-scoreboard" to { animatedScoreboard },
            "scoreboard" to { scoreboardExample },
            "anvil-input" to { AnvilInputGuiExample() },
            "config" to { ConfigScreenExample() },
            "conversation" to { ConversationGuiExample() },
            "random" to { CustomGUI() },
            "pattern" to { GuiPatternExample() },
            "infinite" to { InfiniteGuiExample() },
            "pages" to { MultiPageExample() },
            "counter" to { TitleCounterExample() },
            "signals" to { SignalsExample() },
            "signals-list" to { SignalsListExample() },
            "hook" to { GuiHookExample() },
            "java-simple" to { JavaGuiExample() },
            "java-new" to { JavaUpdateExample() },
            "refresh" to { RefreshBlockExample() },
            "config-gui" to { GuiConfigExample() }
        )
        GuiHookExample.registerListener(this)

        sync {
            val cachedDesigners = hashMapOf<String, GuiDesigner>()
            rawCommand("ktgui") {
                permission = "ktgui.command"
                playerOnly = true
                suggestSubCommands = true

                executes {
                    source.sendMessage(!"${mainColor}You are running ${subColor}KtGUI v${pluginMeta.version}")
                }

                subCommands += rawCommand("example") {
                    permission = "ktgui.command.example"
                    playerOnly = true

                    executes {
                        val exampleId = args.getOrNull(1)
                            ?: return@executes source.sendMessage(!"${mainColor}Please provide a valid example id.")

                        val example = examples[exampleId]
                            ?: return@executes source.sendMessage(!"${mainColor}Please provide a valid example id.")

                        example().run(player())
                    }
                    suggests {
                        examples.keys.filter { ex -> ex.startsWith(it.lastArg) }
                    }
                }

                subCommands += rawCommand("cooldown-example") {
                    permission = "ktgui.command.cooldown-example"
                    playerOnly = true
                    cooldown(Duration.ofSeconds(2))

                    executes {
                        source.sendMessage(!"&aNot on cool-down!")

                        if (args.isNotEmpty()) {
                            val newCoolDown = args.first().toLongOrNull()
                                ?: return@executes

                            val dur = Duration.ofMillis(newCoolDown)
                            source.sendMessage(!"&aNew cool-down set: ${dur.pretty()}")
                            cooldown(dur)
                        }
                    }

                    onCooldown {
                        player.sendMessage(!"&cPlease wait before doing that again.")
                    }
                }
            }.register(false)

            "designer" {
                buildAutomaticPermissions("ktgui.command")
                withDefaultUsageSubCommand(defaultUsageOptions)

                val typeOrRowArgMessage = !"&cYou must provide an InventoryType or an amount of rows."
                val typeOrRowArg by argument<String>("type_or_row")
                val id by argument<String>("unique_id")

                typeOrRowArg suggestsTopLevel { InventoryType.values().map { it.name.lowercase() } }
                typeOrRowArg invalid { reply(typeOrRowArgMessage) }
                id missing { reply(!"&cMissing argument 'id'. Need an identifier for the designer UI.") }

                val create = ("create" / typeOrRowArg / id) {
                    runs<Player> {
                        val type = runCatching {
                            InventoryType.valueOf(typeOrRowArg().uppercase())
                        }.getOrNull()
                        val rows = typeOrRowArg().toIntOrNull()

                        if (type == null && rows == null) {
                            reply(typeOrRowArgMessage)
                            return@runs
                        }

                        if (cachedDesigners.containsKey(id())) {
                            return@runs reply("&cThere is already a designer by that name.")
                        }

                        val designer =
                            cachedDesigners.getOrPut(id()) { GuiDesigner(id(), type = type, rows = rows ?: 1) }
                        designer.open(sender)
                    }
                }

                ("open" / id) {

                    id suggests { cachedDesigners.keys.toList() }

                    runs<Player> {
                        val designer = cachedDesigners[id()]
                            ?: return@runs reply(!"&cInvalid id, create one using &7/&fdesigner ${create.getUsage(defaultUsageOptions, false)}")
                        designer.open(sender)
                    }
                }

                val newTitle by argument<String>("string")
                ("set-title" / id / newTitle) {

                    id suggests { cachedDesigners.keys.toList() }

                    runs<CommandSender> {
                        val designer = cachedDesigners[id()]
                            ?: return@runs reply(!"&cInvalid id, create one using &7/&fdesigner ${create.getUsage(defaultUsageOptions, false)}")
                        designer.exportTitle = newTitle()
                        reply(!"&aSet title of ${id()} to ${newTitle()}")
                    }
                }

                subcommand("export" / id) {

                    id suggests { cachedDesigners.keys.toList() }

                    runs<CommandSender> {
                        val designer = cachedDesigners.getOrPut(id()) { GuiDesigner(id()) }
                        val file = designer.save(this@KotlinGui)
                        reply(!"&aSaved to /plugins/KtGUI/designer/${file.name}")
                    }
                }
            } register this@KotlinGui

            val someArg by argument<String>("string", true)
            someArg {
                missing { reply(!"&cMissing argument 'someArg'!") }
            }
        }
    }

    override fun onDisable() {
        GuiManager.shutdown()
    }

    companion object {
        var protocolLib: Boolean = false
        var plugin: JavaPlugin? = null
        lateinit var version: String
        lateinit var log: Logger

        val defaultUsageOptions = CommandUsageOptions {
            namePrefix = "&7/&f"

            arguments {
                prefix = "&7<&f"
                typeChar = "&7:&6"

                required = "&c!"
                optional = "&7?"

                suffix = "&7>"
            }

            subCommands {
                prefix = "&f"
                divider = "&7|&f"
            }
        }
    }
}