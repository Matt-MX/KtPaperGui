package com.mattmx.ktgui.examples

import com.mattmx.ktgui.extensions.removeScoreboard
import com.mattmx.ktgui.scoreboards.scoreboard
import com.mattmx.ktgui.utils.Chat
import org.bukkit.entity.Player

object ScoreboardExample {
    private val builder = scoreboard(
        Chat.color("&6&lTitle")
    ) {
        add(Chat.color("&7This is a scoreboard example!"))
        whitespace()
        add(Chat.color("&7New lines!"))
    }

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