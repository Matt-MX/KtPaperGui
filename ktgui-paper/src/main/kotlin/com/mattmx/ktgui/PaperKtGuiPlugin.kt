package com.mattmx.ktgui

import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.InventoryTypes
import com.mattmx.ktgui.util.not
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.plugin.java.JavaPlugin

class PaperKtGuiPlugin : JavaPlugin() {

    override fun onEnable() {
        gui(!"Test", GuiType.ofType(InventoryTypes.HOPPER)) {
            button(Material.SCUTE) {
                flags(ItemFlag.HIDE_DYE)
            }
        }
    }

}