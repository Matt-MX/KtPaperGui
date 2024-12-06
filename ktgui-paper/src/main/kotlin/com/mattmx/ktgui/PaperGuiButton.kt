package com.mattmx.ktgui

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@Suppress("UNCHECKED_CAST")
open class PaperGuiButton<T : PaperGuiButton<T>>(
    var material: Material
) : GuiButton<T, Material, Enchantment, ItemStack>() {

    inline fun <reified M : ItemMeta> consumeMeta(noinline block: M.() -> Unit) = apply {
        postBuild { editMeta(M::class.java, block) }
    } as T

    override fun buildItem(): ItemStack {
        val itemStack = ItemStack(material)
        itemStack.amount = amount

        itemStack.editMeta { meta ->
            meta.displayName(name.orElse(null))
            meta.lore(this.lore.toList())

            for ((e, l) in enchantments) {
                meta.addEnchant(e, l, true)
            }
        }

        postBuild.apply(itemStack)

        return itemStack
    }
}