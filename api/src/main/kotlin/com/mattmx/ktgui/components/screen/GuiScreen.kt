package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.ClickCallback
import com.mattmx.ktgui.components.EffectBlock
import com.mattmx.ktgui.components.button.ButtonClickedEvent
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.components.signal.GuiSignalOwner
import com.mattmx.ktgui.components.signal.Signal
import com.mattmx.ktgui.event.PreGuiBuildEvent
import com.mattmx.ktgui.event.PreGuiOpenEvent
import com.mattmx.ktgui.extensions.setOpenGui
import com.mattmx.ktgui.scheduling.isAsync
import com.mattmx.ktgui.utils.JavaCompatibility
import com.mattmx.ktgui.utils.legacy
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*
import java.util.concurrent.Future
import kotlin.collections.HashMap

open class GuiScreen(
    title: Component = Component.empty(),
    var rows: Int = 1,
    var type: InventoryType? = null
) : IGuiScreen, GuiSignalOwner<EffectBlock<GuiScreen>> {

    constructor(title: Component, rows: Int) : this(title, rows, null)
    constructor(title: Component, type: InventoryType) : this(title, 0, type)

    var title: Component = title
        set(value) {
            field = value
            GuiManager.getPlayers(this).forEach { player ->
                player.openInventory.title = value.legacy()
            }
        }

    // Can be used to identify dsl guis
    var id: String = UUID.randomUUID().toString()
    var items = hashMapOf<Int, GuiButton<*>>()
    override var currentlyProcessing: EffectBlock<GuiScreen>? = null

    var click = ClickCallback<IGuiButton<*>>()
        protected set
    protected lateinit var closeCallback: (InventoryCloseEvent) -> Unit
    protected lateinit var quitCallback: (PlayerQuitEvent) -> Unit
    protected lateinit var moveCallback: (PlayerMoveEvent) -> Unit

    protected var openCallback: ((Player) -> Unit)? = null

    /**
     * Return the default size of the inventory type
     * Or rows * 9 (= no. of slots)
     * We will make sure that rows is between 1 and 6.
     */
    override fun totalSlots(): Int {
        return if (type != null) type!!.defaultSize
        else max(min(rows, 6), 1) * 9
    }

    override fun getSlots(button: IGuiButton<*>): List<Int> {
        return items.entries
            .filter { it.value == button }
            .map { it.key }
    }

    override fun numberOfItems(): Int {
        return items.size
    }

    override fun setSlot(button: IGuiButton<*>, slot: Int): GuiScreen {
        items[slot] = button as GuiButton<*>
        return this
    }

    fun slotsUsed(): List<Int> = items.map { it.key }

    infix fun type(type: InventoryType) = apply { this.type = type }

    infix fun title(title: Component) = apply { this.title = title }

    infix fun rows(rows: Int) = apply { this.rows = rows }

    @Deprecated("Guis should no longer be formatted per player, handle that yourself.", ReplaceWith("open(player)"))
    fun openAndFormat(player: Player) = open(player)

    fun forceClose(player: Player) {
        GuiManager.clearGui(player)
        player.closeInventory()
    }

    fun refresh() {
        val inv = arrayOfNulls<ItemStack?>(totalSlots())

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
        val inv: Inventory =
            if (type != null) Bukkit.createInventory(player, type!!, title) else Bukkit.createInventory(
                player,
                totalSlots(),
                title
            )

        if (firePreBuildEvent(player)) return

        items.forEach { (slot, item) ->
            if (slot < inv.size)
                inv.setItem(slot, item.formatIntoItemStack(player))
        }

        openIfNotCancelled(player, inv)
    }

    fun firePreBuildEvent(player: Player): Boolean {
        return if (Bukkit.isPrimaryThread()) firePreBuildEventSync(player)
        else firePreBuildEventAsync(player).get()
    }

    private fun firePreBuildEventSync(player: Player): Boolean {
        val event = PreGuiBuildEvent(this, player)
        Bukkit.getPluginManager().callEvent(event)
        return event.isCancelled
    }

    private fun firePreBuildEventAsync(player: Player): Future<Boolean> {
        return Bukkit.getScheduler().callSyncMethod(GuiManager.owningPlugin) {
            firePreBuildEventSync(player)
        }
    }

    fun openIfNotCancelled(player: Player, inventory: Inventory) {
        if (!firePreGuiOpenEvent(player)) {
            if (isAsync()) {
                Bukkit.getScheduler().runTask(GuiManager.owningPlugin) { ->
                    player.openInventory(inventory)
                    player.setOpenGui(this)
                    openCallback?.invoke(player)
                }
            } else {
                player.openInventory(inventory)
                player.setOpenGui(this)
                openCallback?.invoke(player)
            }
        }
    }

    protected fun firePreGuiOpenEvent(player: Player): Boolean {
        val event = PreGuiOpenEvent(this, player)
        Bukkit.getPluginManager().callEvent(event)
        return event.isCancelled
    }

    override fun copy(): IGuiScreen {
        val screen = GuiScreen(title)
        screen.items = items.mapValues { it.value.copy(screen) }.toMutableMap() as HashMap<Int, GuiButton<*>>
        screen.type = type
        screen.rows = rows
        screen.click = click
        screen.closeCallback = closeCallback
        screen.moveCallback = moveCallback
        screen.quitCallback = quitCallback
        screen.openCallback = openCallback
        return screen
    }

    fun move(moveCallback: (PlayerMoveEvent) -> Unit) = apply { this.moveCallback = moveCallback }

    fun quit(quitCallback: (PlayerQuitEvent) -> Unit) = apply { this.quitCallback = quitCallback }

    fun close(closeCallback: (InventoryCloseEvent) -> Unit) = apply { this.closeCallback = closeCallback }

    fun open(openCallback: (Player) -> Unit) = apply { this.openCallback = openCallback }

    infix fun click(clickCallbackBuilder: (ClickCallback<*>) -> Unit) = apply {
        this.click.apply(clickCallbackBuilder)
    }

    fun addEffect(effect: EffectBlock<GuiScreen>) {
        currentlyProcessing = effect
        effect.block.invoke(this)
        currentlyProcessing = null
    }

    override fun addChild(child: IGuiButton<*>) = apply {
        child.slots()?.forEach {
            items[it] = child as GuiButton<*>
        }
    }

    override fun click(e: InventoryClickEvent) {
        val button = items[e.rawSlot]

        val event = ButtonClickedEvent<IGuiButton<*>>(e.whoClicked as Player, e)
        if (button != null)
            event.button = button

        click.run(event)

        if (event.shouldContinueCallback()) {
            button?.onButtonClick(event)
        }
    }

    override fun drag(e: InventoryDragEvent) {
        e.rawSlots.forEach {
            val button = items[it]
            button?.onButtonDrag(e)
        }
    }

    fun last(): Int {
        return totalSlots() - 1
    }

    fun middle(): Int {
        return kotlin.math.floor(totalSlots() * 0.5).toInt()
    }

    fun first(): Int {
        return 0
    }

    @JavaCompatibility
    infix fun <S> createSignal(initial: S) = Signal(initial, this)

    @JavaCompatibility
    infix fun effectBlock(block: Runnable) = apply {
        EffectBlock(this) { block.run() }.apply { addEffect(this) }
    }

    override fun close(e: InventoryCloseEvent) {
        if (::closeCallback.isInitialized)
            closeCallback(e)
    }

    override fun quit(e: PlayerQuitEvent) {
        if (::quitCallback.isInitialized)
            quitCallback(e)
    }

    override fun move(e: PlayerMoveEvent) {
        if (::moveCallback.isInitialized)
            moveCallback(e)
    }
}