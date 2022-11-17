package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.KotlinBukkitGui
import com.mattmx.ktgui.components.ClickEvents
import com.mattmx.ktgui.components.Formattable
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.extensions.color
import com.mattmx.ktgui.extensions.setOpenGui
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
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
    protected var quit: ((PlayerQuitEvent) -> Unit)? = null
    protected var move: ((PlayerMoveEvent) -> Unit)? = null

    protected var open: ((Player) -> Unit)? = null

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
//        println("${button.javaClass.name} $pointerIndex <- $slot ${items.size - 1}")
        return this
    }

    fun slotsUsed() : List<Int> {
        return pointers.keys.toMutableList()
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
        title = title.color(player)
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
            } catch (e: IndexOutOfBoundsException) {
                // fixme
                println("GUI with title $title had an issue when building. Slot $slot points to an invalid item!")
                e.printStackTrace()
            }
        }
        player.openInventory(inv)
        player.setOpenGui(this)
        open?.invoke(player)
        // open gui for player
//        pointers.forEach { (slot, index) ->
//            println("slot: $slot index: $index -> ${items[index].getItemStack()?.type}")
//        }
    }

    override fun copy(): IGuiScreen {
        val screen = GuiScreen(title)
        screen.items = items.map { it.copy(screen) }.toMutableList() as ArrayList<IGuiButton>
        screen.pointers = pointers.toMutableMap() as HashMap<Int, Int>
        screen.type = type
        screen.rows = rows
        screen.click = click
        screen.move = move
        screen.close = close
        screen.quit = quit
        screen.open = open
        return screen
    }

    override fun copyAndFormat(player: Player): GuiScreen {
        val screen = copy() as GuiScreen
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

    fun close(ic: (InventoryCloseEvent) -> Unit): GuiScreen {
        close = ic
        return this
    }

    fun open(oc: (Player) -> Unit) : GuiScreen {
        open = oc
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

    fun last() : Int {
        return totalSlots() - 1
    }

    fun middle() : Int {
        return kotlin.math.floor(totalSlots() * 0.5).toInt()
    }

    fun first() : Int {
        return 0
    }

    override fun close(e: InventoryCloseEvent) {
        close?.invoke(e)
    }

    override fun quit(e: PlayerQuitEvent) {
        quit?.invoke(e)
    }

    override fun move(e: PlayerMoveEvent) {
        move?.invoke(e)
    }

    override fun format(p: Player) {
        title = title.color(p)
    }

    companion object {
        operator fun invoke() = GuiButton()
    }
}