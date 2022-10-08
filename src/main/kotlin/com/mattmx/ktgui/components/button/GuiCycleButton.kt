package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class GuiCycleButton(
    material: Material = Material.STONE,
    item: ItemStack? = null,
    protected val map: MutableMap<String, ItemStack>,
    protected var selected: Int = 0,
    protected var changed: ((GuiCycleButton, InventoryClickEvent?) -> Unit)? = null
) : GuiButton(
    material, item
) {

    init {
        click {
            right = { e ->
                nextItem(e.whoClicked as Player)
                changed?.invoke(this@GuiCycleButton, e)
            }
            left = { e ->
                prevItem(e.whoClicked as Player)
                changed?.invoke(this@GuiCycleButton, e)
            }
        }
        this.item = getSelectedItem()
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

    fun changed(cb: (GuiCycleButton, InventoryClickEvent?) -> Unit): GuiCycleButton {
        changed = cb
        return this
    }

    override fun copy(parent: IGuiScreen): GuiButton {
        val copy = GuiCycleButton(map = map)
        copy.notClicked = notClicked
        copy.changed = changed
        copy.parent = parent
        copy.click = click
        copy.close = close
        return copy
    }
}