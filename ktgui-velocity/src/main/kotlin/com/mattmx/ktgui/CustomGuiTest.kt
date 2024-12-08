package com.mattmx.ktgui

import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.util.not

class CustomGuiTest : PacketGuiScreen<CustomGuiTest>(GuiType.ofRows(6), !"Test") {

    init {
        button(ItemTypes.ARROW) {

        }
    }

}