package com.mattmx.ktgui.bossbar

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player

open class BossBarBuilder {
    var title = ""
    var color: BarColor = BarColor.WHITE
        set(value) {
            field = value
            if (this::bar.isInitialized)
                bar.color = value
        }
    var style: BarStyle = BarStyle.SOLID
        set(value) {
            field = value
            if (this::bar.isInitialized)
                bar.style = value
        }
    val flags = arrayListOf<BarFlag>()
    lateinit var bar: BossBar

    fun build() {
        bar = Bukkit.createBossBar(title, color, style, *flags.toTypedArray())
    }

    operator fun plusAssign(player: Player) = bar.addPlayer(player)
    operator fun minusAssign(player: Player) = bar.removePlayer(player)
    fun clear() = bar.removeAll()
}

inline fun bossBar(builder: BossBarBuilder.() -> Unit) = bossBarDsl(BossBarBuilder(), builder)
inline fun animatedBossBar(period: Long? = null, builder: AnimatedBossBar.() -> Unit) = bossBarDsl(AnimatedBossBar(period), builder)

inline fun <T : BossBarBuilder> bossBarDsl(instance: T, builder: T.() -> Unit) : T {
    builder(instance)
    instance.build()
    return instance
}