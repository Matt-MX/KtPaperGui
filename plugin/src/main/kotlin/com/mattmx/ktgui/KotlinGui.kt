package com.mattmx.ktgui

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandWrapper
import com.mattmx.ktgui.commands.declarative.arg.impl.*
import com.mattmx.ktgui.commands.declarative.arg.suggests
import com.mattmx.ktgui.commands.declarative.arg.suggestsTopLevel
import com.mattmx.ktgui.commands.declarative.arg.withArgs
import com.mattmx.ktgui.commands.declarative.div
import com.mattmx.ktgui.commands.declarative.invoke
import com.mattmx.ktgui.commands.declarative.runs
import com.mattmx.ktgui.commands.rawCommand
import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.cooldown.ActionCoolDown
import com.mattmx.ktgui.designer.DesignerManager
import com.mattmx.ktgui.designer.GuiDesigner
import com.mattmx.ktgui.examples.*
import com.mattmx.ktgui.extensions.getOpenGui
import com.mattmx.ktgui.papi.placeholder
import com.mattmx.ktgui.papi.placeholderExpansion
import com.mattmx.ktgui.scheduling.sync
import com.mattmx.ktgui.sound.playSound
import com.mattmx.ktgui.sound.soundBuilder
import com.mattmx.ktgui.utils.*
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Sound
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
        val signalScoreboardExample = SignalScoreboardExample(this)
        val examples by multiChoiceArgument(hashMapOf(
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
            "config-gui" to { GuiConfigExample() },
            "refresh-scoreboard" to { signalScoreboardExample },
            "new-multi-screen-cram" to { NewCramMultiPageExample() },
            "new-multi-screen" to { NewMultiPageExample() },
            "hotbar" to { HotbarExample() }
        ))
        GuiHookExample.registerListener(this)

        sync {
            "ktgui" {
                permission("ktgui.command")

                examples invalid { reply(!"&cProvide a valid example ID") }
                ("example" / examples).runs<Player> {
                    examples()().run(sender)
                } permission "ktgui.command.examples"

                ("version").runs<CommandSender> {
                    reply(!"${mainColor}You are on ${subColor}KtGUI v${pluginMeta.version}")
                }

                ("debug") {
                    runs<CommandSender> {
                        val guis = GuiManager.getPlayersInGui()
                        reply(!"${mainColor}${guis.size} players in a GUI currently.")
                        guis.forEach { reply(!"&f- ${subColor}${it.key.name} in ${it.value::class.java.simpleName}") }
                    }

                    val player by playerArgument()
                    ("player" / player).runs<CommandSender> {
                        val gui = player().getOpenGui()
                            ?: return@runs reply(!"&cThat player has no GUI open.")

                        reply(
                            "${subColor}${player().name} ${mainColor}is currently in ${gui::class.java.simpleName}"
                                .component
                                .apply {
                                    if (sender is Player) {
                                        append("&a[open]".component.click { gui.open(sender as Player) })
                                    }
                                }
                                .append("&c[close]".component.click { GuiManager.forceClose(player()) })
                        )
                    }
                }
            } register this@KotlinGui

            "ktgui-cmd-examples" {
                buildAutomaticPermissions("ktgui.examples.command")

                ("sound") {
                    runs<Player> {
                        val sound = soundBuilder {
                            play(Sound.ENTITY_ENDERMAN_DEATH) volume 0.4f
                            wait(7)
                            play(Sound.BLOCK_NOTE_BLOCK_BANJO) pitch 2f
                        } relative true

                        sender.playSound(sound)
                    }
                }

                val coords by relativeCoords()

                coords invalid { reply(!"&cInvalid coords provided") }

                ("tppos" / coords) {
                    runs<Player> {
                        reply(!"&aTeleporting to ${coords().toVector()}")
                        sender.teleport(coords())
                    }
                }

                val block by relativeCoords()
                block blockPos true
                ("getblock" / block).runs<Player> {
                    reply(
                        !"&aBlock at ${block().toVector()} is " + block().block
                            .type
                            .translationKey()
                            .translatable
                            .color(TextColor.color(75, 200, 100))
                    )
                }

                val invType by multiChoiceArgument(
                    InventoryType.values().map { it.name.lowercase() to it }
                )
                invType invalid { reply(!"&cThat is not a valid inventory type.") }

                ("inventory" / invType) {
                    runs<Player> {
                        GuiScreen(!"", type = invType()).open(sender)
                    }
                }

                val a by doubleArgument()
                val b by doubleArgument()
                val op by multiChoiceArgument<(Double, Double) -> Double>(
                    "+" to { a, b -> a + b },
                    "-" to { a, b -> a - b },
                    "*" to { a, b -> a * b },
                    "/" to { a, b -> a / b },
                )
                ("math" / a / op / b) {
                    runs<CommandSender> {
                        reply(!"&f${a()} ${op.context.stringValue()} ${b()} = ${op()(a(), b())}")
                    }

                    invalid { reply(!"&cProvide two double values to add.") }
                }

                val player by playerArgument()
                player invalid { reply(!"&cInvalid player '$provided'") }
                ("find" / player) {
                    runs<Player> {
                        val target = player()
                        reply(
                            !"&aFound ${target.name} @ ${
                                target.location.clone().toVector().toString().replace(",", ", ")
                            } in world '${target.location.world.name}'."
                        )
                    }
                }

                val msg by greedyStringArgument()
                msg min 1
                msg invalid { reply(!"&cMust provide a valid msg (at least 1 char)") }
                msg suggests { emptyList() }
                ("msg" / player / msg) {
                    runs<CommandSender> {
                        reply(!"&f[Me -> ${player().name}]: ${msg()}")
                        reply(!"&f[${sender.name} -> Me]: ${msg()}")
                    }
                }

                val history = listOf(
                    "MattMX" to "meow",
                    "MattMX" to ":3",
                    "GabbySimon" to "test hello world",
                    "GabbySimon" to "hello matt",
                    "MattMX" to "ktgui",
                )

                val username by stringArgument()
                username suggests { history.map { it.first }.toSet() }

                val maxResults by intArgument()
                maxResults min 1 max 100

                ("hist") {
                    +maxResults
                    +username

                    runs<Player> {
                        reply(username.context.stringValue() ?: "null")

                        val results = history
                            .subList(0, maxResults.context.orElse(history.size).coerceAtMost(history.size))
                            .filter { it.first == username.context.orElse(it.first) }

                        if (results.isEmpty())
                            return@runs reply(!"&cNo results match your query.")

                        results.forEach { r -> reply(!"&7${r.first} said '${r.second}'") }
                    }
                }

                val cooldownPeriod by longArgument()
                cooldownPeriod optional true
                cooldownPeriod suggests { listOf(cooldownPeriod.toString()) }
                ("cooldown" / cooldownPeriod) {

                    cooldown(Duration.ofSeconds(3))

                    runs<CommandSender> {
                        withArgs(cooldownPeriod) {
                            ActionCoolDown.unregister(coolDown.get())

                            val newDuration = Duration.ofMillis(cooldownPeriod())
                            cooldown(newDuration)

                            reply(!"&aSet new cooldown duration to ${newDuration.pretty()}.")
                        } or {
                            reply(!"&6Command ran successfully! &fProvide millis arg to set new cooldown period.")
                        }
                    }
                }

                "obj" {
                    val objects = hashMapOf<String, HashMap<String, String>>()

                    val newObjectId by stringArgument()
                    newObjectId range (3..16) matches "[a-z0-9_]{3,16}".toRegex()
                    newObjectId invalid { reply(!"Invalid object ID $provided") }

                    ("create" / newObjectId) {
                        runs<CommandSender> {
                            objects.putIfAbsent(newObjectId(), hashMapOf())
                            reply(!"&aCreated object ${newObjectId()}")
                        }
                    }

                    val existingObjectId by multiChoiceArgument { objects }
                    existingObjectId invalid newObjectId.invalidCallback.first()

                    val path by stringArgument()
                    path matches "([a-z0-9_]\\.?)+".toRegex()

                    val value by stringArgument()
                    ("set" / existingObjectId / path / value) {
                        runs<CommandSender> {
                            existingObjectId().putIfAbsent(path(), value())
                            reply(!"&aSet ${existingObjectId.context.stringValue()}:${path()} = '${value()}'")
                        }
                    }

                    val pathOptional = path.clone()
                    pathOptional.optional()
                    ("get" / existingObjectId / pathOptional) {
                        runs<CommandSender> {

                            if (pathOptional.context.isEmpty()) {
                                reply(!"&aValues in object ${existingObjectId.context.stringValue()}")
                                for ((k, e) in existingObjectId().entries) {
                                    reply(!"&f${k}&7 = &b'${e}'")
                                }
                            } else {
                                val value = existingObjectId()[pathOptional()]
                                if (value != null) {
                                    reply(!"&cThere is no defined value for ${existingObjectId.context.stringValue()}:${pathOptional()}")
                                } else {
                                    reply(!"&a${existingObjectId.context.stringValue()}:${pathOptional()} = '${value}'")
                                }
                            }
                        }
                    }

                    ("del" / existingObjectId) {
                        runs<CommandSender> {
                            objects.remove(existingObjectId.context.stringValue())
                            reply(!"&cDeleted object ${existingObjectId.context.stringValue()}.")
                        }
                    }
                }

                runs<CommandSender> {
                    reply(!getUsage(defaultUsageOptions))
                }
            } register this@KotlinGui

            DesignerManager(this@KotlinGui)
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