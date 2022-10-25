package com.mattmx.ktgui.scoreboards

import org.bukkit.scoreboard.Scoreboard

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