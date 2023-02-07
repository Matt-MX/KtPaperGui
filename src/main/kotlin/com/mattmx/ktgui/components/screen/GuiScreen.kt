package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.ClickEvents
import com.mattmx.ktgui.components.Formattable
import com.mattmx.ktgui.components.button.ButtonClickedEvent
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.event.AsyncPreGuiOpenEvent
import com.mattmx.ktgui.event.PreGuiBuildEvent
import com.mattmx.ktgui.event.PreGuiOpenEvent
import com.mattmx.ktgui.extensions.color
import com.mattmx.ktgui.extensions.setOpenGui
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.concurrent.Future

open class GuiScreen(
    var title: String = "null",
    var rows: Int = 1,
    var type: InventoryType? = null,
) : IGuiScreen, Formattable {
    var items = hashMapOf<Int, IGuiButton>()

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
        return items.entries
            .filter { it.value == button }
            .map { it.key }
    }

    override fun size(): Int {
        return items.size
    }

    override fun setSlot(button: IGuiButton, slot: Int): GuiScreen {
        items[slot] = button
        return this
    }

    fun slotsUsed() : List<Int> {
        return items.map { it.key }
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

        if (firePreBuildEvent(player)) return

        items.forEach { (slot, item) ->
            if (slot < inv.size)
                inv.setItem(slot, item.formatIntoItemStack(player))
        }

        openIfNotCancelled(player, inv)
    }

    fun firePreBuildEvent(player: Player) : Boolean {
        return if (Bukkit.isPrimaryThread()) firePreBuildEventSync(player)
        else firePreBuildEventAsync(player).get()
    }

    private fun firePreBuildEventSync(player: Player) : Boolean {
        val event = PreGuiBuildEvent(this, player)
        Bukkit.getPluginManager().callEvent(event)
        return event.isCancelled
    }

    private fun firePreBuildEventAsync(player: Player) : Future<Boolean> {
        return Bukkit.getScheduler().callSyncMethod(GuiManager.owningPlugin) {
            firePreBuildEventSync(player)
        }
    }

    fun openIfNotCancelled(player: Player, inventory: Inventory) {
        if (Bukkit.isPrimaryThread()) {
            println("primary thread, check event")
            if (!firePreGuiOpenEvent(player)) {
                println("event was not cancelled")
                player.openInventory(inventory)
                player.setOpenGui(this)
                open?.invoke(player)
            }
        } else {
            if (!firePreGuiOpenEventAsync(player)) {
                // Must open inventory sync
                Bukkit.getScheduler().runTask(GuiManager.owningPlugin) { _ ->
                    player.openInventory(inventory)
                    player.setOpenGui(this)
                    open?.invoke(player)
                }
            }
        }
    }

    fun firePreGuiOpenEvent(player: Player) : Boolean {
        val event = PreGuiOpenEvent(this, player)
        Bukkit.getPluginManager().callEvent(event)
        return event.isCancelled
    }

    fun firePreGuiOpenEventAsync(player: Player) : Boolean {
        val event = AsyncPreGuiOpenEvent(this, player)
        Bukkit.getPluginManager().callEvent(event)
        return event.isCancelled
    }

    override fun copy(): IGuiScreen {
        val screen = GuiScreen(title)
        screen.items = items.mapValues { it.value.copy(screen) }.toMutableMap() as HashMap<Int, IGuiButton>
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
        child.slots()?.forEach {
            items[it] = child
        }
    }

    override fun click(e: InventoryClickEvent) {
        val button = items[e.rawSlot]
        // If dev wants to handle click calls globally
        click?.let {
            it.accept(ButtonClickedEvent(e.whoClicked as Player, e, button))
            return
        }
        button?.thisClicked(ButtonClickedEvent(e.whoClicked as Player, e, button))
    }

    override fun drag(e: InventoryDragEvent) {
        e.rawSlots.forEach {
            val button = items[it]
            button?.thisDragged(e)
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