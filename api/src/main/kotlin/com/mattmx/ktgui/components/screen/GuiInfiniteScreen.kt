package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.components.button.IGuiButton
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

open class GuiInfiniteScreen(
    title: String = "null",
    rows: Int = 1
) : GuiScreen(title, rows, null) {
    var items2D = hashMapOf<Pair<Int, Int>, IGuiButton>()
    var x = 0
    var y = 0

    fun getCoords(button: IGuiButton) = items2D.filter { it.value == button }

    fun setSlot(x: Int, y: Int, button: IGuiButton) {
        items2D[x to y] = button
    }

    fun coordsUsed() = items2D.keys

    operator fun set(coords: Pair<Int, Int>, button: IGuiButton) = setSlot(coords.first, coords.second, button)

    operator fun set(x: Int, y: Int, button: IGuiButton) = setSlot(x, y, button)

    override fun open(player: Player) {
        val inv: Inventory = if (type != null) Bukkit.createInventory(player, type!!, title) else Bukkit.createInventory(player, totalSlots(), title)

        if (firePreBuildEvent(player)) return

        items2D.forEach { (coord, item) ->
            if (coord.first >= x && coord.first <= x + 9
                && coord.second >= y && coord.second <= y + rows) {
                val normalSlot = (coord.second - y) * 9 + (coord.first - x)
                if (normalSlot >= 0 && normalSlot <= last()) {
                    inv.setItem(normalSlot, item.formatIntoItemStack(player))
                }
            }
        }
        items.forEach { (slot, item) ->
            if (slot < inv.size && slot >= 0)
                inv.setItem(slot, item.formatIntoItemStack(player))
        }
        openIfNotCancelled(player, inv)
    }
}