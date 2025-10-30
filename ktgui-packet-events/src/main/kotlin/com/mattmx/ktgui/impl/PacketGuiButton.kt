package com.mattmx.ktgui.impl

import com.github.retrooper.packetevents.protocol.component.ComponentType
import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType
import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.mattmx.ktgui.button.GuiButton
import com.mattmx.ktgui.click.ClickEventCallback
import com.mattmx.ktgui.event.ParentEventCallback
import com.mattmx.ktgui.event.PlayerClickButtonEvent
import com.mattmx.ktgui.screen.GuiScreen
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

@Suppress("UNCHECKED_CAST")
open class PacketGuiButton<T : PacketGuiButton<T>>(
    var base: ItemStack
) : GuiButton<T, ItemType, EnchantmentType, ItemStack>() {
    var type: ItemType
        get() = base.type
        set(value) {
            base = ItemStack.builder()
                .components(base.components)
                .amount(base.amount)
                .nbt(base.nbt)
                .type(value)
                .build()

            onChanges.apply(this as T)
        }
    val preBuild by lazy { ParentEventCallback<ItemStack, T>(this as T) }
    override val click by lazy { ClickEventCallback<T, PlayerClickButtonEvent<T>>(this as T) }
    var showEnchantsInTooltip = true
    var components = mutableMapOf<ComponentType<*>, Any>()

    constructor(type: ItemType) : this(ItemStack.builder().type(type).build())

    fun handleClick(event: PlayerClickButtonEvent<*>) {
        click.apply(event as PlayerClickButtonEvent<T>)
        onChanges.apply(this as T)
    }

    infix fun showEnchantmentsInTooltip(value: Boolean): T = apply {
        this.showEnchantsInTooltip = value
        onChanges.apply(this as T)
    } as T

    fun <C : Any> component(type: ComponentType<C>, value: C) = apply {
        this.components[type] = value
        onChanges.apply(this as T)
    } as T

    private fun <C> ItemStack.Builder.addComponent(type: ComponentType<C>, valueAny: Any) {
        val asCast = valueAny as? C ?: return
        component(type, asCast)
        onChanges.apply(this as T)
    }

    override fun <G : GuiScreen<*, *>> refresh(parent: G) {
        if (parent !is PacketGuiInventoryScreen<*>) error("Must be an instance of PacketGuiScreen<*>!")

        val slots = parent.getSlots(this)

        val setItemPackets = slots.map { slot ->
            WrapperPlayServerSetSlot(parent.windowId, parent.stateId, slot, buildItem())
        }

        parent.sendPacketsToViewers(*setItemPackets.toTypedArray())
    }

    override fun buildItem(): ItemStack {
        val itemStack = base.also { builder ->

            if (name != null) {
                builder.setComponent(ComponentTypes.ITEM_NAME, name)
            }

            components.forEach { (type, value) ->
                setComponent(builder, type, value)
            }

            // Clients perceive lore as always italic if not otherwise specified.
            val finalLore = lore.map { line ->
                Component.empty()
                    .append(line)
                    .style(Style.style().decoration(TextDecoration.ITALIC, false))
            }

            builder.setComponent(ComponentTypes.LORE, ItemLore(finalLore))
            builder.setComponent(ComponentTypes.ENCHANTMENTS, ItemEnchantments(enchantments, showEnchantsInTooltip))
            preBuild.apply(builder)
        }

        postBuild.apply(itemStack)

        return itemStack
    }

    // Hack to get around generics safety 🔥🔥🔥
    private fun <T : Any> setComponent(base: ItemStack, type: ComponentType<T>, value: Any) {
        base.setComponent(type, Optional.of<T>(value as T))
    }

}