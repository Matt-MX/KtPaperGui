package com.mattmx.ktgui.examples

import com.mattmx.ktgui.KotlinBukkitGui
import com.mattmx.ktgui.extensions.removeScoreboard
import com.mattmx.ktgui.scoreboards.animatedScoreboard
import com.mattmx.ktgui.utils.Chat
import org.bukkit.entity.Player
import java.lang.Integer.min

object AnimatedScoreboardExample {

    /**
     * Animated scoreboards can be made easily, and cancelled whenever you see fit.
     * They update with a period you provide asynchronously. Updates are sent to players
     * automatically.
     */
    private val text = "Hello world"
    private val builder = animatedScoreboard(Chat.color("&7Animated Scoreboard"), 10) {
        /**
         * Default lines that we start off with, before animation occurs.
         */
        add(Chat.color("&a$text"))
        add("")
        add(Chat.color("&aThis line is not animated"))
    }.update {
        /**
         * We can use getIterations() to know how long the animation has been running for.
         * This way we can animate our scoreboards easily (works as a deltaTime)
         */
        val chars = min((getIterations() % 20).toInt(), text.length)
        // Set the first line to a new string.
        set(0, Chat.color("&c" + text.substring(chars)))
        /**
         * Calling begin(plugin) will begin the loop of updating the scoreboard.
         * You may not want to do this until the scoreboard is actually used.
         */
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