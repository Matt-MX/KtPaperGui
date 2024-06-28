package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.scoreboards.AnimatedScoreboardBuilder
import com.mattmx.ktgui.scoreboards.LegacyScoreboardBuilder

@Deprecated("No longer supported", ReplaceWith("scoreboard"))
inline fun scoreboard(title: String = "null", build: LegacyScoreboardBuilder.() -> Unit): LegacyScoreboardBuilder {
    val builder = LegacyScoreboardBuilder(title)
    build.invoke(builder)
    return builder
}

@Deprecated("No longer supported", ReplaceWith("scoreboard"))
inline fun animatedScoreboard(
    title: String = "null", updateEvery: Long = 20, build: LegacyScoreboardBuilder.() -> Unit
): AnimatedScoreboardBuilder {
    val builder = AnimatedScoreboardBuilder(title, updateEvery)
    build.invoke(builder)
    return builder
}