package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.GuiPattern
import com.mattmx.ktgui.components.screen.pagination.GuiCramMultiPageScreen
import com.mattmx.ktgui.components.screen.pagination.cramMultiPageScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.sound.sound
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.translatable
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.entity.Player

class CramStrategyExample : Example {

    val cram = cramMultiPageScreen(!"Cram pretty", 5) {
        val pattern = GuiPattern(
            """
            ---------
            -xxxxxxx-
            -xxxxxxx-
            ---------
        """.trimIndent()
        )

        tryGetNextSlot = GuiCramMultiPageScreen.NextSlotStrategy.pattern('x', pattern)

        button(Material.SPECTRAL_ARROW) {
            named(!"&aNext")
            click.left(::navigateNextPage)
        } slot last() - 1

        button(Material.SPECTRAL_ARROW) {
            named(!"&cLast")
            click.left(::navigatePreviousPage)
        } slot last() - 7

        +Material.entries
            .filter { it.isBlock }
            .map {
                button(it) {
                    named(it.translationKey().translatable.color(TextColor.color(97, 237, 195)))

                    click.left {
                        player.playSound(sound(Key.key("minecraft:block.${it.key.value()}.break")).build())
                    }
                }
            }
    }

    override fun run(player: Player) = cram.open(player)
}