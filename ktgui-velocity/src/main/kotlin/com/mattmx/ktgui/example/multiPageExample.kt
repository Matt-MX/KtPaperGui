package com.mattmx.ktgui.example

import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.mattmx.ktgui.button
import com.mattmx.ktgui.click.ClickTypes
import com.mattmx.ktgui.gui
import com.mattmx.ktgui.impl.PacketGuiInventoryScreen
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.trait.impl.pages
import com.mattmx.ktgui.util.not

fun createMultiPageGui(): PacketGuiInventoryScreen<*> {
    return gui(!"Nav Page 1", GuiType.rows(6)) {
        pages {

            onPageChange {
                title = !"Nav Page ${currentPage + 1}"
            }

            page(0) {
                button(ItemTypes.DIAMOND) {
                    named(!"Page 1")
                } slot guiType.middle
            }

            page(1) {
                button(ItemTypes.DIRT) {
                    named(!"Page 2")
                } slot guiType.middle
            }

            -button(ItemTypes.ARROW) {
                click(ClickTypes.LEFT) {
                    currentPage--
                }
                slot(guiType.last - 8)
            }

            -button(ItemTypes.ARROW) {
                click(ClickTypes.LEFT) {
                    currentPage++
                }
                slot(guiType.last)
            }
        }
    }
}