package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.screen.hotbar
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.math.abs

class HotbarExample : Example {
    val gui = hotbar {
        var counter = 0
        scroll {
            event.isCancelled = false
            counter += difference
            player.sendActionBar(!"&fScrolled: $counter ${if (difference > 0) "&a+" else "&c-"}${abs(difference)}")

            if (counter > 100) {
                open(player)
            }
        }

        button(Material.NETHER_STAR) {
            named(!"&aServer Selector")
            click.left { player.sendMessage(!"&7<Clicked>") }
        } slot hotbarMiddle() holdingSendActionBar !"&7Click to change servers."

        if (counter > 100) {
            button(Material.PLAYER_HEAD) {
                named(!"&aYou found a secret!")
            } slot hotbarLast()
        }

    }

    override fun run(player: Player) = gui.open(player)
}