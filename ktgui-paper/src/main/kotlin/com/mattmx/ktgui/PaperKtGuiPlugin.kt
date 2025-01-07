package com.mattmx.ktgui

import com.mattmx.ktgui.command.*
import com.mattmx.ktgui.command.arg.prettyBoolean
import com.mattmx.ktgui.example.createInventorySeeCommand
import com.mattmx.ktgui.impl.PacketScoreboard.Companion.scoreboard
import com.mattmx.ktgui.impl.PaperGuiManagerImpl
import com.mattmx.ktgui.util.not
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.TextColor
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