package com.mattmx.ktgui

import com.mattmx.ktgui.commands.declarative.argument
import com.mattmx.ktgui.commands.declarative.div
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import com.mattmx.ktgui.commands.declarative.invoke
import com.mattmx.ktgui.commands.rawCommand
import com.mattmx.ktgui.commands.simpleCommand
import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import com.mattmx.ktgui.commands.suggestions.SimpleCommandSuggestion
import com.mattmx.ktgui.creator.GuiDesigner
import com.mattmx.ktgui.examples.*
import com.mattmx.ktgui.scheduling.sync
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.pretty
import org.bukkit.Bukkit
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

            "designer"<CommandSender> {
                buildAutomaticPermissions("ktgui.command")

                val typeOrRowArg by argument<String>("type_or_row")
                val id by argument<String>("unique_id")

                typeOrRowArg suggests { InventoryType.values().map { it.name.lowercase() } }

                ("open" / typeOrRowArg / id)<Player> {
                    runs {
                        val type = runCatching {
                            InventoryType.valueOf(typeOrRowArg().uppercase())
                        }.getOrNull()
                        val rows = typeOrRowArg().toIntOrNull()

                        if (type == null && rows == null) {
                            reply(!"&cYou need to provide an InventoryType or an amount of rows.")
                            return@runs
                        }

                        val designer = cachedDesigners.getOrPut(id()) { GuiDesigner(id(), type = type, rows = rows ?: 1) }
                        designer.open(sender)
                    }
                }

                ("export" / id)<CommandSender> {
                    runs {
                        val designer = cachedDesigners.getOrPut(id()) { GuiDesigner(id()) }
                        val file = designer.save(this@KotlinGui)
                        reply(!"&aSaved to /plugins/KtGUI/designer/${file.name}")
                    }
                }
            } register this@KotlinGui
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
    }
}