package com.mattmx.ktgui

import com.mattmx.ktgui.button.GuiButton
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.screen.GuiType
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component

fun multiPlatformGui(title: Component, type: GuiType, block: GuiScreen<*, *>.() -> Unit): GuiScreen<*, *> {
    return KtGui.getInstance()
        .createPlatformGui(title, type)
        .apply(block)
}

fun multiPlatformButton(type: Key, block: GuiButton<*, *, *, *>.() -> Unit): GuiButton<*, *, *, *> {
    return KtGui.getInstance()
        .createPlatformButtonOfType(type)
        .apply(block)
}