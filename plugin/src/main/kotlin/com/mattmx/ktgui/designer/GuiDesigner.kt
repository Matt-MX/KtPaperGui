package com.mattmx.ktgui.designer

import com.mattmx.ktgui.KotlinGui
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.scheduling.sync
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryType
import java.io.File

class GuiDesigner(
    val name: String,
    rows: Int = 1,
    type: InventoryType? = null
) : GuiScreen(!"Designer ($name&r)", rows, type) {
    var exportTitle = name
        set(value) {
            title = !exportTitle

            field = value
        }

    init {

        click.any {
            event.isCancelled = slot in 0..last()
        }

        click.left {
            if (slot !in 0..last()) {
                return@left
            }
            val clickedItem = itemClicked?.clone()

            val existing = items[slot]
            items.remove(slot)
            existing?.removeSlots(slot)

            GuiDesignerButton(player.itemOnCursor.clone()) childOf this@GuiDesigner slot slot
            player.setItemOnCursor(clickedItem)

            sync {
                refresh()
            }
        }

        click {
            (ClickType.SHIFT_LEFT + ClickType.SHIFT_RIGHT) {
                event.isCancelled = true
            }
        }

        click.middle {
            event.isCancelled = false
        }

        click.drop {
            val button = items.remove(slot)
            button?.removeSlots(slot)
            sync {
                refresh()
            }
        }

        click.right {
            if (isButton()) {
                GuiDesignerButtonCustomizer(this@GuiDesigner, button as GuiDesignerButton).open(player)
            }
        }
    }

    fun save(plugin: KotlinGui): File {
        val file = File("${plugin.dataFolder}/designer/$name.kt")

        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.writeText(export())
        return file
    }

    fun export(): String {
        val start = "gui(!\"$exportTitle\") {\n"
        val guiOption = if (type == null) "    rows = $rows" else "type = InventoryType.${type!!.name}"
        val middle = items.values
            .filterIsInstance<GuiDesignerButton>()
            .filter { it.getItemStack()?.type != Material.AIR }
            .joinToString("\n    ") { it.full + " childOf this" }
        val end = "\n}"

        return "$start$guiOption\n$middle$end"
    }

}