package com.mattmx.ktgui.example

import com.mattmx.ktgui.impl.PacketGuiInventoryScreen
import com.mattmx.ktgui.gui
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.util.not

fun createOptionsGui(settingsSchema: PlayerSettingsSchema): PacketGuiInventoryScreen<*> {
    var i = 0
    return gui(!"Settings", GuiType.rows(2)) {
        booleanButton(settingsSchema::optionOne) slot i++
        booleanButton(settingsSchema::optionTwo) slot i++
    }
}