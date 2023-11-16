package com.mattmx.ktgui.examples

import com.mattmx.ktgui.extensions.removeScoreboard
import com.mattmx.ktgui.scoreboards.scoreboard
import com.mattmx.ktgui.utils.not
import org.bukkit.entity.Player

object ScoreboardExample {
    // Create a new scoreboard builder with a title.
    private val builder = scoreboard(!"&6&lTitle") {
        // Add a line of text, then whitespace, then another line of text.
        + !"&7This is a scoreboard example!"
        + !" "
        + !"&7New lines!"
    }

    fun toggle(player: Player) {
        if (player.scoreboard == builder.scoreboard) {
            player.removeScoreboard()
            player.sendMessage(!"&7No longer showing the scoreboard")
        } else {
            player.scoreboard = builder.scoreboard
            player.sendMessage(!"&7Now showing the scoreboard")
        }
    }
}