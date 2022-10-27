package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.scoreboards.AnimatedScoreboardBuilder
import com.mattmx.ktgui.scoreboards.ScoreboardBuilder

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