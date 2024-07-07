package com.mattmx.ktgui.designer

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.extensions.stripColor
import com.mattmx.ktgui.utils.legacy
import org.bukkit.inventory.ItemStack

class GuiDesignerButton(item: ItemStack) : GuiButton<GuiDesignerButton>(item) {

    val enchantPart: String?
        get() {
            val start = "enchant { "
            val enchantments = getItemStack()!!.enchantments.entries
            if (enchantments.isEmpty()) return null

            val middle = enchantments.joinToString("\n") { (e, l) ->
                "   this += Enchantment.${e.name} lvl $l"
            }
            return "$start$middle\n}"
        }

    val lorePart: String?
        get() {
            val start = "lore {"
            val lore = getItemStack()!!.lore()
                ?: return null
            if (lore.isEmpty()) return null

            val middle = lore.joinToString("\n") {
                "   add(!\"${it.legacy().replace("§", "&")}\")"
            }
            return "$start$middle\n}"
        }

    val namedPart: String
        get() {
            val name = getItemStack()!!
                .displayName()
                .legacy()
                .replace("§", "&")

            return "named(!\"$name\")"
        }

    val slotsPart: String
        get() {
            return "slots listOf(${slots().joinToString(", ")})"
        }

    val full: String
        get() {
            val start = "button(Material.${getItemStack()!!.type}) {\n"

            val tab = "   "
            var middle = tab
            middle += namedPart + "\n"

            if (lorePart != null)
             middle += lorePart!!.split("\n").joinToString("\n$tab") + "\n"

            if (enchantPart != null)
                middle += enchantPart!!.split("\n").joinToString("\n$tab") + "\n"

            val end = "} $slotsPart"

            return start + middle + end
        }

}