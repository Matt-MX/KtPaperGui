package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.guiconfig.configButton
import org.bukkit.configuration.file.FileConfiguration

class GuiConfigScreen(
    val gui: GuiScreen,
    val path: String,
    val config: FileConfiguration
) {
    fun parseButtons() = apply {
        val section = config.getConfigurationSection("$path.buttons")
            ?: error("There is no configuration section named 'buttons'.")

        for (key in section.getKeys(false)) {
            val buttonPath = "$path.buttons.$key"

            val slotString = config.getString("$buttonPath.slot")?.let { listOf(it) }
                ?: config.getStringList("$buttonPath.slots")

            val slots = slotString.mapNotNull {
                when (it.lowercase()) {
                    "last" -> gui.last()
                    "first" -> gui.first()
                    "middle" -> gui.middle()
                    else -> it.toIntOrNull()
                }
            }

            configButton(config, buttonPath) {
                slots(slots)
            } childOf gui
        }
    }
}