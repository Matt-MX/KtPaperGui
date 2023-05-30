package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.ClickEvents
import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.extensions.format
import com.mattmx.ktgui.extensions.setEnchantments
import com.mattmx.ktgui.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

open class GuiButton(
    material: Material = Material.STONE,
    var item: ItemStack? = null
) : IGuiButton {
    protected var parent: IGuiScreen? = null

    var click = ClickEvents()
    var drag: ((InventoryDragEvent) -> Unit)? = null
    var close: ((ButtonClickedEvent) -> Unit)? = null
    var texturePack: ((GuiButton) -> Unit)? = null

    protected var slots: ArrayList<Int>? = null

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
        parent?.also {
            if (slots == null) slots = arrayListOf()
            slots!!.add(slot)
            it.setSlot(this, slot)
        } ?: run {
            if (slots == null) slots = arrayListOf()
            slots!!.add(slot)
        }
        return this
    }

    override infix fun childOf(screen: IGuiScreen): GuiButton {
        this.parent = screen
        parent?.addChild(this)
        return this
    }

    infix fun materialOf(string: String?) : GuiButton {
        return materialOf(string, Material.STONE)
    }

    fun materialOf(string: String?, fallback: Material = Material.STONE) : GuiButton {
        val mat = Material.values().firstOrNull { it.name.lowercase() == string?.lowercase()?.replace(" ", "_") }
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

    infix fun texturePackActive(guiButton: GuiButton.() -> Unit) : GuiButton {
        texturePack = guiButton
        return this
    }

    infix fun amount(amount: Int) : GuiButton {
        item?.let { it.amount = amount }
        return this
    }

    infix fun ofBuilder(builder: ItemBuilder) : GuiButton {
        item = builder.make()
        return this
    }

    override fun getItemStack(): ItemStack? {
        return item
    }

    inline fun click(ce: ClickEvents.() -> Unit) : GuiButton {
        ce.invoke(click)
        return this
    }

    fun drag(cb: InventoryDragEvent.() -> Unit) : GuiButton {
        this.drag = cb
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

    override fun thisClicked(e: ButtonClickedEvent) {
        click.accept(e)
    }

    override fun thisDragged(e: InventoryDragEvent) {
        drag?.invoke(e)
    }

    override fun formatIntoItemStack(player: Player?) : ItemStack? {
        // format itemstack and return
        if (player?.hasResourcePack() == true && texturePack != null) {
            val copy = this.copy(this.parent!!)
            texturePack!!(copy)
            val item = copy.item
            item?.format(player)
            return item
        }
        val i = getItemStack()?.clone()
        i?.format(player)
        return i
    }

    override fun slots(): List<Int>? {
        return slots?.toMutableList()
    }

    fun update(player: Player) : GuiButton {
         val istack = formatIntoItemStack(player)
        // get all slots that this item exists in
        // update every slot to this new itemstack
        parent?.getSlots(this)?.forEach { slot ->
            player.openInventory.setItem(slot, istack)
        }
        return this
    }

    override fun copy(parent: IGuiScreen) : GuiButton {
        val copy = GuiButton()
        copy.parent = parent
        copy.item = item?.clone()
        copy.click = click.copy()
        copy.close = close
        copy.texturePack = texturePack
        return copy
    }
}