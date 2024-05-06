package com.mattmx.ktgui.guiconfig

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiConfigScreen
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.extensions.translatableButton
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.inventory.InventoryType

inline fun <reified T : Any> T.configGui(path: String, block: GuiConfigScreen.() -> Unit): GuiConfigScreen {
    val config = GuiManager.guiConfigManager.getConfigFile<T>()
    return configGui(config, path, block)
}

inline fun configGui(config: FileConfiguration, path: String, block: GuiConfigScreen.() -> Unit): GuiConfigScreen {
    val section = config.getConfigurationSection(path)
        ?: error("No such config section '$path'")

    val title = !section.getString("title", "")!!
    val rows = section.getInt("rows", -1)
    val type = InventoryType.values()
        .firstOrNull { it.name.equals(section.getString("type"), true) }

    if (rows < 1 && type == null)
        error("You must specify a value for 'rows' or 'type' for '$path'")

    val gui = GuiScreen(title, rows, type)

    return GuiConfigScreen(gui, path, config).apply(block)
}

inline fun <T : GuiButton<T>, reified V : Any> V.configButton(
    path: String,
    block: GuiButton<T>.() -> Unit
): T {
    val config = GuiManager.guiConfigManager.getConfigFile<V>()
    return configButton(config, path, block)
}

inline fun <T : GuiButton<T>> configButton(config: FileConfiguration, path: String, block: GuiButton<T>.() -> Unit): T {
    return configButton(config, path, GuiButton(Material.AIR), block)
}

inline fun <T : GuiButton<T>, reified V : Any> V.configButton(
    path: String,
    existing: GuiButton<T>,
    block: GuiButton<T>.() -> Unit
): T {
    val config = GuiManager.guiConfigManager.getConfigFile(V::class.java)
    return configButton(config, path, existing, block)
}

inline fun <T : GuiButton<T>> configButton(
    config: FileConfiguration,
    path: String,
    existing: GuiButton<T>,
    block: GuiButton<T>.() -> Unit
): T {
    val button = path.translatableButton(config, existing) ?: error("Invalid config section for a GuiButton '$path'")
    button.id = path.split(".").lastOrNull() ?: path
    return button.apply(block)
}