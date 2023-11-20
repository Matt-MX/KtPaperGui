package com.mattmx.ktgui.extensions

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.item.lvl
import com.mattmx.ktgui.utils.component
import com.mattmx.ktgui.utils.legacy
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment

fun String.translatableList(config: FileConfiguration): List<String> {
    val list = config.getStringList(this)
    if (list.size > 0) {
        return list
    }
    return config.getString(this, null)?.let { listOf(it) } ?: listOf()
}

fun List<String>.component() = map { it.component }
    .reduce { c1, c2 -> c1.append(c2) }

private val ENCHANT_LINE_REGEX = "[A-Za-z_]+(:\\d+)?".toRegex()
fun String.translatableButton(config: FileConfiguration): GuiButton<*>? {
    val section = config.getConfigurationSection(this)
        ?: return null
    return button<GuiButton<*>> {
        materialOf(section.getString("material"), Material.AIR)
        amount(section.getInt("amount", 1))
        named(section.getString("name")?.component ?: Component.empty())
        lore { addAll(section.getStringList("lore").map { it.component }) }

        // enchantments
        val enchants = arrayListOf<Pair<Enchantment, Int>>()
        for (enchantmentLine in section.getStringList("enchantments")) {
            if (!enchantmentLine.matches(ENCHANT_LINE_REGEX)) {
                GuiManager.owningPlugin.logger.info("Translatable button (${this}) for config '${config.name}': Enchantment '$enchantmentLine' is invalid.")
                continue
            }

            val split = enchantmentLine.split(":")
            val enchantment = Enchantment.getByKey(NamespacedKey.fromString(split.getOrNull(0) ?: enchantmentLine))
            if (enchantment == null) {
                GuiManager.owningPlugin.logger.info("Translatable button (${this}) for config '${config.name}': Enchantment '${split[0]}' is not recognized.")
                continue
            }
            val level = split.getOrNull(1)?.toIntOrNull() ?: 1
            enchants += enchantment lvl level
        }
        enchant { putAll(enchants) }
        // todo potion effects
    }
}

fun FileConfiguration.setButton(path: String, button: GuiButton<*>) {
    val section = createSection(path)
    val item = button.getItemStack() ?: return
    section.set("material", item.type)
    section.set("amount", item.amount)
    section.set("name", item.displayName().legacy())
    section.set("lore", item.lore()?.map { it.legacy() })
    section.set("enchantments", item.enchantments.entries.map { "${it.key.name}:${it.value}" })
    // todo potion effects
}