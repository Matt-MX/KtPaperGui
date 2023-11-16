package com.mattmx.ktgui.item

import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Inline item builder starter functions.
 *
 * @param material of the item (Must be first!)
 * [KT] is an empty object used for starting the inline item builder.
 */
object KT
infix fun KT.ib(material: Material) = iBuilder(material)
infix fun KT.itemBuilder(material: Material) = iBuilder(material)
infix fun KT.iBuilder(material: Material): DslIBuilder {
    return DslIBuilder(material)
}

/**
 * Normal item builder methods.
 *
 * @param builder block for building the item
 * @return the item as a stack or a builder.
 */
inline fun itemBuilderStack(material: Material, builder: DslIBuilder.() -> Unit): ItemStack = itemBuilder(material, builder).build()
inline fun itemBuilder(material: Material, builder: DslIBuilder.() -> Unit) = DslIBuilder(material).apply(builder)
fun itemBuilder(material: Material) = DslIBuilder(material)

inline infix fun ItemStack.builder(builder: DslIBuilder.() -> Unit) =
    itemBuilder(type) {
        name = itemMeta?.displayName()
        itemMeta?.lore()?.let { lore = it.toMutableList() }
        itemMeta?.enchants?.forEach { (ench, lvl) ->
            enchantments += ench lvl lvl
        }
        if (itemMeta is PotionMeta) {
            val potionMeta = itemMeta as PotionMeta
            potionMeta.customEffects.toList().forEach { effect ->
                potionEffects[effect.type] = effect.amplifier to effect.duration
            }
        }
        customModelData = itemMeta?.customModelData
        amount = getAmount()
        durability = getDurability()
    }

/**
 * Methods for making your builders look nicer, and more easily readable.
 */
infix fun Pair<PotionEffectType, Pair<Int, Int>>.lvl(lvl: Int) = level(lvl)
infix fun Pair<PotionEffectType, Pair<Int, Int>>.level(level: Int) = this.copy(second = second.copy(second = level))
infix fun PotionEffectType.lvl(lvl: Int) = level(lvl)
infix fun PotionEffectType.level(level: Int): Pair<PotionEffectType, Pair<Int, Int>> = Pair(this, Pair(1, level))
infix fun PotionEffectType.duration(time: Int) = time(time)
infix fun PotionEffectType.time(time: Int): Pair<PotionEffectType, Pair<Int, Int>> = Pair(this, Pair(time, 0))
infix fun <F> Enchantment.lvl(lvl: F) = level(lvl)
infix fun <F> Enchantment.level(level: F): Pair<Enchantment, F> = Pair(this, level)
operator fun DslIBuilder.invoke() = build()

class DslIBuilder(var material: Material) {
    var name: Component? = null
    var lore = mutableListOf<Component>()
    var amount = 1
    val enchantments = hashMapOf<Enchantment, Int>()
    val potionEffects = hashMapOf<PotionEffectType, Pair<Int, Int>>()
    var color: Color? = null
    var skullOwner: OfflinePlayer? = null
    var durability: Short? = null
    var customModelData: Int? = null

    /**
     * Infix functions allows us to make inline item builders
     * read like sentences.
     * Names of functions kept minimal to reduce unwanted length
     */
    infix fun mat(m: Material) = material(m)
    infix fun material(m: Material): DslIBuilder {
        material = m; return this
    }

    infix fun named(n: Component) = name(n)
    infix fun name(n: Component): DslIBuilder {
        name = n; return this
    }

    infix fun amount(a: Int): DslIBuilder {
        amount = a; return this
    }

    infix fun lore(l: Component): DslIBuilder {
        lore += l; return this
    }

    infix fun enchant(e: Pair<Enchantment, Int>) = ench(e)
    infix fun ench(e: Pair<Enchantment, Int>): DslIBuilder {
        enchantments += e; return this
    }

    infix fun potion(e: Pair<PotionEffectType, Pair<Int, Int>>) = effect(e)
    infix fun effect(e: Pair<PotionEffectType, Pair<Int, Int>>): DslIBuilder {
        potionEffects += e; return this
    }

    infix fun color(c: Color): DslIBuilder {
        color = c; return this
    }

    infix fun skull(o: OfflinePlayer): DslIBuilder {
        skullOwner = o; return this
    }

    infix fun durability(d: Short) = dura(d)
    infix fun dura(d: Short): DslIBuilder {
        durability = d; return this
    }

    infix fun modelData(d: Int) : DslIBuilder {
        customModelData = d; return this
    }

    // Gross function if you want to start a new line to make your builder readable (yuck inline builders)
    infix fun nl(cb: () -> Unit): DslIBuilder = this

    // Get material from string name or null
    fun materialOf(name: String): Material? {
        return Material.values().firstOrNull { it.name.lowercase() == name.lowercase().replace(" ", "_") }
    }

    // Shortened materialOf method for inline
    infix fun matOf(name: String): DslIBuilder {
        materialOf(name)?.let { material = it }
        return this
    }

    // Method for formatting all strings of the item (Can be inlined)
    inline infix fun format(cb: Component.() -> Component): DslIBuilder {
        name?.let { name = cb(it) }
        lore = lore.map { cb(it) }.toMutableList()
        return this
    }

    // To simply copy the item
    fun clone() = copy()
    fun copy(): DslIBuilder {
        val ib = DslIBuilder(material)
        ib.name = name
        ib.amount = amount
        ib.lore += lore
        ib.enchantments += enchantments
        ib.potionEffects += potionEffects
        ib.color = color
        ib.skullOwner = skullOwner
        ib.durability = durability
        ib.customModelData = customModelData
        return ib
    }

    /**
     * Puts the item together finally.
     *
     * @return the built item as [ItemStack]
     */
    fun build(): ItemStack {
        val stack = ItemStack(material)
        var meta = stack.itemMeta!!
        name?.let { meta.displayName(name) }
        meta.lore(lore.toMutableList())
        if (material == Material.LEATHER_BOOTS || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_HELMET) {
            val leatherMeta = meta as LeatherArmorMeta
            leatherMeta.setColor(color)
            meta = leatherMeta
        }
        if (material == Material.PLAYER_HEAD && skullOwner != null) {
            val skullMeta = meta as SkullMeta
            skullMeta.owningPlayer = skullOwner
            meta = skullMeta
        }
        if (material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION || material == Material.TIPPED_ARROW) {
            val potMeta = meta as PotionMeta
            potionEffects.forEach { (type, u) ->
                val duration = u.first
                val level = u.second
                val potionEffect = PotionEffect(type, duration, level)
                potionEffect.let { potMeta.addCustomEffect(potionEffect, true) }
            }
            meta = potMeta
        }
        meta.setCustomModelData(customModelData)
        stack.itemMeta = meta
        stack.addUnsafeEnchantments(enchantments.toMutableMap())
        stack.amount = amount
        durability?.let { stack.durability = it }
        return stack
    }
}
