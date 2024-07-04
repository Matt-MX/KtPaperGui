package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.screen.pagination.cramMultiPageScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.entity.Player

class NewCramMultiPageExample : Example {
    val gui = cramMultiPageScreen(!"Materials") {
        reserve(last() - 8..last())

        button(Material.SPECTRAL_ARROW) {
            named(!"&aNext")
            click.left { navigateNextPage() }
        } slot last()

        button(Material.SPECTRAL_ARROW) {
            named(!"&cLast")
            click.left { navigatePreviousPage() }
        } slot last() - 8

        +Material.values().map { button(it) {} }
    }

    override fun run(player: Player) = gui.open(player)
}