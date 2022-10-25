package com.mattmx.ktgui.scoreboards

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.lang.IndexOutOfBoundsException
import java.lang.Integer.min


open class ScoreboardBuilder(
    var title: String,
    var displaySlot: DisplaySlot = DisplaySlot.SIDEBAR
) {
    private val modifies = arrayListOf<String>()

    private val scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
    private val objective: Objective = scoreboard.registerNewObjective(if (title.length > MAX_LINES) title.substring(0, MAX_LINES) else title, "dummy")

    init {
        if (title.length > MAX_LINES) title = title.substring(0, MAX_LINES)
        objective.displayName = title
        objective.displaySlot = displaySlot
    }

    infix fun title(title: String): ScoreboardBuilder {
        if (title.length > MAX_LINES) this.title = title.substring(0, MAX_LINES)
        else this.title = title
        objective.displayName = this.title
        return this
    }

    fun clear() : ScoreboardBuilder {
        modifies.clear()
        scoreboard.resetScores(title)
        return this
    }

    fun set(index: Int, line: String): ScoreboardBuilder {
        if (index >= MAX_LINES) throw IndexOutOfBoundsException("Index too high. Maximum index is 15 (0-15).")
        if (index >= modifies.size) {
            repeat(index - modifies.size + 1) {
                whitespace()
            }
        }
        scoreboard.resetScores(modifies[index])
        modifies[index] = line
        objective.getScore(line).score = -index
        return this
    }

    fun add(line: String): ScoreboardBuilder {
        if (modifies.size > MAX_LINES) throw IndexOutOfBoundsException("You can't add more than 16 lines.")
        val modified = getLineCoded(line)
        modifies.add(modified)
        objective.getScore(modified).score = -modifies.size - 1
        return this
    }

    fun whitespace(): ScoreboardBuilder {
        add(" ")
        return this
    }

    private fun getLineCoded(line: String): String {
        var result = line
        while (modifies.contains(result)) result += ChatColor.RESET
        return result.substring(0, min(40, result.length))
    }

    fun scoreboard(): Scoreboard {
        return scoreboard
    }

    companion object {
        val MAX_LINES = 16
    }

}