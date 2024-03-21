package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.ClickCallback
import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.extensions.setEnchantments
import com.mattmx.ktgui.item.DslIBuilder
import com.mattmx.ktgui.utils.JavaCompatibility
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack
import java.lang.StringBuilder
import java.util.StringJoiner
import java.util.function.Consumer

open class GuiButton<T: GuiButton<T>>(
    material: Material = Material.STONE,
    var item: ItemStack? = ItemStack(material)
) : IGuiButton<T> {

    constructor(material: Material) : this(material, null)
    constructor(item: ItemStack) : this(item.type, item)

    lateinit var parent: IGuiScreen
        protected set

    var click = ClickCallback<T>()
        protected set
    var dragCallback: ((InventoryDragEvent) -> Unit)? = null
        protected set
    var closeCallback: ((ButtonClickedEvent<T>) -> Unit)? = null
        protected set
    var ifTexturePackActive: ((T) -> Unit)? = null
        protected set
    var postBuild: ((ItemStack) -> Unit)? = null
        protected set

    // todo should remove this once we have no need for it (parent has been declared)
    private var slots: ArrayList<Int>? = null

    init {
        if (item == null) item = ItemStack(material)
    }

    open fun lore(block: MutableList<Component>.() -> Unit) : T {
        item?.editMeta {
            val newLore = mutableListOf<Component>().apply(block)
            it.lore(newLore.map { line -> Component.empty().decoration(TextDecoration.ITALIC, false).append(line) })
        }
        return this as T
    }

    @JavaCompatibility
    fun lore(vararg lines: Component) : T {
        item?.editMeta {
            it.lore(lines.map { line -> Component.empty().decoration(TextDecoration.ITALIC, false).append(line) })
        }
        return this as T
    }

    infix fun named(name: Component?) : T {
        val itemMeta = item?.itemMeta
        if (name != null)
            itemMeta?.displayName(Component.empty().decoration(TextDecoration.ITALIC, false).append(name))
        else itemMeta?.displayName(null)
        item?.itemMeta = itemMeta
        return this as T
    }

    override infix fun slots(slots: List<Int>) : T {
        slots.forEach { slot(it) }
        return this as T
    }

    fun slots(vararg slots: Int) : T {
        slots.forEach { slot(it) }
        return this as T
    }

    override infix fun slot(slot: Int) : T {
        if (hasParent()) {
            if (slots == null) slots = arrayListOf()
            slots!!.add(slot)
            parent.setSlot(this, slot)
        } else {
            if (slots == null) slots = arrayListOf()
            slots!!.add(slot)
        }
        return this as T
    }

    fun hasParent() = this::parent.isInitialized

    override infix fun childOf(parent: IGuiScreen): T {
        this.parent = parent
        this.parent.addChild(this)
        return this as T
    }

    override fun destroy() {

    }

    fun materialOf(materialName: String?, fallback: Material) : T {
        val materialNameFormatted = materialName?.uppercase()?.replace(" ", "_")
        val mat = Material.values().firstOrNull { it.name == materialNameFormatted }
        mat?.also { material(it) } ?: material(fallback)
        return this as T
    }

    infix fun material(material: Material) : T {
        item?.let {
            it.type = material
            return this as T
        }
        item = ItemStack(material)
        return this as T
    }

    infix fun customModelData(model: Int) : T {
        val meta = item?.itemMeta ?: return this as T
        meta.setCustomModelData(model)
        item?.itemMeta = meta
        return this as T
    }

    infix fun amount(amount: Int) : T {
        item?.let { it.amount = amount }
        return this as T
    }

    infix fun ifTexturePackActive(block: GuiButton<T>.() -> Unit) : T {
        ifTexturePackActive = block
        return this as T
    }

    /**
     * Code runs after the [GuiButton] has built an [ItemStack]
     */
    infix fun postBuild(block: ItemStack.() -> Unit) = apply {
        postBuild = block
    } as T

    infix fun fromItemBuilder(builder: DslIBuilder) : T {
        item = builder.build()
        return this as T
    }

    override fun getItemStack(): ItemStack? {
        return item
    }

    @JavaCompatibility
    fun click(type: ClickType, block: Consumer<ButtonClickedEvent<T>>) : T {
        click.handleClicks({ block.accept(this) }, type)
        return this as T
    }

    fun test(b: Consumer<StringBuilder>) {

    }

    inline infix fun click(block: ClickCallback<T>.() -> Unit) : T {
        block.invoke(click)
        return this as T
    }

    infix fun drag(cb: InventoryDragEvent.() -> Unit) : T {
        dragCallback = cb
        return this as T
    }

    inline infix fun enchant(ce: MutableMap<Enchantment, Int>.() -> Unit) : T {
        val enchantments = item?.itemMeta?.enchants?.toMutableMap() ?: mutableMapOf()
        ce.invoke(enchantments)
        val itemMeta = item?.itemMeta
        itemMeta?.setEnchantments(enchantments)
        item?.itemMeta = itemMeta
        return this as T
    }

    override fun onButtonClick(e: ButtonClickedEvent<*>) {
        click.run(e)
    }

    override fun onButtonDrag(e: InventoryDragEvent) {
        dragCallback?.invoke(e)
    }

    override fun formatIntoItemStack(player: Player?): ItemStack? {
        if (player?.hasResourcePack() == true && ifTexturePackActive != null) {
            val copy = this.copy(this.parent)
            ifTexturePackActive!!(copy)
            return copy.item.apply { this?.let { postBuild?.invoke(it) } }
        }
        return getItemStack()?.clone().apply { this?.let { postBuild?.invoke(it) } }
    }

    override fun slots(): List<Int> {
        return slots?.toMutableList() ?: parent.getSlots(this)
    }

    infix fun update(player: Player) : T {
        val itemStack = formatIntoItemStack(player)
        // get all slots that this item exists in
        // update every slot to this new [ItemStack]
        parent.getSlots(this).forEach { slot ->
            player.openInventory.setItem(slot, itemStack)
        }
        return this as T
    }

    /**
     * Updates the item for all players with the parent GUI open.
     */
    fun update() {
        val itemStack = formatIntoItemStack()
        val players = GuiManager.getPlayers(parent)
        players.forEach { player ->
            this.slots().forEach { slot ->
                player.openInventory.setItem(slot, itemStack)
            }
        }
    }

    override fun copy(parent: IGuiScreen) : T {
        val copy = GuiButton<T>()
        copy.parent = parent
        copy.item = item?.clone()
        copy.click = click.clone()
        copy.closeCallback = closeCallback
        copy.ifTexturePackActive = ifTexturePackActive
        return copy as T
    }
}