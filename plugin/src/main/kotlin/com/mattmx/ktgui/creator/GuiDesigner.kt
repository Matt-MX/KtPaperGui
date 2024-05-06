package com.mattmx.ktgui.creator

import com.mattmx.ktgui.KotlinGui
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.utils.not
import java.io.File

class GuiDesigner(
    val name: String
) : GuiScreen(!"Designer ($name&r)") {

    init {
        click.left {
            event.isCancelled = false

            if (slot !in 0..last()) {
                return@left
            }

            val existing = items[slot]
            items.remove(slot)
            existing?.removeSlots(slot)

            GuiButton(item = itemClicked) childOf this@GuiDesigner slot slot
        }

        click.right {
            if (isButton()) {
                GuiDesignerButtonCustomizer(this@GuiDesigner, button as GuiButton<*>).open(player)
            }
        }
    }

    fun save(plugin: KotlinGui): File {
        val file = File("${plugin.dataFolder}/designer/$name.kt")

        if (file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.writeText(export())
        return file
    }

    fun export(): String {
        val start = "gui(\"$name\") {"
        val middle = items.values.filterIsInstance<GuiDesignerButton>()
            .joinToString("\n") { it.full + " childOf this" }
        val end = "}"

        return "$start$middle$end"
    }

}