package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.utils.JavaCompatibility
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@JavaCompatibility
class BaseGuiButton(
    material: Material = Material.STONE,
    item: ItemStack? = null
) : GuiButton<BaseGuiButton>(material, item)