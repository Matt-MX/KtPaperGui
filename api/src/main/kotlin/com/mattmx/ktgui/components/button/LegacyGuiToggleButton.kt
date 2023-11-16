package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

@Deprecated("No longer supported", ReplaceWith("GuiToggleButton"))
open class LegacyGuiToggleButton(
    val enabledItem: ItemStack,
    val disabledItem: ItemStack,
) : GuiButton<LegacyGuiToggleButton>() {
    private var current = false
    var changed: ((ButtonClickedEvent<LegacyGuiToggleButton>) -> Unit)? = null

    init {
        this.item = if (current) enabledItem else disabledItem
    }

    override fun onButtonClick(e: ButtonClickedEvent<*>) {
        changeState(e.player, e.event)
        update(e.player)
        super.onButtonClick(e)
    }

    fun enabledOnDefault(state: Boolean) : LegacyGuiToggleButton {
        current = state
        this.item = if (current) enabledItem else disabledItem
        return this
    }

    fun onChange(cb: ButtonClickedEvent<LegacyGuiToggleButton>.() -> Unit): LegacyGuiToggleButton {
        changed = cb
        return this
    }

    fun enabled() : Boolean {
        return current
    }

    fun changeState(player: Player, e: InventoryClickEvent) {
        setState(!current, player)
        changed?.invoke(ButtonClickedEvent<LegacyGuiToggleButton>(player, e).apply { button = this@LegacyGuiToggleButton })
    }

    fun setState(value: Boolean, player: Player) {
        current = value
        item = if (current) enabledItem else disabledItem
        update(player)
    }

    override fun copy(parent: IGuiScreen): LegacyGuiToggleButton {
        val button = LegacyGuiToggleButton(enabledItem, disabledItem)
        button.enabledOnDefault(current)
        button.changed = changed
        button.parent = parent
        return button
    }
}