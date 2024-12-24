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
import com.mattmx.ktgui.event.PlayerClickButtonEvent
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.util.ParentEventCallback
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration

@Suppress("UNCHECKED_CAST")
open class PacketGuiButton<T : PacketGuiButton<T>>(
    var material: ItemType
) : GuiButton<T, ItemType, EnchantmentType, ItemStack>() {
    val preBuild by lazy { ParentEventCallback<ItemStack.Builder, T>(this as T) }
    override val click by lazy { ClickEventCallback<T, PlayerClickButtonEvent<T>>(this as T) }
    var showEnchantsInTooltip = true
    var components = mutableMapOf<ComponentType<*>, Any>()

    fun handleClick(event: PlayerClickButtonEvent<*>) {
        click.apply(event as PlayerClickButtonEvent<T>)
    }

    infix fun showEnchantmentsInTooltip(value: Boolean): T = apply {
        this.showEnchantsInTooltip = value
    } as T

    fun <C : Any> component(type: ComponentType<C>, value: C) = apply {
        this.components[type] = value
    } as T

    private fun <C> ItemStack.Builder.addComponent(type: ComponentType<C>, valueAny: Any) {
        val asCast = valueAny as? C ?: return
        component(type, asCast)
    }

    override fun <G : GuiScreen<*, *>> refresh(parent: G) {
        if (parent !is PacketGuiScreen<*>) error("Must be an instance of PacketGuiScreen<*>!")

        val slots = parent.getSlots(this)

        val setItemPackets = slots.map { slot ->
            WrapperPlayServerSetSlot(parent.windowId, parent.stateId, slot, buildItem())
        }

        parent.sendPacketsToViewers(*setItemPackets.toTypedArray())
    }

    override fun buildItem(): ItemStack {
        val itemStack = ItemStack.builder()
            .type(material)
            .amount(amount)
            .also { builder ->
                if (name != null) {
                    builder.component(ComponentTypes.ITEM_NAME, name)
                }
                components.forEach { (type, value) ->
                    builder.addComponent(type, value)
                }

                // Clients perceive lore as always italic if not otherwise specified.
                val finalLore = lore.map { line ->
                    Component.empty()
                        .append(line)
                        .style(Style.style().decoration(TextDecoration.ITALIC, false))
                }

                builder.component(ComponentTypes.LORE, ItemLore(finalLore))
            }
            .component(ComponentTypes.ENCHANTMENTS, ItemEnchantments(enchantments, showEnchantsInTooltip))
            .also { builder ->
                preBuild.apply(builder)
            }
            .build()

        postBuild.apply(itemStack)

        return itemStack
    }

}