package com.mattmx.ktgui

import com.mattmx.ktgui.click.ClickButtonEvent
import com.mattmx.ktgui.click.ClickEventCallback
import com.mattmx.ktgui.util.EnchantmentMap
import com.mattmx.ktgui.util.ParentEventCallback
import com.mattmx.ktgui.util.UnaryOperatorList
import net.kyori.adventure.text.Component
import java.util.*

@Suppress("UNCHECKED_CAST")
abstract class GuiButton<S : GuiButton<S, M, E, I>, M, E, I> {
    var buttonId: String = EMPTY_ID
    var name: Optional<Component> = Optional.empty()
    var lore = mutableListOf<Component>()
    var amount: Int = 1
    var enchantments = mutableMapOf<E, Int>()
    val postBuild = ParentEventCallback<I, S>(this as S)

    infix fun named(name: Component?): S = apply {
        this.name = Optional.ofNullable(name)
    } as S

    infix fun amount(amount: Int): S = apply {
        this.amount = amount
    } as S

    infix fun lore(line: Component): S = apply {
        this.lore.add(line)
    } as S

    infix fun lore(block: UnaryOperatorList<Component>.() -> Unit): S = setLore(
        UnaryOperatorList<Component>()
            .also { m -> m.addAll(lore) }
            .apply(block)
    )

    infix fun setLore(list: List<Component>): S = apply {
        this.lore = list.toMutableList()
    } as S

    inline infix fun enchant(block: EnchantmentMap<E>.() -> Unit): S = apply {
        setEnchantments(
            EnchantmentMap<E>()
                .also { m -> m.putAll(enchantments) }
                .apply(block)
        )
    } as S

    fun enchant(enchantment: E, level: Int): S = apply {
        enchantments[enchantment] = level
    } as S

    infix fun setEnchantments(map: Map<E, Int>): S = apply {
        this.enchantments = map.toMutableMap()
    } as S

    fun hasEnchantment(enchantment: E) = enchantments.containsKey(enchantment)

    fun removeEnchantment(enchantment: E): S = apply {
        enchantments.remove(enchantment)
    } as S

    abstract fun buildItem(): I

    abstract fun getClickEventHandler(): ClickEventCallback<S, *>

    companion object {
        const val EMPTY_ID = "emptyId"
    }
}