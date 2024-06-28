package com.mattmx.ktgui.extensions

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.item.lvl
import com.mattmx.ktgui.utils.component
import com.mattmx.ktgui.utils.legacy
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.parsePotionData
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.potion.PotionData

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

/**
 * A function for reading the basic information of a [GuiButton],
 * gets the material, amount, name, lore, enchantments and potion meta.
 *
 * Where [this] is the path to the button.
 *
 * @param config configuration file to pull the data from
 * @param button to apply the changes to
 * @return a [GuiButton] with changes applied or null if the path is invalid
 */
fun String.translatableButton(config: FileConfiguration) = this.translatableButton(config, GuiButton(Material.AIR))

/**
 * A function for reading the basic information of a [GuiButton],
 * gets the material, amount, name, lore, enchantments and potion meta.
 *
 * Where [this] is the path to the button.
 *
 * @param T the type of [GuiButton]
 * @param config configuration file to pull the data from
 * @param button to apply the changes to
 * @return button as [T] with changes applied or null if the path is invalid
 */
fun <T : GuiButton<T>> String.translatableButton(config: FileConfiguration, button: GuiButton<T>): T? {
    val section = config.getConfigurationSection(this)
        ?: return null
    return button.apply {
        materialOf(section.getString("material"), Material.AIR)
        amount(section.getInt("amount", 1))
        section.getString("name")?.let {
            named(!it)
        }
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

        // todo this won't work for multiple potion types or anything that is not vanilla!
        val potions = config.getStringList("effects")
        val effects = arrayListOf<PotionData>()
        potions.forEach { str ->
            val data = parsePotionData(str)
            if (data != null) {
                effects.add(data)
            }
        }
        effects {
            effects.forEach { effect ->
                basePotionData = effect
            }
        }
    } as T
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