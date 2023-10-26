package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.scoreboards.AnimatedScoreboardBuilder
import com.mattmx.ktgui.scoreboards.ScoreboardBuilder

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
    val scoreboard = scoreboard("title") {
        + "test"
        val helloWorldLine = +"hello world"
    }

    // Will automatically change the component for all subscribed players
    helloWorldLine = "meow"
}