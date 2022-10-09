package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

open class GuiToggleButton(
    val enabledItem: ItemStack,
    val disabledItem: ItemStack,
) : GuiButton() {
    private var current = false
    var changed: ((ButtonClickedEvent) -> Unit)? = null

    init {
        this.item = if (current) enabledItem else disabledItem
    }

    override fun thisClicked(e: InventoryClickEvent) {
        changeState(e.whoClicked as Player, e)
        update(e.whoClicked as Player)
        super.thisClicked(e)
    }

    fun enabledOnDefault(state: Boolean) : GuiToggleButton {
        current = state
        this.item = if (current) enabledItem else disabledItem
        return this
    }

    inline fun onChange(noinline cb: ButtonClickedEvent.() -> Unit): GuiToggleButton {
        changed = cb
        return this
    }

    fun enabled() : Boolean {
        return current
    }

    fun changeState(player: Player, e: InventoryClickEvent) {
        setState(!current, player)
        changed?.invoke(ButtonClickedEvent(player, e, this))
    }

    fun setState(value: Boolean, player: Player) {
        current = value
        item = if (current) enabledItem else disabledItem
        update(player)
    }

    override fun copy(parent: IGuiScreen): GuiToggleButton {
        val button = GuiToggleButton(enabledItem, disabledItem)
        button.enabledOnDefault(current)
        button.changed = changed
        button.parent = parent
        return button
    }
}