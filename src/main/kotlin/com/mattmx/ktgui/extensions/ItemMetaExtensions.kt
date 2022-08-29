package com.mattmx.ktgui.extensions

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.ItemMeta

fun ItemMeta.setEnchantments(enchants: MutableMap<Enchantment, Int>) {
    this.enchants.keys.forEach { this.removeEnchant(it) }
    enchants.forEach { (e, l) -> this.addEnchant(e, l, true) }
}