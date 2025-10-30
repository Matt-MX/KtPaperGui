package com.mattmx.ktgui

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.mattmx.ktgui.impl.PacketGuiButton
import com.mattmx.ktgui.impl.PacketGuiInventoryScreen
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.refreshBlock
import net.kyori.adventure.text.Component
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun renderingGui(
    title: Component,
    type: GuiType,
    refresh: Duration = 1.seconds,
    block: PacketGuiInventoryScreen<*>.() -> Unit
): PacketGuiInventoryScreen<*> {
    val gui = PacketGuiInventoryScreen(type, title).apply(block)

    gui.refreshBlock(refresh) { block(gui) }

    return gui
}

fun gui(title: Component, type: GuiType, block: PacketGuiInventoryScreen<*>.() -> Unit) =
    PacketGuiInventoryScreen(type, title).apply(block)

fun button(item: ItemStack, block: PacketGuiButton<*>.() -> Unit) =
    PacketGuiButton(item).apply(block)

fun button(material: ItemType, block: PacketGuiButton<*>.() -> Unit) =
    PacketGuiButton(ItemStack.builder().type(material).build()).apply(block)