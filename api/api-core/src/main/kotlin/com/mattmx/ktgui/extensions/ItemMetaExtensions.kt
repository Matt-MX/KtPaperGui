package com.mattmx.ktgui.extensions

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.ItemMeta

/**
 * Extension function to completely replace all enchantments of the item.
 *
 * @param enchants new enchants to apply
 */
fun ItemMeta.setEnchantments(enchants: MutableMap<Enchantment, Int>) {
    this.enchants.keys.forEach { this.removeEnchant(it) }
    enchants.forEach { (e, l) -> this.addEnchant(e, l, true) }
}