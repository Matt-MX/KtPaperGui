package com.mattmx.ktgui.example

import com.mattmx.ktgui.impl.PacketGuiScreen
import com.mattmx.ktgui.gui
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.util.not

fun createOptionsGui(settingsSchema: PlayerSettingsSchema): PacketGuiScreen<*> {
    var i = 0
    return gui(!"Settings", GuiType.ofRows(2)) {
        booleanButton(settingsSchema::optionOne) slot i++
        booleanButton(settingsSchema::optionTwo) slot i++
    }
}