package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.ClickCallback
import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.extensions.format
import com.mattmx.ktgui.extensions.setEnchantments
import com.mattmx.ktgui.item.DslIBuilder
import com.mattmx.ktgui.utils.color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

open class GuiButton(
    material: Material = Material.STONE,
    var item: ItemStack? = null
) : IGuiButton {
    lateinit var parent: IGuiScreen
        protected set

    var clickCallback = ClickCallback()
        protected set
    var dragCallback: ((InventoryDragEvent) -> Unit)? = null
        protected set
    var closeCallback: ((ButtonClickedEvent) -> Unit)? = null
        protected set
    var ifTexturePackActive: ((GuiButton) -> Unit)? = null
        protected set
    private var format: (String, Player?) -> String = { s, p -> color(s, p) }

    // todo should remove this once we have no need for it (parent has been declared)
    private var slots: ArrayList<Int>? = null

    init {
        if (item == null) item = ItemStack(material)
    }

    inline fun lore(lore: MutableList<String>.() -> Unit) : GuiButton {
        item?.itemMeta?.let {
            val newLores = mutableListOf<String>()
            lore.invoke(newLores)
            it.lore = newLores
            item?.itemMeta = it
        }
        return this
    }

    infix fun named(name: String?) : GuiButton {
        if (name == null) return this
        val imeta = item?.itemMeta
        imeta?.setDisplayName(name)
        item?.itemMeta = imeta
        return this
    }

    override infix fun slots(slots: List<Int>) : GuiButton {
        slots.forEach { slot(it) }
        return this
    }

    fun slots(vararg slots: Int) : GuiButton {
        slots.forEach { slot(it) }
        return this
    }

    override infix fun slot(slot: Int) : GuiButton {
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

    override infix fun childOf(parent: IGuiScreen): GuiButton {
        this.parent = parent
        this.parent.addChild(this)
        return this
    }

    override fun destroy() {

    }

    fun materialOf(materialName: String?, fallback: Material) : GuiButton {
        val materialNameFormatted = materialName?.uppercase()?.replace(" ", "_")
        val mat = Material.values().firstOrNull { it.name == materialNameFormatted }
        mat?.also { material(it) } ?: material(fallback)
        return this
    }

    infix fun material(material: Material) : GuiButton {
        item?.let {
            it.type = material
            return this
        }
        item = ItemStack(material)
        return this
    }

    infix fun customModelData(model: Int) : GuiButton {
        val meta = item?.itemMeta ?: return this
        meta.setCustomModelData(model)
        item?.itemMeta = meta
        return this
    }

    infix fun amount(amount: Int) : GuiButton {
        item?.let { it.amount = amount }
        return this
    }

    infix fun ifTexturePackActive(block: GuiButton.() -> Unit) : GuiButton {
        ifTexturePackActive = block
        return this
    }

    infix fun fromItemBuilder(builder: DslIBuilder) : GuiButton {
        item = builder.build()
        return this
    }

    override fun getItemStack(): ItemStack? {
        return item
    }

    inline fun click(block: ClickCallback.() -> Unit) : GuiButton {
        block.invoke(clickCallback)
        return this
    }

    fun drag(cb: InventoryDragEvent.() -> Unit) : GuiButton {
        dragCallback = cb
        return this
    }

    inline fun enchant(ce: MutableMap<Enchantment, Int>.() -> Unit) : GuiButton {
        val enchantments = item?.itemMeta?.enchants?.toMutableMap() ?: mutableMapOf()
        ce.invoke(enchantments)
        val imeta = item?.itemMeta
        imeta?.setEnchantments(enchantments)
        item?.itemMeta = imeta
        return this
    }

    override fun onButtonClick(event: ButtonClickedEvent) {
        clickCallback.run(event)
    }

    override fun onButtonDrag(e: InventoryDragEvent) {
        dragCallback?.invoke(e)
    }

    override fun formatIntoItemStack(player: Player?) : ItemStack? {
        // format itemstack and return
        if (player?.hasResourcePack() == true && ifTexturePackActive != null) {
            val copy = this.copy(this.parent)
            ifTexturePackActive!!(copy)
            val item = copy.item
            item?.format(player, format)
            return item
        }
        val i = getItemStack()?.clone()
        i?.format(player, format)
        return i
    }

    override fun slots(): List<Int>? {
        return slots?.toMutableList()
    }

    fun update(player: Player) : GuiButton {
        val itemStack = formatIntoItemStack(player)
        // get all slots that this item exists in
        // update every slot to this new itemstack
        parent.getSlots(this).forEach { slot ->
            player.openInventory.setItem(slot, itemStack)
        }
        return this
    }

    override fun copy(parent: IGuiScreen) : GuiButton {
        val copy = GuiButton()
        copy.parent = parent
        copy.item = item?.clone()
        copy.clickCallback = clickCallback.clone()
        copy.closeCallback = closeCallback
        copy.ifTexturePackActive = ifTexturePackActive
        return copy
    }
}