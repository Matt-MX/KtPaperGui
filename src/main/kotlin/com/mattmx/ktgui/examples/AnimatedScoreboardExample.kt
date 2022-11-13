package com.mattmx.ktgui.examples

import com.mattmx.ktgui.KotlinBukkitGui
import com.mattmx.ktgui.dsl.animatedScoreboard
import com.mattmx.ktgui.extensions.color
import com.mattmx.ktgui.extensions.removeScoreboard
import com.mattmx.ktgui.extensions.stripColor
import org.bukkit.entity.Player
import java.lang.Integer.min

object AnimatedScoreboardExample {

    /**
     * Animated scoreboards can be made easily, and cancelled whenever you see fit.
     * They update with a period you provide asynchronously. Updates are sent to players
     * automatically.
     */
    private val text = "Hello world"
    private val rgbText = "&#773383This line has RGB".color()
    private val builder = animatedScoreboard("&6&lAnimated".color(), 10) {
        /**
         * Default lines that we start off with, before animation occurs.
         */
        add("&a$text".color())
        addRGB(rgbText)
        addRGB("&#fb0000&lR&#fc4800&la&#fd8f00&li&#bab800&ln&#55d200&lb&#0fd91e&lo&#08bc77&lw&#009ed0&l!".color())
        add("&aThis line is not animated".color())
        whitespace()
        add("&aRAM usage: 0mb".color())
    }.update {
        /**
         * We can use getIterations() to know how long the animation has been running for.
         * This way we can animate our scoreboards easily (works as a deltaTime)
         */
        val chars = min((getIterations() % text.length).toInt(), text.length)
        // Set the first line to a new string.
        set(0, "&c" + text.substring(chars))
        // Remember with RGB strings, the rgb values are there too, taking up the length.
        // Strip the color before referring to their length etc.
        setRGB(1, rgbText.substring(0, rgbText.length - min((getIterations() % rgbText.stripColor().length).toInt(), rgbText.stripColor().length - 1)))
        val runtime = Runtime.getRuntime()
        val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        // Update the memory usage of the server
        set(5, "&aRam usage: ${usedMemInMB}mb/${maxHeapSizeInMB}".color())
        /**
         * Calling begin(plugin) will begin the loop of updating the scoreboard.
         * You may not want to do this until the scoreboard is actually used.
         */
    }.begin(KotlinBukkitGui.plugin!!)

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