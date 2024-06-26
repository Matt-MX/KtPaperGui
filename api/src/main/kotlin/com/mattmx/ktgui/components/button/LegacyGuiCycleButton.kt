package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

@Deprecated("No longer supported", ReplaceWith("LoreCycleButton"))
class LegacyGuiCycleButton(
    material: Material = Material.STONE,
    item: ItemStack? = ItemStack(material),
) : GuiButton<LegacyGuiCycleButton>(
    material, item
) {
    protected val map: MutableMap<String, ItemStack> = mutableMapOf()
    protected var selected: Int = 0
    protected var changed: ((ButtonClickedEvent<LegacyGuiCycleButton>) -> Unit)? = null

    init {
        click {
            ClickType.RIGHT {
                nextItem(player)
                changed?.invoke(ButtonClickedEvent<LegacyGuiCycleButton>(player, this.event).apply {
                    button = this@LegacyGuiCycleButton
                })
            }
            ClickType.LEFT {
                prevItem(player)
                changed?.invoke(ButtonClickedEvent<LegacyGuiCycleButton>(player, this.event).apply {
                    button = this@LegacyGuiCycleButton
                })
            }
        }
    }

    fun items(items: MutableMap<String, ItemStack>.() -> Unit): LegacyGuiCycleButton {
        items.invoke(map)
        this.item = getSelectedItem()
        return this
    }

    fun getSelectedId(): String? {
        return map.keys.toMutableList().getOrNull(selected)
    }

    fun getSelectedItem(): ItemStack? {
        return map[getSelectedId()]
    }

    fun nextItem(player: Player) {
        selected++
        if (selected >= map.size) selected = 0
        this.item = getSelectedItem()
        update(player)
    }

    fun prevItem(player: Player) {
        selected--
        if (selected < 0) selected = map.size - 1
        this.item = getSelectedItem()
        update(player)
    }

    fun changed(cb: ButtonClickedEvent<LegacyGuiCycleButton>.() -> Unit): LegacyGuiCycleButton {
        changed = cb
        return this
    }

    override fun copy(parent: IGuiScreen): LegacyGuiCycleButton {
        val copy = LegacyGuiCycleButton()
        copy.map.putAll(map)
        copy.changed = changed
        copy.parent = parent
        copy.click = click
        copy.item = item
        return copy
    }
}