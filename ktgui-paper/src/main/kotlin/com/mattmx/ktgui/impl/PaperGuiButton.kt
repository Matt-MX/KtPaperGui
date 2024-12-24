package com.mattmx.ktgui.impl

import com.mattmx.ktgui.button.GuiButton
import com.mattmx.ktgui.click.ClickButtonEvent
import com.mattmx.ktgui.click.ClickEventCallback
import com.mattmx.ktgui.screen.GuiScreen
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@Suppress("UNCHECKED_CAST")
open class PaperGuiButton<T : PaperGuiButton<T>>(
    var material: Material
) : GuiButton<T, Material, Enchantment, ItemStack>() {
    override val click by lazy { ClickEventCallback<T, ClickButtonEvent>(this as T) }

    inline fun <reified M : ItemMeta> consumeMeta(noinline block: (meta: M) -> Unit) =
        consumeMetaClass(M::class.java, block)

    fun <M : ItemMeta> consumeMetaClass(clazz: Class<M>, block: (meta: M) -> Unit) = apply {
        postBuild { editMeta(clazz, block) }
    } as T

    operator fun ItemFlag.unaryPlus() {
        postBuild { addItemFlags(this@unaryPlus) }
    }

    fun flags(vararg flags: ItemFlag) = apply {
        postBuild { addItemFlags(*flags) }
    } as T

    override fun <G : GuiScreen<*, *>> refresh(parent: G) {
        if (parent !is PaperGuiScreen<*>) error("Must be an instance of PaperGuiScreen<*>!")

        val slots = parent.getSlots(this)
        val itemStack = buildItem()

        val viewers = parent.getAllWatchingInstance()
            .mapKeys { it as Player }

        for ((viewer, _) in viewers) {
            for (slot in slots) {
                viewer.openInventory.setItem(slot, itemStack)
            }
        }
    }

    override fun buildItem(): ItemStack {
        val itemStack = ItemStack(material)
        itemStack.amount = amount

        itemStack.editMeta { meta ->
            meta.displayName(name)
            meta.lore(this.lore.toList())

            for ((e, l) in enchantments) {
                meta.addEnchant(e, l, true)
            }
        }

        postBuild.apply(itemStack)

        return itemStack
    }
}