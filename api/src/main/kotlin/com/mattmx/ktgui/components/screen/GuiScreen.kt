package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.ClickCallback
import com.mattmx.ktgui.components.EffectBlock
import com.mattmx.ktgui.components.RefreshBlock
import com.mattmx.ktgui.components.button.ButtonClickedEvent
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.components.signal.GuiSignalOwner
import com.mattmx.ktgui.components.signal.Signal
import com.mattmx.ktgui.event.ContinuousEventCallback
import com.mattmx.ktgui.event.EventCallback
import com.mattmx.ktgui.event.PreGuiBuildEvent
import com.mattmx.ktgui.event.PreGuiOpenEvent
import com.mattmx.ktgui.extensions.setOpenGui
import com.mattmx.ktgui.scheduling.TaskTracker
import com.mattmx.ktgui.scheduling.isAsync
import com.mattmx.ktgui.utils.Invokable
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
import java.util.function.Consumer

open class GuiScreen(
    title: Component = Component.empty(),
    var rows: Int = 1,
    var type: InventoryType? = null
) : IGuiScreen, GuiSignalOwner<EffectBlock<GuiScreen>>, Invokable<GuiScreen> {
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

    // todo should be probably moved to a session context
    private val taskTracker = TaskTracker()
    override var currentlyProcessing: EffectBlock<GuiScreen>? = null

    var click = ClickCallback<IGuiButton<*>>()
        protected set
    var close = EventCallback<InventoryCloseEvent>()
        protected set
    var quit = EventCallback<PlayerQuitEvent>()
        protected set
    var playerMove = EventCallback<PlayerMoveEvent>()
        protected set
    var open = EventCallback<Player>()
        protected set

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

    override fun clearSlot(vararg slot: Int) {
        for (s in slot) {
            items.remove(s)
        }
    }

    open fun slotsUsed(): List<Int> = items.map { it.key }

    open fun findButton(id: String) = findButtons(id).firstOrNull()

    open fun findButtons(id: String) = items.values.filter { it.id == id }

    open fun <T : GuiButton<T>> findButton(id: String, block: T.() -> Unit) = (findButton(id) as T?)?.apply(block)

    open fun <T : GuiButton<T>> findButtons(id: String, block: T.() -> Unit) = findButtons(id).map { (it as T).apply(block) }

    open infix fun type(type: InventoryType) = apply { this.type = type }

    open infix fun title(title: Component) = apply { this.title = title }

    open infix fun rows(rows: Int) = apply { this.rows = rows }

    @Deprecated("Guis should no longer be formatted per player, handle that yourself.", ReplaceWith("open(player)"))
    fun openAndFormat(player: Player) = open(player)

    open fun forceClose(player: Player) {
        GuiManager.clearGui(player)
        player.closeInventory()
    }

    open fun refresh() {
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
                    open(player)
                }
            } else {
                player.openInventory(inventory)
                player.setOpenGui(this)
                open(player)
            }
        }
    }

    open protected fun firePreGuiOpenEvent(player: Player): Boolean {
        val event = PreGuiOpenEvent(this, player)
        Bukkit.getPluginManager().callEvent(event)
        return event.isCancelled
    }

    override fun copy(): IGuiScreen {
        val screen = GuiScreen(title)
        screen.items = items.mapValues { it.value.copy(screen) }.toMutableMap() as HashMap<Int, GuiButton<*>>
        screen.type = type
        screen.rows = rows
        screen.click = click.clone()
        screen.close = close.clone()
        screen.playerMove = playerMove.clone()
        screen.quit = quit.clone()
        screen.open = open.clone()
        return screen
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

    fun addRefreshBlock(block: RefreshBlock<GuiScreen>) {
        open {
            taskTracker.runAsyncRepeat(0, block.repeat) {
                // todo only change slots modified!
                block.block.invoke(this@GuiScreen)
                refresh()
            }
        }
    }

    @JavaCompatibility
    infix fun <S> createSignal(initial: S) = Signal(initial, this)

    @JavaCompatibility
    infix fun effectBlock(block: Runnable) = apply {
        EffectBlock(this) { block.run() }.apply { addEffect(this) }
    }

    @JavaCompatibility
    fun refreshBlock(repeat: Long, block: Runnable) = apply {
        RefreshBlock(repeat, this) { block.run() }.apply { addRefreshBlock(this) }
    }

    @JavaCompatibility
    open infix fun onOpen(callback: Consumer<Player>) = apply {
        this.open { callback.accept(this) }
    }
    @JavaCompatibility
    open infix fun onClose(callback: Consumer<InventoryCloseEvent>) = apply {
        this.close { callback.accept(this) }
    }

    @JavaCompatibility
    open infix fun onQuit(callback: Consumer<PlayerQuitEvent>) = apply {
        this.quit { callback.accept(this) }
    }

    @JavaCompatibility
    open infix fun onMove(callback: Consumer<PlayerMoveEvent>) = apply {
        this.playerMove { callback.accept(this) }
    }

    override fun close(e: InventoryCloseEvent) {
        close.invoke(e)
    }
    override fun quit(e: PlayerQuitEvent) {
        quit.invoke(e)
    }

    override fun move(e: PlayerMoveEvent) {
        playerMove.invoke(e)
    }

    override fun destroy() {
        taskTracker.cancelAll()
    }
}