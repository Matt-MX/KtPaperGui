package com.mattmx.ktgui

import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.protocol.world.MaterialType
import com.mattmx.ktgui.click.ClickType
import com.mattmx.ktgui.click.ClickTypes
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.stateful
import com.mattmx.ktgui.util.not

private enum class States {
    INITIAL,
    RANDOM
}

fun createStatefulGui(): PacketGuiScreen<*> {

    val items = listOf(
        ItemTypes.DIRT,
        ItemTypes.STONE,
        ItemTypes.COD
    )

    return gui(!"Stateful", GuiType.ofRows(6)) {
        stateful(States.INITIAL) {

            state(States.INITIAL) {
                button(ItemTypes.DIAMOND_SWORD) {
                    named(!"Click to change")
                    click(ClickTypes.LEFT) {
                        state = States.RANDOM
                    }
                } slot guiType.middle
            }

            state(States.RANDOM) {
                for (slot in 0..gui.guiType.getTotalSlots()) {
                    button(items.random()) {
                        named(!"Meow")
                    } slot slot
                }

                button(ItemTypes.ARROW) {
                    named(!"&cClose")
                    click(ClickTypes.LEFT) {
                        forcefullyClose(getPlayer())
                    }
                } slot guiType.row(guiType.rows!!).middle
            }
        }
    }
}