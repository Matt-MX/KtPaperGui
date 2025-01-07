package com.mattmx.ktgui.scoreboard

import net.kyori.adventure.text.Component

fun interface ScoreboardLine {

    fun getComponent() : Component

    companion object {
        fun of(component: Component) = ScoreboardLine { component }
        fun empty() = ScoreboardLine { Component.empty() }
    }
}