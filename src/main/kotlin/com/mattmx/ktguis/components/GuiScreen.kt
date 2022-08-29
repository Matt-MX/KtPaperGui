package com.mattmx.ktguis.components

import com.mattmx.ktguis.extensions.setOpenGui
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

open class GuiScreen(
    var title: String = "null",
    var rows: Int = 1,
    var type: InventoryType? = null,
) : IGuiScreen, Formattable {
    val items = arrayListOf<IGuiButton>()
    // slot : items[index]
    val pointers = hashMapOf<Int, Int>()

    var click : ClickEvents? = null
    var close : ((InventoryCloseEvent) -> Unit)? = null
    var chat : ((AsyncPlayerChatEvent) -> Unit)? = null
    var quit : ((PlayerQuitEvent) -> Unit)? = null
    var move : ((PlayerMoveEvent) -> Unit)? = null

    override fun size(): Int {
        return items.size
    }

    override fun setSlot(button: IGuiButton, slot: Int) : GuiScreen {
        if (!items.contains(button)) items.add(button)
        pointers[slot] = items.size - 1
        return this
    }

    infix fun type(type: InventoryType) : GuiScreen {
        this.type = type
        return this
    }

    infix fun title(title: String) : GuiScreen {
        this.title = title
        return this
    }

    infix fun rows(rows: Int) : GuiScreen {
        this.rows = rows
        return this
    }

    override fun createAndOpen(player: Player) : IGuiScreen {
        val gui = create(player)
        gui.open(player)
        return gui
    }

    override fun open(player: Player) {
        // open gui for player
        player.setOpenGui(this)
    }

    override fun create(player: Player): IGuiScreen {
        // create copy and format for this player
        // return new version
        return this
    }

    fun move(me: (PlayerMoveEvent) -> Unit) : GuiScreen {
        move = me
        return this
    }

    fun quit(qe: (PlayerQuitEvent) -> Unit) : GuiScreen {
        quit = qe
        return this
    }

    fun chat(ch: (AsyncPlayerChatEvent) -> Unit) : GuiScreen {
        chat = ch
        return this
    }

    fun close(ic: (InventoryCloseEvent) -> Unit) : GuiScreen {
        close = ic
        return this
    }

    infix fun click(ce: (ClickEvents) -> Unit) : GuiScreen {
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
        val index = pointers[e.slot]
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
        // todo: do some papi stuff
    }
}