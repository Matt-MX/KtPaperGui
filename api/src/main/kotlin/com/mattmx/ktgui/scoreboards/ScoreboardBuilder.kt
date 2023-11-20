package com.mattmx.ktgui.scoreboards

import com.mattmx.ktgui.utils.legacy
import com.mattmx.ktgui.utils.legacyColor
import com.mattmx.ktgui.utils.not
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*

open class ScoreboardBuilder(
    title: Component,
    val name: String = UUID.randomUUID().toString().replace("-", "")
) {
    var title: Component = title
        set(value) {
            this.objective.displayName(title)
            field = value
        }

    // Holds the lines of text, we are able to remove them because of this
    private val scoreboardLines = arrayListOf<String>()

    private val scoreboard: Scoreboard = Bukkit.getScoreboardManager().newScoreboard
    private val objective: Objective = scoreboard.registerNewObjective(name, Criteria.DUMMY, title).apply {
        displaySlot = DisplaySlot.SIDEBAR
    }

    /**
     * Shorthand operator for [add] function.
     */
    operator fun Component.unaryPlus() = add(this)

    /**
     * Append a new line to the scoreboard.
     *
     * @param line the line of text.
     * @return the index of the inserted line.
     * @throws IndexOutOfBoundsException if there are more than 16 lines.
     */
    fun add(line: Component): Int {
        if (scoreboardLines.size > LegacyScoreboardBuilder.MAX_LINES) throw IndexOutOfBoundsException("You can't add more than 16 lines.")
        val name = "&r".repeat(scoreboardLines.size).legacyColor()
        val team = scoreboard.getTeam(name) ?: scoreboard.registerNewTeam(name)
        team.suffix(line)
        team.addEntry(name)
        objective.getScore(name).score = LegacyScoreboardBuilder.MAX_LINES - scoreboardLines.size
        scoreboardLines.add(name)
        return scoreboardLines.size - 1
    }

    /**
     * Set a specified line to a new value.
     * Updates all players which are watching the scoreboard.
     *
     * @param index of the line to change.
     * @param line new content of the line.
     * @throws IndexOutOfBoundsException if [index] >= 16
     */
    operator fun set(index: Int, line: Component) {
        if (index >= LegacyScoreboardBuilder.MAX_LINES) throw IndexOutOfBoundsException("Index too high. Maximum index is 15 (0-15).")
        if (index >= scoreboardLines.size) {
            repeat(index - scoreboardLines.size + 1) {
                // fill with whitespace
                +!" "
            }
        }
        val name = scoreboardLines[index]
        val team = scoreboard.getTeam(name) ?: scoreboard.registerNewTeam(name)
        team.suffix(line)
        if (!team.hasEntry(name)) team.addEntry(name)
    }

    /**
     * Get a specific line's content
     *
     * @param index of the line
     * @return line requested
     */
    operator fun get(index: Int): Component? =
        scoreboardLines.getOrNull(index)?.let { scoreboard.getTeam(it)?.suffix() }

    /**
     * Clears all scoreboard content
     */
    fun clear() {
        scoreboardLines.clear()
        scoreboard.resetScores(name)
    }

    fun scoreboard() = scoreboard
    fun isShownFor(player: Player) = player.scoreboard == scoreboard
    fun showFor(player: Player) {
        player.scoreboard = scoreboard
    }
}

fun scoreboard(title: Component, block: ScoreboardBuilder.() -> Unit) = ScoreboardBuilder(title).apply(block)