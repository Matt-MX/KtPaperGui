package com.mattmx.ktgui.example

import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.mattmx.ktgui.impl.PacketGuiInventoryScreen
import com.mattmx.ktgui.button
import com.mattmx.ktgui.click.ClickTypes
import com.mattmx.ktgui.gui
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.stateful
import com.mattmx.ktgui.util.not

private enum class States {
    INITIAL,
    RANDOM
}

fun createStatefulGui(): PacketGuiInventoryScreen<*> {

    val items = listOf(
        ItemTypes.DIRT,
        ItemTypes.STONE,
        ItemTypes.COD
    )

    return gui(!"Stateful", GuiType.ofRows(5)) {
        updateOnModify(true)

        stateful(States.INITIAL) {

            state(States.INITIAL) {
                button(ItemTypes.DIAMOND_SWORD) {
                    named(!"<blue>Click to change")
                    click(ClickTypes.LEFT) {
                        state = States.RANDOM
                    }
                } slot guiType.middle
            }

            state(States.RANDOM) {
                for (slot in 0..<gui.guiType.getTotalSlots()) {
                    button(items.random()) {
                        named(!"<light_purple>Meow")

                        lore += !"<light_gray>Click to reset!"

                        click(ClickTypes.ALL_CLICK_TYPES) {
                            state = States.INITIAL
                        }
                    } slot slot
                }

                button(ItemTypes.ARROW) {
                    named(!"<red>Close")
                    click(ClickTypes.LEFT) {
                        forcefullyClose(getPlayer())
                    }
                } slot guiType.row(guiType.rows!!).middle
            }
        }
    }
}