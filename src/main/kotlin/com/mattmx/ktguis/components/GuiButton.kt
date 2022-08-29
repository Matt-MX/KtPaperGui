package com.mattmx.ktguis.components

import com.mattmx.ktguis.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

// todo give option to have different click events for different types of click
class GuiButton : IGuiButton, Formattable {
    var parent: IGuiScreen? = null

    var item : ItemStack? = null

    private var click = ClickEvents()
    var notClicked: ((InventoryClickEvent) -> Unit)? = null
    var close: ((InventoryCloseEvent) -> Unit)? = null

    infix fun lore(l: (MutableList<String>) -> Unit) : GuiButton {
        item?.itemMeta?.let {
            if (it.lore == null) it.lore = mutableListOf()
            l.invoke(it.lore!!)
        }
        return this
    }

    infix fun named(name: String) : GuiButton {
        item?.itemMeta?.setDisplayName(name)
        return this
    }

    infix fun slot(slot: Int) : GuiButton {
        parent?.setSlot(this, slot)
        return this
    }

    infix fun childOf(screen: IGuiScreen): GuiButton {
        this.parent = screen
        parent?.addChild(this)
        return this
    }

    infix fun materialOf(string: String) : GuiButton {
        return materialOf(string)
    }

    fun materialOf(string: String, fallback: Material = Material.STONE) : GuiButton {
        val mat = Material.values().firstOrNull { it.name.lowercase() == string.lowercase().replace(" ", "_") }
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

    infix fun amount(amount: Int) : GuiButton {
        item?.let { it.amount = amount }
        return this
    }

    infix fun ofBuilder(builder: ItemBuilder) : GuiButton {
        item = builder.make()
        return this
    }

    override fun getItemStack(): ItemStack? {
        return null
    }

    fun click(ce: (ClickEvents) -> Unit) : GuiButton {
        ce.invoke(click)
        return this
    }

    fun enchant(ce: (MutableMap<Enchantment, Int>) -> Unit) : GuiButton {
        item?.let { ce.invoke(it.enchantments) }
        return this
    }

    override fun thisClicked(e: InventoryClickEvent) {
        click.accept(e)
    }

    override fun notClicked(e: InventoryClickEvent) {
        notClicked?.let { notClicked!!.invoke(e) }
    }

    override fun format(p: Player) {
        // todo do some papi stuff
    }
}