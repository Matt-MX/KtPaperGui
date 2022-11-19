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
import org.bukkit.potion.PotionType

fun foo() {
    itemBuilder {
        material = Material.WOODEN_SWORD
        name = "&cItem Builder".color()
        lore += ""
        lore += "&7This is lore!".color()
        lore += ""
        enchantments += Enchantment.MENDING lvl 1
        potionEffects += PotionType.STRENGTH time 60 level 0
    }
    val item = KT ibuilder Material.WOODEN_SWORD name "&cInline itembuilder" lore "&7Reminds me of skript" ench (Enchantment.BINDING_CURSE lvl 1)
    val pot = KT ibuilder Material.POTION name "&bWKD 40" lore "&7Teens love it" effect (PotionType.SPEED time 60 lvl 4)
}

infix fun KT.itemBuilder(material: Material) : DslIBuilder = ibuilder(material)
infix fun KT.ibuilder(material: Material) : DslIBuilder {
    return DslIBuilder().material(material)
}

inline fun itemBuilderStack(builder: DslIBuilder.() -> Unit): ItemStack = itemBuilder(builder).build()
inline fun itemBuilder(builder: DslIBuilder.() -> Unit): DslIBuilder {
    val builderInst = DslIBuilder()
    builder(builderInst)
    return builderInst
}

infix fun Pair<PotionType, Pair<Int, Int>>.lvl(lvl: Int) = level(lvl)
infix fun Pair<PotionType, Pair<Int, Int>>.level(level: Int) = this.copy(second = second.copy(second = level))
infix fun PotionType.duration(time: Int) = time(time)
infix fun PotionType.time(time: Int) : Pair<PotionType, Pair<Int, Int>> = Pair(this, Pair(time, 0))
infix fun <F> Enchantment.lvl(lvl: F) = level(lvl)
infix fun <F> Enchantment.level(level: F): Pair<Enchantment, F> = Pair(this, level)

object KT

class DslIBuilder {
    var material: Material = Material.STONE
    var name: String? = null
    val lore = arrayListOf<String>()
    var amount = 1
    val enchantments = hashMapOf<Enchantment, Int>()
    val potionEffects = hashMapOf<PotionType, Pair<Int, Int>>()
    var color: Color? = null
    var skullOwner: String? = null
    var durability: Short? = null

    infix fun material(m: Material) : DslIBuilder { material = m; return this }
    infix fun name(n: String) : DslIBuilder { name = n; return this }
    infix fun amount(a: Int) : DslIBuilder { amount = a; return this }
    infix fun lore(l: String) : DslIBuilder { lore += l; return this }
    infix fun ench(e: Pair<Enchantment, Int>) : DslIBuilder { enchantments += e; return this }
    infix fun effect(e: Pair<PotionType, Pair<Int, Int>>) : DslIBuilder { potionEffects += e; return this }
    infix fun color(c: Color) : DslIBuilder { color = c; return this }
    infix fun skull(o: String) : DslIBuilder { skullOwner = o; return this }
    infix fun dura(d: Short) : DslIBuilder { durability = d; return this }

    fun materialOf(name: String): Material? {
        return Material.values().firstOrNull { it.name.lowercase() == name.lowercase().replace(" ", "_") }
    }

    fun clone() = copy()
    fun copy(): DslIBuilder {
        val ib = DslIBuilder()
        ib.material = material
        ib.name = name
        ib.lore += lore
        ib.enchantments += enchantments
        ib.color = color
        ib.skullOwner = skullOwner
        return ib
    }

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
                val potionEffect = type.effectType?.let { PotionEffect(it, duration, level) }
                potionEffect?.let { potMeta?.addCustomEffect(potionEffect, true) }
            }
            stack.itemMeta = potMeta
        }
        return stack
    }
}
