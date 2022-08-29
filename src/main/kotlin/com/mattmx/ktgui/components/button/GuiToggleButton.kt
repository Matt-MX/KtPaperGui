package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

open class GuiToggleButton(
    val enabledItem: ItemStack,
    val disabledItem: ItemStack,
    var default: Boolean = false,
    var onChange: ((GuiToggleButton, InventoryClickEvent?, Boolean) -> Unit)? = null
) : GuiButton() {

    private var current = default

    init {
        this.item = if (default) enabledItem else disabledItem
    }

    override fun thisClicked(e: InventoryClickEvent) {
        changeState(e.whoClicked as Player, e)
        super.thisClicked(e)
    }

    fun onChange(cb: (GuiToggleButton, InventoryClickEvent?, Boolean) -> Unit): GuiToggleButton {
        onChange = cb
        return this
    }

    fun changeState(player: Player, e: InventoryClickEvent? = null) {
        setState(!current, player)
        onChange?.invoke(this, e, current)
    }

    fun setState(value: Boolean, player: Player) {
        current = value
        item = if (current) enabledItem else disabledItem
        update(player)
    }

    override fun copy(parent: IGuiScreen): GuiToggleButton {
        val button = GuiToggleButton(enabledItem, disabledItem, default, onChange)
        button.parent = parent
        return button
    }
}