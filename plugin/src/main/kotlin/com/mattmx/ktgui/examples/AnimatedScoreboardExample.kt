package com.mattmx.ktgui.examples

import com.mattmx.ktgui.extensions.removeScoreboard
import com.mattmx.ktgui.extensions.stripColor
import com.mattmx.ktgui.scoreboards.scoreboard
import com.mattmx.ktgui.utils.not
import org.bukkit.entity.Player
import java.lang.Integer.min

object AnimatedScoreboardExample {

    /**
     * Animated scoreboards can be made easily, and cancelled whenever you see fit.
     * They update with a period you provide asynchronously. Updates are sent to players
     * automatically.
     */
    private val text = "Hello world"
    private val rgbText = "&#773383This line has RGB"
    private var iterations = 0
    private val builder = scoreboard(!"&6&lAnimated") {
        /**
         * Default lines that we start off with, before animation occurs.
         */
        add(!"&a$text")
        add(!rgbText)
        add(!"&#fb0000&lR&#fc4800&la&#fd8f00&li&#bab800&ln&#55d200&lb&#0fd91e&lo&#08bc77&lw&#009ed0&l!")
        add(!"&aThis line is not animated")
        add(!" ")
        add(!"&aRAM usage: 0mb")
    }

    fun update() = builder.apply {
        iterations++
        val chars = min((iterations % text.length), text.length)
        // Set the first line to a new string.
        this[0] = !"&c${text.substring(chars)}"
        // Remember with RGB strings, the rgb values are there too, taking up the length.
        // Strip the color before referring to their length etc.
        this[1] = !rgbText.substring(0, rgbText.length - min((iterations % rgbText.stripColor().length), rgbText.stripColor().length - 1))
        val runtime = Runtime.getRuntime()
        val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        // Update the memory usage of the server
        this[5] = !"&aRam usage: ${usedMemInMB}mb/${maxHeapSizeInMB}"
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