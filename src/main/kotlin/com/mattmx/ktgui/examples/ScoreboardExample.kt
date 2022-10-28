package com.mattmx.ktgui.examples

import com.mattmx.ktgui.dsl.animatedScoreboard
import com.mattmx.ktgui.dsl.scoreboard
import com.mattmx.ktgui.extensions.removeScoreboard
import com.mattmx.ktgui.utils.Chat
import org.bukkit.Statistic
import org.bukkit.entity.Player

object ScoreboardExample {
    // Create a new scoreboard builder with a title.
    private val builder = scoreboard(
        Chat.color("&6&lTitle")
    ) {
        // Add a line of text, then whitespace, then another line of text.
        add(Chat.color("&7This is a scoreboard example!"))
        whitespace()
        add(Chat.color("&7New lines!"))
    }

    fun toggle(player: Player) {
        if (player.scoreboard == builder.scoreboard()) {
            player.removeScoreboard()
            player.sendMessage(Chat.color("&7No longer showing the scoreboard"))
        } else {
//            player.scoreboard = builder.scoreboard()
//            player.sendMessage(Chat.color("&7Now showing the scoreboard"))
            val board = animatedScoreboard("&5pvphub".format()) {
                title = "<main>pvphub".format()
                add(" &8&m                            &r ".format())
                whitespace()
                addRGB("  <light>Kills: ${player.getStatistic(Statistic.PLAYER_KILLS)}".format())
                addRGB("  <light>Deaths: ${player.getStatistic(Statistic.DEATHS)}".format())
                whitespace()
                add(" &8&m                            &r ".format())
                addRGB(" <light>IP: <main>pvphub.me".format())
                add(" &8&m                            &r ".format())
            }.update {
                setRGB(2, "  <light>Kills: ${player.getStatistic(Statistic.PLAYER_KILLS)}".format())
                setRGB(3, "  <light>Deaths: ${player.getStatistic(Statistic.DEATHS)}".format())
            }
            player.scoreboard = board.scoreboard()
        }
    }
}