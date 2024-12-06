package com.mattmx.ktgui

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

fun main() {
    val button = button(Material.SPAWNER) {
        named(Component.text("Meow"))

        lore {
            +Component.text("Item Lore!")
        }

        enchant {
            Enchantment.MENDING lvl 10
        }
    }

}