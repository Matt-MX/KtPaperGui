package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.KotlinBukkitGui
import com.mattmx.ktgui.components.ClickEvents
import com.mattmx.ktgui.components.Formattable
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.extensions.setOpenGui
import com.mattmx.ktgui.utils.Chat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import java.lang.IndexOutOfBoundsException
import java.lang.Integer.max
import java.lang.Integer.min

open class GuiScreen(
    var title: String = "null",
    var rows: Int = 1,
    var type: InventoryType? = null,
) : IGuiScreen, Formattable {
    var items = arrayListOf<IGuiButton>()

    // slot : items[index]
    var pointers = hashMapOf<Int, Int>()

    var click: ClickEvents? = null
    var close: ((InventoryCloseEvent) -> Unit)? = null
    var chat: ((AsyncPlayerChatEvent) -> Unit)? = null
    var quit: ((PlayerQuitEvent) -> Unit)? = null
    var move: ((PlayerMoveEvent) -> Unit)? = null

    var open: ((InventoryOpenEvent, Player) -> Unit)? = null

    override fun onOpen(event: InventoryOpenEvent, player: Player) {
        open?.invoke(event, player)
    }

    /**
     * Return the default size of the inventory type
     * Or rows * 9 (= no. of slots)
     * We will make sure that rows is between 1 and 6.
     */
    override fun totalSlots(): Int {
        return if (type != null) type!!.defaultSize
        else max(min(rows, 6), 1) * 9
    }

    override fun getSlots(button: IGuiButton): List<Int> {
        val index = items.indexOf(button)
        return pointers.filter { it.value == index }.map { it.key }
    }

    override fun size(): Int {
        return pointers.size
    }

    override fun setSlot(button: IGuiButton, slot: Int): GuiScreen {
        val pointerIndex: Int = if (!items.contains(button)) {
            items.add(button)
            items.size - 1
        } else items.indexOf(button)
        pointers[slot] = pointerIndex
        return this
    }

    infix fun type(type: InventoryType): GuiScreen {
        this.type = type
        return this
    }

    infix fun title(title: String): GuiScreen {
        this.title = title
        return this
    }

    infix fun rows(rows: Int): GuiScreen {
        this.rows = rows
        return this
    }

    override fun createCopyAndOpen(player: Player): IGuiScreen {
        val gui = copyAndFormat(player)
        gui.open(player)
        return gui
    }

    fun openAndFormat(player: Player) {
        title = Chat.format(title, player)
        open(player)
    }

    fun forceClose(player: Player) {
        GuiManager.players.remove(player.uniqueId)
        player.closeInventory()
    }
    override fun open(player: Player) {
        // format the items
        val inv: Inventory = if (type != null) Bukkit.createInventory(player, type!!, title) else Bukkit.createInventory(player, totalSlots(), title)
        pointers.forEach { (slot, index) ->
            try {
                val item = items[index]
                inv.setItem(slot, item.formatIntoItemStack(player))
            } catch (e: IndexOutOfBoundsException) { KotlinBukkitGui.log.warning("GUI with title $title had an issue when building. Slot $slot points to an invalid item!") }
        }
        player.openInventory(inv)?.let {
            onOpen(InventoryOpenEvent(it), player)
        }
        // open gui for player
        player.setOpenGui(this)
    }

    override fun copy(): GuiScreen {
        val screen = GuiScreen(title)
        // todo: Fix issue with items not copying correctly
        screen.items = items.toMutableList() as ArrayList<IGuiButton>
        screen.pointers = pointers.toMutableMap() as HashMap<Int, Int>
        screen.type = type
        screen.rows = rows
        screen.click = click
        screen.move = move
        screen.chat = chat
        screen.close = close
        screen.quit = quit
        return screen
    }

    override fun copyAndFormat(player: Player): GuiScreen {
        val screen = copy()
        screen.format(player)
        return screen
    }

    fun move(me: (PlayerMoveEvent) -> Unit): GuiScreen {
        move = me
        return this
    }

    fun quit(qe: (PlayerQuitEvent) -> Unit): GuiScreen {
        quit = qe
        return this
    }

    fun chat(ch: (AsyncPlayerChatEvent) -> Unit): GuiScreen {
        chat = ch
        return this
    }

    fun close(ic: (InventoryCloseEvent) -> Unit): GuiScreen {
        close = ic
        return this
    }

    infix fun click(ce: (ClickEvents) -> Unit): GuiScreen {
        this.click = ClickEvents()
        ce.invoke(this.click!!)
        return this
    }

    override fun addChild(child: IGuiButton) {
        items.add(child)
    }

    override fun click(e: InventoryClickEvent) {
        // If dev wants to handle click calls globally
        click?.let {
            it.accept(e)
            return
        }
        val index = pointers[e.rawSlot]
        index?.let {
            val item = items[it]
            item.thisClicked(e)
        }
    }

    override fun close(e: InventoryCloseEvent) {
        close?.invoke(e)
    }

    override fun quit(e: PlayerQuitEvent) {
        quit?.invoke(e)
    }

    override fun chat(e: AsyncPlayerChatEvent) {
        chat?.invoke(e)
    }

    override fun move(e: PlayerMoveEvent) {
        move?.invoke(e)
    }

    override fun format(p: Player) {
        title = Chat.format(title, p)
    }
}