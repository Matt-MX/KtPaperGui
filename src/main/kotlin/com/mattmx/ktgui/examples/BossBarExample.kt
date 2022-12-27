package com.mattmx.ktgui.examples

import com.mattmx.ktgui.bossbar.animatedBossBar
import com.mattmx.ktgui.bossbar.bossBar
import com.mattmx.ktgui.extensions.color
import org.bukkit.boss.BarColor

object BossBarExample {
    val static = bossBar {
        title = "&cStatic Bossbar".color()
        color = BarColor.RED
    }

    var green = false
    val animated = animatedBossBar(20L) {
        title = "&cI'm animated!".color()
        color = BarColor.RED
    }.update {
        green = !green
        if (green) {
            title = "&aI'm animated!".color()
            color = BarColor.GREEN
        } else {
            title = "&cI'm animated".color()
            color = BarColor.RED
        }
    }
}