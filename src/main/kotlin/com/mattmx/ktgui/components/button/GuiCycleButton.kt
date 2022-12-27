package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class GuiCycleButton(
    material: Material = Material.STONE,
    item: ItemStack? = null,
) : GuiButton(
    material, item
) {
    protected val map: MutableMap<String, ItemStack> = mutableMapOf()
    protected var selected: Int = 0
    protected var changed: ((ButtonClickedEvent) -> Unit)? = null

    init {
        click {
            right = {
                nextItem(player)
                changed?.invoke(ButtonClickedEvent(player, this.event, this@GuiCycleButton))
            }
            left = {
                prevItem(player)
                changed?.invoke(ButtonClickedEvent(player, this.event, this@GuiCycleButton))
            }
        }
    }

    fun items(items: MutableMap<String, ItemStack>.() -> Unit) : GuiCycleButton {
        items.invoke(map)
        this.item = getSelectedItem()
        return this
    }

    fun getSelectedId() : String? {
        return map.keys.toMutableList().getOrNull(selected)
    }

    fun getSelectedItem() : ItemStack? {
        return map[getSelectedId()]
    }

    fun nextItem(player: Player) {
        selected++
        if (selected >= map.size)
            selected = 0
        this.item = getSelectedItem()
        update(player)
    }

    fun prevItem(player: Player) {
        selected--
        if (selected < 0)
            selected = map.size - 1
        this.item = getSelectedItem()
        update(player)
    }

    fun changed(cb: ButtonClickedEvent.() -> Unit): GuiCycleButton {
        changed = cb
        return this
    }

    override fun copy(parent: IGuiScreen): GuiButton {
        val copy = GuiCycleButton()
        copy.map.putAll(map)
        copy.changed = changed
        copy.parent = parent
        copy.click = click
        copy.close = close
        copy.item = item
        return copy
    }
}