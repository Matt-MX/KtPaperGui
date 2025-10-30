package com.mattmx.ktgui.button

import com.mattmx.ktgui.click.ClickEventCallback
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.trait.TraitHolder
import com.mattmx.ktgui.util.EnchantmentMap
import com.mattmx.ktgui.event.ParentEventCallback
import com.mattmx.ktgui.util.UnaryOperatorList
import net.kyori.adventure.text.Component

@Suppress("UNCHECKED_CAST")
abstract class GuiButton<S : GuiButton<S, M, E, I>, M, E, I> {
    var buttonId: String = EMPTY_ID
    var name: Component? = null
    var lore = mutableListOf<Component>()
    var amount: Int = 1
    var enchantments = mutableMapOf<E, Int>()
    val postBuild by lazy { ParentEventCallback<I, S>(this as S) }
    val onChanges by lazy { ParentEventCallback<S, S>(this as S) }
    val traits by lazy { TraitHolder(this as S) }
    abstract val click: ClickEventCallback<S, *>

    infix fun named(name: Component?): S = apply {
        this.name = name
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

    fun <T> consumeAs(block: T.() -> Unit) = apply {
        (this as? T)?.apply(block)
    } as S

    abstract fun <G : GuiScreen<*, *>> refresh(parent: G)

    abstract fun buildItem(): I

    companion object {
        const val EMPTY_ID = "emptyId"
    }
}