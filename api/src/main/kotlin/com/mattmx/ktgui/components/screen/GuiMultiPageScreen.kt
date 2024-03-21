package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.components.button.GuiButton
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

open class GuiMultiPageScreen(
    title: Component = Component.empty(),
    rows: Int = 1,
    protected var maxPages: Int = -1,
    protected var minSlot: Int = 9,
    protected var maxSlot: Int = rows * 9 - 9,
    private val startPage: Int = 0
) : GuiScreen(title, rows) {
    protected var page = startPage
    protected var itemList = arrayListOf<GuiButton<*>>()

    init {
        open { p ->
            update(p)
        }
        click { c ->
             c.any {
                if (slot in minSlot until maxSlot) {
                    val index = slot + (page * pageSize()) - minSlot
                    itemList.getOrNull(index)?.onButtonClick(this)
                } else {
                    items[slot]?.onButtonClick(this)
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
                player.openInventory.setItem(normalized, it.formatIntoItemStack(player))
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

    operator fun plusAssign(button: GuiButton<*>) = add(button)

    fun add(button: GuiButton<*>) {
        itemList += button
    }

    override fun copy(): IGuiScreen {
        val screen = GuiMultiPageScreen(title, rows, maxPages, minSlot, maxSlot, startPage)
        screen.items = items.mapValues { it.value.copy(screen) }.toMutableMap() as HashMap<Int, GuiButton<*>>
        screen.itemList = itemList.map { it.copy(screen) }.toMutableList() as ArrayList<GuiButton<*>>
        screen.type = type
        screen.rows = rows
        screen.click = click
        screen.moveCallback = moveCallback
        screen.closeCallback = closeCallback
        screen.quitCallback = quitCallback
        screen.openCallback = openCallback
        return screen
    }
}