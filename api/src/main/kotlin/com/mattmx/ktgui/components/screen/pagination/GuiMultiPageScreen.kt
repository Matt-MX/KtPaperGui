package com.mattmx.ktgui.components.screen.pagination

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.button.ButtonClickedEvent
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.event.EventCallback
import com.mattmx.ktgui.utils.not
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class GuiMultiPageScreen(
    title: Component,
    rows: Int = 6
) : GuiScreen(title, rows) {
    var currentPage = 0
        set(value) {
            field = value
            refresh()
            pageChange.invoke(value)
        }
    val pageChange = EventCallback<Int>()
    val pages = Collections.synchronizedList(arrayListOf<GuiScreen>())

    override fun refresh() {
        val inv = arrayOfNulls<ItemStack?>(totalSlots())

        // Now apply the items
        val current = pages.getOrNull(currentPage)

        current?.items?.forEach { (slot, item) ->
            inv[slot] = item.formatIntoItemStack()
        }

        items.forEach { (slot, item) ->
            if (slot < inv.size)
                inv[slot] = item.formatIntoItemStack()
        }

        GuiManager.getPlayers(this)
            .forEach { player ->
                for ((index, item) in inv.withIndex()) {
                    player.openInventory.setItem(index, item)
                }
            }
    }

    override fun open(player: Player) {
        // format the items
        val inv = Bukkit.createInventory(player, totalSlots(), title)

        if (firePreBuildEvent(player)) return

        // Now apply the items
        val current = pages.getOrNull(currentPage)

        current?.items?.forEach { (slot, item) ->
            inv.setItem(slot, item.formatIntoItemStack(player))
        }

        items.forEach { (slot, item) ->
            if (slot < inv.size)
                inv.setItem(slot, item.formatIntoItemStack(player))
        }

        openIfNotCancelled(player, inv)
    }

    override fun click(e: InventoryClickEvent) {
        val currentPage = pages.getOrNull(currentPage)

        val button = items[e.rawSlot] ?: currentPage?.items?.get(e.rawSlot)

        val event = ButtonClickedEvent<IGuiButton<*>>(e.whoClicked as Player, e)
        if (button != null)
            event.button = button

        click.run(event)

        if (event.shouldContinueCallback()) {
            button?.onButtonClick(event)
        }
    }

    infix fun page(block: GuiScreen.() -> Unit) = page(null, block)
    open fun page(index: Int? = null, block: GuiScreen.() -> Unit) = apply {
        val sub = GuiScreen(Component.empty(), rows).apply(block)
        if (index == null) {
            pages.add(sub)
        } else {
            pages[index] = sub
        }
    }

    open fun navigatePreviousPage(e: ButtonClickedEvent<*>) = navigatePreviousPage()
    open fun navigateNextPage(e: ButtonClickedEvent<*>) = navigateNextPage()

    open fun navigatePreviousPage() {
        currentPage = max(0, currentPage - 1)
    }

    open fun navigateNextPage() {
        currentPage = min(pages.size - 1, currentPage + 1)
    }

    override fun destroy() {
        pages.forEach { it.destroy() }
        super.destroy()
    }
}

fun multiPageGui(title: Component, rows: Int = 6, block: GuiMultiPageScreen.() -> Unit) =
    GuiMultiPageScreen(title, rows).apply(block)