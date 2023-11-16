package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.ClickCallback
import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.extensions.setEnchantments
import com.mattmx.ktgui.item.DslIBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

open class GuiButton<T : GuiButton<T>>(
    material: Material = Material.STONE,
    var item: ItemStack? = null
) : IGuiButton<T> {
    lateinit var parent: IGuiScreen
        protected set

    var clickCallback = ClickCallback<T>()
        protected set
    var dragCallback: ((InventoryDragEvent) -> Unit)? = null
        protected set
    var closeCallback: ((ButtonClickedEvent<T>) -> Unit)? = null
        protected set
    var ifTexturePackActive: ((T) -> Unit)? = null
        protected set

    // todo should remove this once we have no need for it (parent has been declared)
    private var slots: ArrayList<Int>? = null

    init {
        if (item == null) item = ItemStack(material)
    }

    open fun lore(lore: MutableList<Component>.() -> Unit) : T {
        item?.itemMeta?.let {
            val newLore = mutableListOf<Component>()
            lore.invoke(newLore)
            it.lore(newLore.map { line -> Component.empty().decoration(TextDecoration.ITALIC, false).append(line) })
            item?.itemMeta = it
        }
        return this as T
    }

    infix fun named(name: Component) : T {
        val itemMeta = item?.itemMeta
        itemMeta?.displayName(Component.empty().decoration(TextDecoration.ITALIC, false).append(name))
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

    infix fun fromItemBuilder(builder: DslIBuilder) : T {
        item = builder.build()
        return this as T
    }

    override fun getItemStack(): ItemStack? {
        return item
    }

    inline fun click(block: ClickCallback<T>.() -> Unit) : T {
        block.invoke(clickCallback)
        return this as T
    }

    fun drag(cb: InventoryDragEvent.() -> Unit) : T {
        dragCallback = cb
        return this as T
    }

    inline fun enchant(ce: MutableMap<Enchantment, Int>.() -> Unit) : T {
        val enchantments = item?.itemMeta?.enchants?.toMutableMap() ?: mutableMapOf()
        ce.invoke(enchantments)
        val itemMeta = item?.itemMeta
        itemMeta?.setEnchantments(enchantments)
        item?.itemMeta = itemMeta
        return this as T
    }

    override fun onButtonClick(e: ButtonClickedEvent<*>) {
        clickCallback.run(e)
    }

    override fun onButtonDrag(e: InventoryDragEvent) {
        dragCallback?.invoke(e)
    }

    override fun formatIntoItemStack(player: Player?): ItemStack? {
        if (player?.hasResourcePack() == true && ifTexturePackActive != null) {
            val copy = this.copy(this.parent)
            ifTexturePackActive!!(copy)
            return copy.item
        }
        return getItemStack()?.clone()
    }

    override fun slots(): List<Int> {
        return slots?.toMutableList() ?: parent.getSlots(this)
    }

    fun update(player: Player) : T {
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
        copy.clickCallback = clickCallback.clone()
        copy.closeCallback = closeCallback
        copy.ifTexturePackActive = ifTexturePackActive
        return copy as T
    }
}