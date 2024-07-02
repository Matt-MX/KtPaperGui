package com.mattmx.ktgui.examples

import com.mattmx.ktgui.scoreboards.scoreboard
import com.mattmx.ktgui.utils.not
import net.kyori.adventure.text.Component
import java.text.DateFormat
import java.util.*

class SignalScoreboardExample {

    val board = scoreboard(!"&fSignals x Scoreboard") {
        +!"&fFirst line"
        +Component.empty()
        +!"&f${DateFormat.getTimeInstance().format(Date())}"
        +Component.empty()
    }
}