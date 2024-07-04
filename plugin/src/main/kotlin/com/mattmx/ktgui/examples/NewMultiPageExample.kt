package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.screen.pagination.multiPageGui
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.entity.Player

class NewMultiPageExample : Example {
    val gui = multiPageGui(!"Test") {
        button(Material.SPECTRAL_ARROW) {
            named(!"&aLast")
            click.left { navigatePreviousPage() }
        } slot last()

        button(Material.SPECTRAL_ARROW) {
            named(!"&aNext")
            click.left { navigateNextPage() }
        } slot last()

        page {
            button(Material.BEACON) { named(!"Page 1 test") } slot middle()
        }

        page {
            button(Material.NETHER_STAR) { named(!"Page 2 test") } slot middle()
        }
    }

    override fun run(player: Player) = gui.open(player)
}