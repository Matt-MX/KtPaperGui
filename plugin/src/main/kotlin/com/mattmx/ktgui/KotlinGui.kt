package com.mattmx.ktgui

import com.mattmx.ktgui.commands.declarative.arg.impl.*
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
import com.mattmx.ktgui.papi.placeholder
import com.mattmx.ktgui.papi.placeholderExpansion
import com.mattmx.ktgui.scheduling.sync
import com.mattmx.ktgui.sound.playSound
import com.mattmx.ktgui.sound.soundBuilder
import com.mattmx.ktgui.utils.component
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.pretty
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
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
            "config-gui" to { GuiConfigExample() },
            "refresh-scoreboard" to { signalScoreboardExample },
            "new-multi-screen-cram" to { NewCramMultiPageExample() },
            "new-multi-screen" to { NewMultiPageExample() },
            "hotbar" to { HotbarExample() }
        )
        GuiHookExample.registerListener(this)

        fun mapFont(alphabet: String) = alphabet
            .mapIndexed { index, it -> Char('a'.code + index) to it }
            .toMap(HashMap())

        fun convertFont(original: String, fontMap: Map<Char, Char>) =
            String(original.map { c -> fontMap[c] ?: c }.toCharArray())

        val smallFont = mapFont("ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ")
        val balls = mapFont("ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ")
        val blackSquares =
            mapFont("\uD83C\uDD70\uD83C\uDD71\uD83C\uDD72\uD83C\uDD73\uD83C\uDD74\uD83C\uDD75\uD83C\uDD76\uD83C\uDD77\uD83C\uDD78\uD83C\uDD79\uD83C\uDD7A\uD83C\uDD7B\uD83C\uDD7C\uD83C\uDD7D\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD80\uD83C\uDD81\uD83C\uDD82\uD83C\uDD83\uD83C\uDD84\uD83C\uDD85\uD83C\uDD86\uD83C\uDD87\uD83C\uDD88\uD83C\uDD89")

        val stringToConvert by greedyStringArgument()
        val fontType by multiChoiceArgument<(String) -> String>(
            "smalltext" to { convertFont(it, smallFont) },
            "balls" to { convertFont(it, balls) },
            "blacksquares" to { convertFont(it, blackSquares) }
        )

        val brandingType by multiChoiceArgument(
            config.getConfigurationSection("branding")
                ?.let { section ->
                    section.getKeys(false).associateWithTo(HashMap()) { k -> section.getString(k) ?: "null" }
                }
                ?: hashMapOf()
        )

        placeholderExpansion {
            placeholder("font" / fontType / stringToConvert) {
                fontType()(stringToConvert())
            }

            placeholder("font-ph" / fontType / stringToConvert) {
                val formatted = "%${stringToConvert().replace(" ", "_")}%"
                val string = PlaceholderAPI.setPlaceholders(requestedBy, formatted)
                fontType()(string)
            }

            placeholder("branding" / brandingType) { brandingType() }
            val a by doubleArgument()
            val op by multiChoiceArgument<(Double, Double) -> Double>(
                "+" to { a, b -> a + b },
                "-" to { a, b -> a - b },
                "/" to { a, b -> a / b },
                "*" to { a, b -> a * b },
            )
            val b by doubleArgument()

            placeholder("math" / a / op / b) {
                op()(a(), b())
            }

        } id "ktgui" author "MattMX"

        ("font" / fontType / stringToConvert).runs<CommandSender> {
            val text = fontType()(stringToConvert())
            reply(
                text.component
                    .clickEvent(ClickEvent.suggestCommand(text))
                    .hoverEvent(HoverEvent.showText(!"&aClick to copy"))
            )
        } permission "ktgui.command.font" register this

        sync {
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

                val invType by enumArgument<InventoryType>()

                invType getStringValueOf { name.lowercase() }
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
                                target.location.clone().toVector()
                            } in world '${target.location.world.name}'."
                        )
                    }
                }

                val msg by greedyStringArgument()
                msg min 1
                msg invalid { reply(!"&cMust provide a valid msg (at least 1 char)") }
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