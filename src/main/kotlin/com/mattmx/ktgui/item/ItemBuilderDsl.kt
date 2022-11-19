package com.mattmx.ktgui.item

import com.mattmx.ktgui.extensions.color
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

fun foo() {

    itemBuilder {
        material = Material.WOODEN_SWORD
        name = "&cItem Builder".color()
        lore += ""
        lore += "&7This is lore!".color()
        lore += ""
        enchantments += Enchantment.MENDING lvl 1
        enchantments += Enchantment.OXYGEN lvl 10
    }.format { color() }

    val item = KT ib Material.WOODEN_SWORD name "&cInline item builder" lore "&7Reminds me of Skript" ench (Enchantment.BINDING_CURSE lvl 1) format { color() }

    val pot = KT ib Material.POTION name "&bWKD 40" lore "&7Teens love it" effect (PotionEffectType.SPEED time 60 lvl 4) format { color() }

    val sword = KT ib Material.DIAMOND_SWORD name "&4Punisher" lore "&8Bruh ting" lore "&8Wet man up" ench (Enchantment.DAMAGE_ALL lvl 255) format { color() }
    
    val reallyBigBuilder = KT ib Material.POTION named "&dGod potion" lore "" lore "&7Drink this to become high," lore "&7powerful, and full of" nl {
    } lore "&7might. &o@MattMX" effect (PotionEffectType.SPEED time 255 lvl 4) effect (PotionEffectType.INCREASE_DAMAGE time 255 lvl 4) nl {
    } effect (PotionEffectType.INVISIBILITY time 255) effect (PotionEffectType.HEALTH_BOOST time 255 lvl 2000) effect (PotionEffectType.HEAL lvl 2000) nl {
    } format String::color

    val realUsage = KT ib Material.ARROW name "&eNext Page" lore "" lore "&7Click to go to page %page%" format String::color
}

/**
 * DSL based Item Builder - will likely deprecate old [ItemBuilder].
 *
 * Capable of also making items completely inline (should you really need to).
 */

/**
 * Inline item builder starter functions.
 *
 * @param material of the item (Must be first!)
 * [KT] is an empty object used for starting the inline item builder.
 */
infix fun KT.ib(material: Material) = iBuilder(material)
infix fun KT.itemBuilder(material: Material) = iBuilder(material)
infix fun KT.iBuilder(material: Material) : DslIBuilder {
    return DslIBuilder().material(material)
}
// Dummy object
object KT

/**
 * Normal item builder methods.
 *
 * @param builder block for building the item
 * @return the item as a stack or a builder.
 */
inline fun itemBuilderStack(builder: DslIBuilder.() -> Unit): ItemStack = itemBuilder(builder).build()
inline fun itemBuilder(builder: DslIBuilder.() -> Unit): DslIBuilder {
    val builderInst = DslIBuilder()
    builder(builderInst)
    return builderInst
}

/**
 * Methods for making your builders look nicer, and more easily readable.
 */
infix fun Pair<PotionEffectType, Pair<Int, Int>>.lvl(lvl: Int) = level(lvl)
infix fun Pair<PotionEffectType, Pair<Int, Int>>.level(level: Int) = this.copy(second = second.copy(second = level))
infix fun PotionEffectType.lvl(lvl: Int) = level(lvl)
infix fun PotionEffectType.level(level: Int) : Pair<PotionEffectType, Pair<Int, Int>> = Pair(this, Pair(1, level))
infix fun PotionEffectType.duration(time: Int) = time(time)
infix fun PotionEffectType.time(time: Int) : Pair<PotionEffectType, Pair<Int, Int>> = Pair(this, Pair(time, 0))
infix fun <F> Enchantment.lvl(lvl: F) = level(lvl)
infix fun <F> Enchantment.level(level: F): Pair<Enchantment, F> = Pair(this, level)

