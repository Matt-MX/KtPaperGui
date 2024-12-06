package com.mattmx.ktgui

import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.mattmx.ktgui.screen.GuiType
import net.kyori.adventure.text.Component

fun gui(title: Component, type: GuiType, block: PacketGuiScreen<*>.() -> Unit) =
    PacketGuiScreen(type, title).apply(block)

fun button(material: ItemType, block: PacketGuiButton<*>.() -> Unit) =
    PacketGuiButton(material).apply(block)