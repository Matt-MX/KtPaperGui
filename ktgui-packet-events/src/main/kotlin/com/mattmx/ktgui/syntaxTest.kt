package com.mattmx.ktgui

import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.mattmx.ktgui.click.ClickTypes
import com.mattmx.ktgui.screen.GuiType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

fun main() {
    gui(Component.empty(), GuiType.ofRows(6)) {

        click {

        }

        button(ItemTypes.TURTLE_SCUTE) {
            named(Component.text("Test"))

            lore {
                +Component.text("Lore").color(NamedTextColor.GOLD)
            }

            enchant(EnchantmentTypes.MENDING, 1)

            click {
                (ClickTypes.DROP_ALL + ClickTypes.DROP) {
                    println("clicked")
                }
            }
        }
    }
}