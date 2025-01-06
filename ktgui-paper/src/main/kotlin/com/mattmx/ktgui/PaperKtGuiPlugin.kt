package com.mattmx.ktgui

import com.mattmx.ktgui.command.*
import com.mattmx.ktgui.command.arg.*
import com.mattmx.ktgui.util.not
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PaperKtGuiPlugin : JavaPlugin() {
    private val commands = mutableListOf<RegisteredPaperCommand>()

    override fun onEnable() {
        CommandManager.inject()

        val register = {
            commands.clear()
            commands += command("simpleInvocationCommand") {
                runs<Player> {
                    reply("You ran the command!")
                }
            }.register(this)

            val number by int()
            commands += command("times-two" / number) {
                runs<Player> {
                    reply(!"${number()} x 2 = ${number() * 2}")
                }
            }.register(this)

            val user by player()
            val msg by greedyString()
            commands += command("msg" / user / msg) {
                runs<Player> {
                    val target = user.first()

                    reply("You -> ${target.name}: ${msg()}")
                    target.sendMessage(!"${sender.name} -> You: $${msg()}")
                }
            }.register(this)

            commands += command("tp") {
                val pos by pos()
                val player by player()

                sub(player) {

                    runs<Player> {
                        sender.teleportAsync(player.first().location)
                    }

                    val player1 by player()
                    sub(player1) {
                        runs<Player> {
                            player.first().teleportAsync(player1.first().location)
                        }
                    }

                    sub(pos) {
                        runs<Player> {
                            player.first().teleportAsync(pos().toLocation(sender.world))
                        }
                    }
                }

                sub(pos) {
                    runs<Player> {
                        sender.teleportAsync(pos().toLocation(sender.world))
                    }
                }
            }.register(this)

            val player by player()
            val test by string()
            commands += command("ping" / player / -test) {
                runs<Player> {
                    val message = test.orElse("Ping!")
                    player.first().sendMessage(!message)
                }
            }.register(this)
        }

        register()

        val state by prettyBoolean()
        command("dynamic-registration" / state) {
            runs<Player> {
                if (state()) {
                    reply(!"<green>Enabling commands!")
                    commands.forEach(RegisteredPaperCommand::register)
                } else {
                    reply(!"<red>Disabling commands!")
                    commands.forEach(RegisteredPaperCommand::unregister)
                }
            }
        }.register(this)

        command("re-register") {
            runs<Player> {
                reply(!"Disabling existing")
                commands.forEach(CommandManager::dispose)
                reply(!"Re-enabling")
                register()
            }
        }.register(this)
    }

}