package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.components.button.SignalButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.components.screen.IGuiScreen
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import java.util.function.Supplier

inline fun gui(title: Component, rows: Int = 6, block: GuiScreen.() -> Unit) = GuiScreen(title, rows).apply(block)
inline fun gui(title: Component, type: InventoryType, block: GuiScreen.() -> Unit) = GuiScreen(title, type = type).apply(block)

inline fun <reified T : IGuiButton<*>> IGuiScreen.button(
    constructor: Supplier<T> = Supplier { GuiButton() as T },
    block: T.() -> Unit
) = constructor.get().apply(block).apply { childOf(this@button) }

inline fun <reified T : IGuiButton<*>> button(
    constructor: Supplier<T> = Supplier { GuiButton() as T },
    block: T.() -> Unit
) = constructor.get().apply(block)

inline fun button(material: Material, block: GuiButton<*>.() -> Unit) = GuiButton(material).apply(block)
inline fun IGuiScreen.button(material: Material, block: GuiButton<*>.() -> Unit) =
    GuiButton(material).apply(block).apply { childOf(this@button) }
fun IGuiScreen.signalButton(material: Material, block: SignalButton.() -> Unit) =
    SignalButton(material, block).apply { childOf(this@signalButton) }