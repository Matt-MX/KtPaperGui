package com.mattmx.ktgui.examples

import com.mattmx.ktgui.dsl.scoreboard
import com.mattmx.ktgui.extensions.color
import com.mattmx.ktgui.extensions.removeScoreboard
import org.bukkit.entity.Player

object ScoreboardExample {
    // Create a new scoreboard builder with a title.
    private val builder = scoreboard("&6&lTitle".color()) {
        // Add a line of text, then whitespace, then another line of text.
        add("&7This is a scoreboard example!".color())
        whitespace()
        add("&7New lines!".color())
    }

    fun toggle(player: Player) {
        if (player.scoreboard == builder.scoreboard()) {
            player.removeScoreboard()
            player.sendMessage("&7No longer showing the scoreboard".color())
        } else {
            player.scoreboard = builder.scoreboard()
            player.sendMessage("&7Now showing the scoreboard".color())
        }
    }
}