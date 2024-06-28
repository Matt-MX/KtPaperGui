package com.mattmx.ktgui

import com.mattmx.ktgui.commands.declarative.arg.impl.*
import com.mattmx.ktgui.commands.declarative.arg.suggestsTopLevel
import com.mattmx.ktgui.commands.declarative.arg.withArgs
import com.mattmx.ktgui.commands.declarative.div
import com.mattmx.ktgui.commands.declarative.invoke
import com.mattmx.ktgui.commands.rawCommand
import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.cooldown.ActionCoolDown
import com.mattmx.ktgui.designer.GuiDesigner
import com.mattmx.ktgui.examples.*
import com.mattmx.ktgui.papi.placeholder
import com.mattmx.ktgui.papi.placeholderExpansion
import com.mattmx.ktgui.scheduling.sync
import com.mattmx.ktgui.sound.playSound
import com.mattmx.ktgui.sound.sound
import com.mattmx.ktgui.sound.soundBuilder
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.pretty
import org.bukkit.Bukkit
<<<<<<< HEAD
=======
import org.bukkit.Sound
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
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
        val examples = hashMapOf("animated-scoreboard" to { animatedScoreboard },
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
            "config-gui" to { GuiConfigExample() })
        GuiHookExample.registerListener(this)

        placeholderExpansion {

            val player by playerArgument()

            placeholder("ping" / player) { player().ping }
            placeholder("ping") { requestedBy?.ping }
            placeholder("iscool" / player) { if (player().name == author) "this player's sick" else "nah not rly" }

        } id "ktgui" author "MattMX"

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
                            val newCoolDown = args.first().toLongOrNull() ?: return@executes

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

<<<<<<< HEAD
                    runs {
                        val designer = cachedDesigners[id()] ?: return@runs reply(
                            !"&cInvalid id, create one using &7/&fdesigner ${
                                create.getUsage(
                                    defaultUsageOptions, false
                                )
                            }"
                        )
=======
                    runs<Player> {
                        val designer = cachedDesigners[id()]
                            ?: return@runs reply(
                                !"&cInvalid id, create one using &7/&fdesigner ${
                                    create.getUsage(
                                        defaultUsageOptions,
                                        false
                                    )
                                }"
                            )
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
                        designer.open(sender)
                    }
                }

                val newTitle by argument<String>("string")
                ("set-title" / id / newTitle) {

                    id suggests { cachedDesigners.keys.toList() }

<<<<<<< HEAD
                    runs {
                        val designer = cachedDesigners[id()] ?: return@runs reply(
                            !"&cInvalid id, create one using &7/&fdesigner ${
                                create.getUsage(
                                    defaultUsageOptions, false
                                )
                            }"
                        )
=======
                    runs<CommandSender> {
                        val designer = cachedDesigners[id()]
                            ?: return@runs reply(
                                !"&cInvalid id, create one using &7/&fdesigner ${
                                    create.getUsage(
                                        defaultUsageOptions,
                                        false
                                    )
                                }"
                            )
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
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

<<<<<<< HEAD
            val someArg by argument<String>("string", true)
            someArg {
                missing { reply(!"&cMissing argument 'someArg'!") }
            }
            ("foo" / listOf(("fizz" / someArg)<CommandSender> {
                withDefaultUsageSubCommand(defaultUsageOptions)
                runs {
                    reply(!"&c${someArg().replace("l", "w")} :3")
                }
            }, ("bar")<CommandSender> {
                runs {
                    reply(!"&1bar")
                }
            }))<CommandSender> {
                withDefaultUsageSubCommand(defaultUsageOptions)
                runs {
                    reply(!"&cfoo&f!")
=======
            "ktgui-cmd-examples" {
                buildAutomaticPermissions("ktgui.examples.command")

                ("sound") {
                    runs<Player> {
                        val sound = soundBuilder {
                            sound(Sound.ENTITY_ENDERMAN_DEATH)
                            wait(1)
                            sound(Sound.BLOCK_NOTE_BLOCK_BANJO) pitch 2f
                        } relative true

                        sender.playSound(sound)
                    }
                }

                val coords by relativeCoords()

                coords invalid { reply(!"&cInvalid coords provided") }

                ("tp" / coords) {
                    runs<Player> {
                        reply(!"&aTeleporting to ${coords().toVector()}")
                        sender.teleport(coords())
                    }
                }

                val invType by enumArgument<InventoryType>()

                invType stringMethod { name.lowercase() }
                invType invalid { reply(!"&cThat is not a valid inventory type.") }

                ("inventory" / invType) {
                    runs<Player> {
                        GuiScreen(!"", type = invType()).open(sender)
                    }
                }

                val a by doubleArgument()
                val b by doubleArgument()
                ("+" / a / b) {
                    runs<CommandSender> {
                        reply(!"&a${a()} + ${b()} = ${a() + b()}")
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

                val cooldownPeriod by longArgument()
                cooldownPeriod optional true
                // fixme: args optional don't work
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

                    val objectId by stringArgument()
                    objectId range (3..16) matches "[a-z0-9_]{3,16}".toRegex()
                    objectId invalid { reply(!"Invalid object ID $provided") }

                    ("create" / objectId) {
                        runs<CommandSender> {
                            objects.putIfAbsent(objectId(), hashMapOf())
                            reply(!"&aCreated object ${objectId()}")
                        }
                    }

                    val existingObjectId by simpleArgument<HashMap<String, String>>()
                    existingObjectId getValue { objects[this] }
                    existingObjectId suggests { objects.keys.toList() }
                    existingObjectId invalid objectId.invalidCallback.first()

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
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
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