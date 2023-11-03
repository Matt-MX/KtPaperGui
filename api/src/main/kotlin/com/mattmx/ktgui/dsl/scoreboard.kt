package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.scoreboards.AnimatedScoreboardBuilder
import com.mattmx.ktgui.scoreboards.ScoreboardBuilder
import org.bukkit.scoreboard.Score

// todo change to components
inline fun scoreboard(title: String = "null", build: ScoreboardBuilder.() -> Unit) : ScoreboardBuilder {
    val builder = ScoreboardBuilder(title)
    build.invoke(builder)
    return builder
}

inline fun animatedScoreboard(title: String = "null", updateEvery: Long = 20, build: ScoreboardBuilder.() -> Unit) : AnimatedScoreboardBuilder {
    val builder = AnimatedScoreboardBuilder(title, updateEvery)
    build.invoke(builder)
    return builder
}

fun main() {
    val scoreboard = scoreboard("meow") {
        add("+----------+")
        add("|   Test   |")
        add("+----------+")
    }

    // Will automatically change the component for all subscribed players
    scoreboard[1] = scoreboard[1].replace("Test", "Heya")
}