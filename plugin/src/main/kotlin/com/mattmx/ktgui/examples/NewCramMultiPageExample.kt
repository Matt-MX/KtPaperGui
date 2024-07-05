package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.screen.pagination.cramMultiPageScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.plus
import com.mattmx.ktgui.utils.translatable
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.entity.Player

class NewCramMultiPageExample : Example {
    val gui = cramMultiPageScreen(!"Materials &71") {
        // These are slots that we should not cram fill with items
        // In this example we're reserving the bottom row for pagination controls.
        reserve(last() - 8..last())

        button(Material.SPECTRAL_ARROW) {
            named(!"&aNext")
            click.left(::navigateNextPage)
        } slot last()

        button(Material.SPECTRAL_ARROW) {
            named(!"&cLast")
            click.left(::navigatePreviousPage)
        } slot last() - 8

        pageChange {
            title = !"Materials &7${currentPage + 1}"
        }

        +Material.values().mapIndexed { index, material ->
            button(material) {
                lore {
                    +!"&aThis is item $index"
                }
                click.left {
                    reply(!"&aClicked " + material.translationKey().translatable + !"&a ($index)")
                }
            }
        }
    }

    override fun run(player: Player) = gui.open(player)
}