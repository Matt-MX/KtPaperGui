@file:Suppress("UNCHECKED_CAST")

package com.mattmx.ktgui.impl

import com.mattmx.ktgui.bukkit
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.event.ParentEventCallback
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PaperGuiScreen<T : PaperGuiScreen<T>>(
    guiType: GuiType,
    title: Component
) : GuiScreen<Player, PaperGuiButton<*>>(guiType, title) {
    override val close by lazy { ParentEventCallback<Player, T>(this as T) }
    override val open by lazy { ParentEventCallback<Player, T>(this as T) }

    override fun refreshTitle(player: Player) {
        player.openInventory.title = LegacyComponentSerializer.legacySection().serialize(title)
    }

    override fun refresh(player: Player) {
        player.openInventory.topInventory.contents = getInventoryContents(player.openInventory.topInventory.size)
    }

    override fun open(player: Player) = apply {
        val inventory = guiType.rows?.let { rows -> Bukkit.createInventory(null, rows * 9, title) }
            ?: Bukkit.createInventory(null, guiType.type.bukkit, title)

        inventory.contents = getInventoryContents(inventory.contents.size)

        open.apply(player)
        setActiveGui(player)

        player.openInventory(inventory)
    } as T

    private fun getInventoryContents(size: Int) : Array<ItemStack?> {
        return arrayOfNulls<ItemStack>(size).mapIndexed { slot, _ ->
            items[slot]?.buildItem()
        }.toTypedArray()
    }

}
