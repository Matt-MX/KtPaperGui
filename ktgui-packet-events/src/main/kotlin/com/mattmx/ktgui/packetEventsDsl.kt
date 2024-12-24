package com.mattmx.ktgui

import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.mattmx.ktgui.impl.PacketGuiButton
import com.mattmx.ktgui.impl.PacketGuiScreen
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.refreshBlock
import net.kyori.adventure.text.Component
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun renderingGui(
    title: Component,
    type: GuiType,
    refresh: Duration = 1.seconds,
    block: PacketGuiScreen<*>.() -> Unit
): PacketGuiScreen<*> {
    val gui = PacketGuiScreen(type, title).apply(block)

    gui.refreshBlock(refresh) { block(gui) }

    return gui
}

fun gui(title: Component, type: GuiType, block: PacketGuiScreen<*>.() -> Unit) =
    PacketGuiScreen(type, title).apply(block)

fun button(material: ItemType, block: PacketGuiButton<*>.() -> Unit) =
    PacketGuiButton(material).apply(block)