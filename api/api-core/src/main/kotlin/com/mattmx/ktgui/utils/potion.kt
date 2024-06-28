package com.mattmx.ktgui.utils

import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType

@Deprecated("PotionData is deprecated, but the functionality still works for vanilla potion effects.")
fun parsePotionData(str: String): PotionData? {
    val split = str.split("_")
    val modifier = split.firstOrNull()

    val upgraded: Boolean
    val extended: Boolean
    when (modifier?.lowercase()) {
        "long" -> {
            upgraded = false
            extended = true
        }

        "strong" -> {
            upgraded = true
            extended = false
        }

        else -> {
            upgraded = false
            extended = false
        }
    }

    val potionEffectId = str.replace("(long|strong)_".toRegex(RegexOption.IGNORE_CASE), "")
    val potionType = PotionType.values().firstOrNull { it.name == potionEffectId.uppercase() }

    potionType ?: return null

    return PotionData(potionType, extended && potionType.isExtendable, upgraded && potionType.isUpgradeable)
}