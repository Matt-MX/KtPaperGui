package com.mattmx.ktgui.ymlguis

import com.mattmx.ktgui.components.ClickEvents
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType

/**
 * This is not really supported and shouldn't be used.
 * I only made it for a bit of fun, and it does work as planned.
 *
 * Unfortunately we can only allow gui buttons to execute a command,
 * due to the limitations of this environment.
 */
object YamlGuiParser {

    fun parseGuiScreen(section: ConfigurationSection) : GuiScreen? {
        val gui = GuiScreen()
        gui.title(section.getString("title")!!)
        val type = section.getString("type")
        if (type != null) {
            gui type InventoryType.valueOf(type.uppercase().replace(" ", "_"))
        } else {
            gui rows section.getInt("rows")
        }
        section.getConfigurationSection("click")?.let {
            gui.click = parseClickEvents(it)
        }
        section.getStringList("close").let {
            if (it.size > 0) gui.close = getCommandExecution(it)
        }
        val itemSection = section.getConfigurationSection("items")
        itemSection?.let {
            var count = 1
            while (it.contains("$count")) {
                parseItem(it.getConfigurationSection("$count")!!) childOf gui
                count++
            }
        }
        return gui
    }

    fun parseItem(section: ConfigurationSection) : GuiButton {
        val button = GuiButton()
        button materialOf section.getString("material") named section.getString("name")
        section.getStringList("lore").let {
            if (it.size > 0) button.lore { loreList -> loreList.addAll(it) }
        }
        section.getStringList("enchantments").let { enchants ->
            if (enchants.size > 0) button
                .enchant { buttonEnchants -> enchants
                    .forEach { enchantString -> parseEnchant(enchantString, buttonEnchants) } }
        }
        section.getConfigurationSection("click")?.let { sec ->
            button.click = parseClickEvents(sec)
        }
        section.getStringList("close").let {
            if (it.size > 0) button.close = getCommandExecution(it)
        }
        button slot section.getInt("slot")
        return button
    }

    private fun parseEnchant(string: String, map: MutableMap<Enchantment, Int>) {
        val split = string.split(":")
        val ench = Enchantment.values().firstOrNull { it.name.lowercase() == split[0].lowercase() }
        val lvl = split[1].toInt()
        ench?.let {
            map[it] = lvl
        }
    }

    private fun parseClickEvents(section: ConfigurationSection) : ClickEvents {
        val ce = ClickEvents()
        ce.left = getCommandExecution(section.getStringList("left"))
        ce.shiftLeft = getCommandExecution(section.getStringList("left-shift"))
        ce.right = getCommandExecution(section.getStringList("right"))
        ce.shiftRight = getCommandExecution(section.getStringList("right-shift"))
        ce.windowBorderLeft = getCommandExecution(section.getStringList("wb-left"))
        ce.windowBorderRight = getCommandExecution(section.getStringList("wb-right"))
        ce.middle = getCommandExecution(section.getStringList("middle"))
        ce.numberKey = getCommandExecution(section.getStringList("number"))
        ce.doubleClick = getCommandExecution(section.getStringList("double"))
        ce.drop = getCommandExecution(section.getStringList("drop"))
        ce.ctrlDrop = getCommandExecution(section.getStringList("ctrl-drop"))
        ce.creative = getCommandExecution(section.getStringList("creative"))
        ce.swapOffhand = getCommandExecution(section.getStringList("offhand"))
        ce.generic = getCommandExecution(section.getStringList("generic"))
        return ce
    }

    private fun getCommandExecution(list: List<String>) : ((Event) -> Unit) {
        return { e ->
            var player: Player? = null
            if (e is InventoryClickEvent)
                player = e.whoClicked as Player
            else if (e is InventoryCloseEvent)
                player = e.player as Player
            list.forEach {
                if (it.startsWith("[as:CONSOLE]"))
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), it.replace("[as:CONSOLE]", ""))
                else player?.performCommand(it.replace("[as:PLAYER]", ""))
            }
        }
    }
}