class DslIBuilder {
    var material: Material = Material.STONE
    var name: String? = null
    val lore = arrayListOf<String>()
    var amount = 1
    val enchantments = hashMapOf<Enchantment, Int>()
    val potionEffects = hashMapOf<PotionEffectType, Pair<Int, Int>>()
    var color: Color? = null
    var skullOwner: String? = null
    var durability: Short? = null

    /**
     * Infix functions allows us to make inline item builders
     * read like sentences.
     * Names of functions kept minimal to reduce unwanted length
     */
    infix fun mat(m: Material) = material(m)
    infix fun material(m: Material) : DslIBuilder { material = m; return this }
    infix fun named(n: String) = name(n)
    infix fun name(n: String) : DslIBuilder { name = n; return this }
    infix fun amount(a: Int) : DslIBuilder { amount = a; return this }
    infix fun lore(l: String) : DslIBuilder { lore += l; return this }
    infix fun enchant(e: Pair<Enchantment, Int>) = ench(e)
    infix fun ench(e: Pair<Enchantment, Int>) : DslIBuilder { enchantments += e; return this }
    infix fun potion(e: Pair<PotionEffectType, Pair<Int, Int>>) = effect(e)
    infix fun effect(e: Pair<PotionEffectType, Pair<Int, Int>>) : DslIBuilder { potionEffects += e; return this }
    infix fun color(c: Color) : DslIBuilder { color = c; return this }
    infix fun skull(o: String) : DslIBuilder { skullOwner = o; return this }
    infix fun durability(d: Short) = dura(d)
    infix fun dura(d: Short) : DslIBuilder { durability = d; return this }
    // Gross function if you want to start a new line to make your builder readable (yuck inline builders)
    infix fun nl(cb: () -> Unit) : DslIBuilder = this

    // Get material from string name or null
    fun materialOf(name: String): Material? {
        return Material.values().firstOrNull { it.name.lowercase() == name.lowercase().replace(" ", "_") }
    }

    // Shortened materialOf method for inline
    infix fun matOf(name: String) : DslIBuilder {
        materialOf(name)?.let { material = it }
        return this
    }

    // Method for formatting all strings of the item (Can be inlined)
    inline infix fun format(cb: String.() -> String) : DslIBuilder{
        name?.let { name = cb(it) }
        lore.map { cb(it) }
        return this
    }

    // To simply copy the item
    fun clone() = copy()
    fun copy(): DslIBuilder {
        val ib = DslIBuilder()
        ib.material = material
        ib.name = name
        ib.amount = amount
        ib.lore += lore
        ib.enchantments += enchantments
        ib.potionEffects += potionEffects
        ib.color = color
        ib.skullOwner = skullOwner
        ib.durability = durability
        return ib
    }

    /**
     * Puts the item together finally.
     *
     * @return the built item as [ItemStack]
     */
    fun build(): ItemStack {
        val stack = ItemStack(material)
        name?.let { stack.itemMeta?.setDisplayName(it) }
        stack.amount = amount
        durability?.let { stack.durability = it }
        stack.itemMeta?.lore = lore.toMutableList()
        stack.addUnsafeEnchantments(enchantments.toMutableMap())
        if (material == Material.LEATHER_BOOTS || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_HELMET) {
            val meta = stack.itemMeta as LeatherArmorMeta?
            meta!!.setColor(color)
            stack.itemMeta = meta
        }
        if (material == Material.PLAYER_HEAD && durability == 3.toByte().toShort()) {
            val skullMeta = stack.itemMeta as SkullMeta?
            skullMeta!!.owner = name
            stack.itemMeta = skullMeta
        }
        if (material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION || material == Material.TIPPED_ARROW) {
            val potMeta = stack.itemMeta as PotionMeta?
            potionEffects.forEach { (type, u) ->
                val duration = u.first
                val level = u.second
                val potionEffect = PotionEffect(type, duration, level)
                potionEffect?.let { potMeta?.addCustomEffect(potionEffect, true) }
            }
            stack.itemMeta = potMeta
        }
        return stack
    }
}
