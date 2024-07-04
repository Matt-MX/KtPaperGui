package com.mattmx.ktgui.examples

import com.mattmx.ktgui.KotlinGui
import com.mattmx.ktgui.dsl.event
import com.mattmx.ktgui.scoreboards.dynamicScoreboard
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.placeholders
import com.mattmx.ktgui.utils.pretty
import com.mattmx.ktgui.utils.seconds
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import java.text.DateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class SignalScoreboardExample(
    plugin: KotlinGui
) : Example {
//    var timesDisplayed by signal(0)

    init {
        event<PlayerInteractEvent>(plugin) {
//            timesDisplayed++
        }
    }

    val board = dynamicScoreboard(!"&#3D7068&lYour Server") {

        val le = 40
        var i = 0
        val line = scoreboardLine {
            i = if (i > le) 0 else i + 1

            val l = max(0, i - 1)
            val r = min(le, le - i + 1)

            !" &#3D7068&m${" ".repeat(l)}&#43C59E&m &#3D7068&m${" ".repeat(r)}"
        } updateEvery 5L

        +line
        +!"  &#3DFAFFLobby"
        +Component.empty()

        +{ !"  &#3DFAFF$ &f${(0..1_000_000_000_000).random().pretty()}" } updateEvery 1.5.seconds
        +{ !"  &#3DFAFF\uD83D\uDD25 &f%server_online%".placeholders(null) } updateEvery 5.seconds
        +{ !"  &#3DFAFF⌛ &f${DateFormat.getTimeInstance().format(Date())}" } updateEvery 1.seconds

        +Component.empty()
        +line
    }

    override fun run(player: Player) {
        val shown = board.isShownFor(player)

        if (shown) {
            board.removeFor(player)
            player.sendMessage(!"&fHiding scoreboard")
        } else {
            player.sendMessage(!"&fShowing scoreboard")
            board.showFor(player)
        }
    }
}