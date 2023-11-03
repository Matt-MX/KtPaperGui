package com.mattmx.ktgui.extensions

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.utils.component
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

fun String.translatableList(config: FileConfiguration): List<String> {
    val list = config.getStringList(this)
    if (list.size > 0) {
        return list
    }
    return config.getString(this, null)?.let { listOf(it) } ?: listOf()
}

fun List<String>.component() = map { it.component }
    .reduce { c1, c2 -> c1.append(c2) }

fun String.translatableButton(config: FileConfiguration) : GuiButton? {
    val section = config.getConfigurationSection(this)
        ?: return null
    return button<GuiButton> {
        materialOf(section.getString("material"), Material.AIR)
        amount(section.getInt("amount", 1))
        named(section.getString("name"))
        lore { addAll(section.getStringList("lore")) }
        // enchant
        // potion effects
    }
}

fun main(config: FileConfiguration) {
    "some.key.to.message"
        .translatableList(config)
        .component()
}