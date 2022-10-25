package com.mattmx.ktgui.examples

import com.mattmx.ktgui.KotlinBukkitGui
import com.mattmx.ktgui.commands.KtGuiCommand
import com.mattmx.ktgui.extensions.removeScoreboard
import com.mattmx.ktgui.scoreboards.AnimatedScoreboardBuilder
import com.mattmx.ktgui.scoreboards.animatedScoreboard
import com.mattmx.ktgui.utils.Chat
import org.bukkit.entity.Player
import java.lang.Integer.min

object AnimatedScoreboardExample {

    private val text = "Hello world"
    private val builder = animatedScoreboard(Chat.color("&7Animated Scoreboard"), 10) {
        add(Chat.color("&a$text"))
        add("")
        add(Chat.color("&cThis line is not animated"))
    }.update {
        val chars = min((getTicksPassed() % 20).toInt(), text.length)
        set(0, Chat.color("&c" + text.substring(chars)))
    }.begin(KotlinBukkitGui.plugin!!)

    fun toggle(player: Player) {
        if (player.scoreboard == builder.scoreboard()) {
            player.removeScoreboard()
            player.sendMessage(Chat.color("&7No longer showing the scoreboard"))
        } else {
            player.scoreboard = builder.scoreboard()
            player.sendMessage(Chat.color("&7Now showing the scoreboard"))
        }
    }

}