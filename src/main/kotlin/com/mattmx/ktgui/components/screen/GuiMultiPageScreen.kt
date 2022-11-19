package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.components.button.IGuiButton
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

open class GuiMultiPageScreen(
    title: String = "null",
    rows: Int = 1,
    protected var maxPages: Int = -1,
    protected var minSlot: Int = 9,
    protected var maxSlot: Int = rows * 9 - 9,
    private val startPage: Int = 0
) : GuiScreen(title, rows) {
    protected var page = startPage
    protected var itemList = arrayListOf<Int>()

    init {
        open { p ->
            update(p)
        }
        click { ce ->
            ce.generic = { e ->
                if (e.rawSlot in minSlot until maxSlot) {
                    val index = e.rawSlot + (page * pageSize()) - minSlot
                    itemList.getOrNull(index)?.let { items[it]?.thisClicked(e) }
                } else {
                    items[e.rawSlot]?.thisClicked(e)
                }
            }
        }
    }

    fun setPage(player: Player, value: Int): Boolean {
        if (!this.canGotoPage(value)) return false
        this.page = value
        update(player)
        return true
    }

    fun update(player: Player) {
        for (slot in (page * pageSize()) until (page + 1) * pageSize()) {
            val normalized = slot - (page * pageSize()) + minSlot
            itemList.getOrNull(slot)?.also {
                player.openInventory.setItem(normalized, items[it]?.formatIntoItemStack(player))
            } ?: run { player.openInventory.setItem(normalized, null) }
        }
    }

    fun getCurrentPage() : Int {
        return page
    }

    fun pageSize(): Int {
        return maxSlot - minSlot
    }

    fun canGotoPage(page: Int): Boolean {
        return if (maxPages <= 0) page >= 0 else page in 0..maxPages
    }

    fun gotoNextPage(player: Player): Boolean {
        return setPage(player, page + 1)
    }

    fun gotoPrevPage(player: Player): Boolean {
        return setPage(player, page - 1)
    }

    fun add(button: IGuiButton) {
        button childOf this
        itemList.add(items.size - 1)
    }

    override fun copy(): IGuiScreen {
        val screen = GuiMultiPageScreen(title, rows, maxPages, minSlot, maxSlot, startPage)
        screen.items = items.mapValues { it.value.copy(screen) }.toMutableMap() as HashMap<Int, IGuiButton>
        screen.itemList = itemList.toMutableList() as ArrayList<Int>
        screen.type = type
        screen.rows = rows
        screen.click = click
        screen.move = move
        screen.close = close
        screen.quit = quit
        screen.open = open
        return screen
    }
}