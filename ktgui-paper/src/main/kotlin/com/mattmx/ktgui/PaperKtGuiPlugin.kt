package com.mattmx.ktgui

import com.mattmx.ktgui.command.*
import com.mattmx.ktgui.command.arg.*
import com.mattmx.ktgui.example.createInventorySeeCommand
import com.mattmx.ktgui.impl.PacketScoreboard.Companion.scoreboard
import com.mattmx.ktgui.impl.PaperGuiManagerImpl
import com.mattmx.ktgui.util.minimessage
import com.mattmx.ktgui.util.not
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minecraft.advancements.critereon.EntityFlagsPredicate.Builder.flags
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class PaperKtGuiPlugin : JavaPlugin() {
    val manager = PaperGuiManagerImpl(this)

    override fun onEnable() {
        instance = this

        CommandManager.inject()
        manager.registerListeners()

        val board = scoreboard(!"<green>Test") {
            updating(this@PaperKtGuiPlugin)

            lines {
                +!"<red>First Line"
                +!"<gray>Second Line"

                +Component.empty()

                +!"<#e352a6><bold>RGB color!"
                +{ !"Time: ${Date()}" } updateEvery 1.seconds

                this[1] = !"Overriden Line"
            }
        }

        command("scoreboard") {
            val state by prettyBoolean()
            sub(state) {
                runs<Player> {
                    if (state()) {
                        board.addViewer(sender.uniqueId)
                    } else {
                        board.removeViewer(sender.uniqueId)
                    }
                }
            }

            sub("update") {
                runs<Player> {
                    board.title = Component.text("Test").color(TextColor.color(Random.nextInt()))
                }
            }
        }.register(this)

        command("bc") {
            val msg by greedyString()

            sub(msg) {
                runs<CommandSender> {
                    Bukkit.broadcast(msg().minimessage)
                }
            }
        }.register(this)

        val username by word()
        command("history" / username) {
            runs<CommandSender> {
                reply(!"History (NO flags)")
            }

            val page by int()
            val ignoreExpired by boolean()
            val actorName by string()
            val flags by options(page, ignoreExpired, actorName)
            sub(flags) {
                runs<CommandSender> {
                    reply(!"History ${username()} page ${flags[page] ?: 1} actor ${flags[actorName] ?: "*"}")
                    if (flags[ignoreExpired] == true) {
                        reply(!"Ignoring expired punishments")
                    }
                }
            }
        }.register(this)

        command("gamemode") {
            val gameModes by gameMode()

            sub(gameModes) {
                runs<Player> {
                    sender.gameMode = gameModes()

                    reply(!"Your game mode was set to ${gameModes().name.lowercase()}.")
                }

                val player by player()
                sub(player) {
                    runs<CommandSender> {
                        val target = player.first()

                        target.gameMode = gameModes()
                        target.sendMessage(!"${sender.name} set your game mode to ${gameModes().name.lowercase()}.")
                        reply(!"Set ${target.name}'s game mode to ${gameModes().name.lowercase()}")
                    }
                }
            }
        }.register(this)

        createInventorySeeCommand()
    }

    override fun onDisable() {
        manager.unregisterListeners()
    }

    companion object {
        private lateinit var instance: PaperKtGuiPlugin
        fun getInstance() = instance
    }
}