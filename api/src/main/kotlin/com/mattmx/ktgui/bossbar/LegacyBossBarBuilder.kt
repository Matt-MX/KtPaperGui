package com.mattmx.ktgui.bossbar

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player

@Deprecated("Simply use the BossBar class in adventure.", ReplaceWith("net.kyori.adventure.bossbar.BossBar"))
open class LegacyBossBarBuilder {
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

@Deprecated("Simply use the BossBar class in adventure.", ReplaceWith("net.kyori.adventure.bossbar.BossBar"))
inline fun bossBar(builder: LegacyBossBarBuilder.() -> Unit) = bossBarDsl(LegacyBossBarBuilder(), builder)

@Deprecated("Simply use the BossBar class in adventure.", ReplaceWith("net.kyori.adventure.bossbar.BossBar"))
inline fun animatedBossBar(period: Long? = null, builder: AnimatedBossBar.() -> Unit) = bossBarDsl(AnimatedBossBar(period), builder)

@Deprecated("Simply use the BossBar class in adventure.", ReplaceWith("net.kyori.adventure.bossbar.BossBar"))
inline fun <T : LegacyBossBarBuilder> bossBarDsl(instance: T, builder: T.() -> Unit) : T {
    builder(instance)
    instance.build()
    return instance
}