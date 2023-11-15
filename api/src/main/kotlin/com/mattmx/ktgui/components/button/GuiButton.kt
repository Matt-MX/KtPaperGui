package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.ClickCallback
import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.extensions.setEnchantments
import com.mattmx.ktgui.item.DslIBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

open class GuiButton<T>(
    material: Material = Material.STONE,
    var item: ItemStack? = null
) : IGuiButton {
    lateinit var parent: IGuiScreen
        protected set

    var clickCallback = ClickCallback<T>()
        protected set
    var dragCallback: ((InventoryDragEvent) -> Unit)? = null
        protected set
    var closeCallback: ((ButtonClickedEvent<T>) -> Unit)? = null
        protected set
    var ifTexturePackActive: ((GuiButton<T>) -> Unit)? = null
        protected set

    // todo should remove this once we have no need for it (parent has been declared)
    private var slots: ArrayList<Int>? = null

    init {
        if (item == null) item = ItemStack(material)
    }

    open fun lore(lore: MutableList<Component>.() -> Unit) : GuiButton<T> {
        item?.itemMeta?.let {
            val newLore = mutableListOf<Component>()
            lore.invoke(newLore)
            it.lore(newLore)
            item?.itemMeta = it
        }
        return this
    }

    infix fun named(name: Component) : GuiButton<T> {
        val itemMeta = item?.itemMeta
        itemMeta?.displayName(name)
        item?.itemMeta = itemMeta
        return this
    }

    override infix fun slots(slots: List<Int>) : GuiButton<T> {
        slots.forEach { slot(it) }
        return this
    }

    fun slots(vararg slots: Int) : GuiButton<T> {
        slots.forEach { slot(it) }
        return this
    }

    override infix fun slot(slot: Int) : GuiButton<T> {
        if (hasParent()) {
            if (slots == null) slots = arrayListOf()
            slots!!.add(slot)
            parent.setSlot(this, slot)
        } else {
            if (slots == null) slots = arrayListOf()
            slots!!.add(slot)
        }
        return this
    }

    fun hasParent() = this::parent.isInitialized

    override infix fun childOf(parent: IGuiScreen): GuiButton<T> {
        this.parent = parent
        this.parent.addChild(this)
        return this
    }

    override fun destroy() {

    }

    fun materialOf(materialName: String?, fallback: Material) : GuiButton<T> {
        val materialNameFormatted = materialName?.uppercase()?.replace(" ", "_")
        val mat = Material.values().firstOrNull { it.name == materialNameFormatted }
        mat?.also { material(it) } ?: material(fallback)
        return this
    }

    infix fun material(material: Material) : GuiButton<T> {
        item?.let {
            it.type = material
            return this
        }
        item = ItemStack(material)
        return this
    }

    infix fun customModelData(model: Int) : GuiButton<T> {
        val meta = item?.itemMeta ?: return this
        meta.setCustomModelData(model)
        item?.itemMeta = meta
        return this
    }

    infix fun amount(amount: Int) : GuiButton<T> {
        item?.let { it.amount = amount }
        return this
    }

    infix fun ifTexturePackActive(block: GuiButton<T>.() -> Unit) : GuiButton<T> {
        ifTexturePackActive = block
        return this
    }

    infix fun fromItemBuilder(builder: DslIBuilder) : GuiButton<T> {
        item = builder.build()
        return this
    }

    override fun getItemStack(): ItemStack? {
        return item
    }

    inline fun click(block: ClickCallback<T>.() -> Unit) : T {
        block.invoke(clickCallback)
        return this as T
    }

    fun drag(cb: InventoryDragEvent.() -> Unit) : GuiButton<T> {
        dragCallback = cb
        return this
    }

    inline fun enchant(ce: MutableMap<Enchantment, Int>.() -> Unit) : GuiButton<T> {
        val enchantments = item?.itemMeta?.enchants?.toMutableMap() ?: mutableMapOf()
        ce.invoke(enchantments)
        val itemMeta = item?.itemMeta
        itemMeta?.setEnchantments(enchantments)
        item?.itemMeta = itemMeta
        return this
    }

    override fun onButtonClick(e: ButtonClickedEvent<T>) {
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

    override fun slots(): List<Int>? {
        return slots?.toMutableList()
    }

    fun update(player: Player) : GuiButton<T> {
        val itemStack = formatIntoItemStack(player)
        // get all slots that this item exists in
        // update every slot to this new [ItemStack]
        parent.getSlots(this).forEach { slot ->
            player.openInventory.setItem(slot, itemStack)
        }
        return this
    }

    override fun copy(parent: IGuiScreen) : GuiButton<T> {
        val copy = GuiButton()
        copy.parent = parent
        copy.item = item?.clone()
        copy.clickCallback = clickCallback.clone()
        copy.closeCallback = closeCallback
        copy.ifTexturePackActive = ifTexturePackActive
        return copy
    }
}