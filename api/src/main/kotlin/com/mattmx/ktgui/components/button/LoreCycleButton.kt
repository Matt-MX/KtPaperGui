package com.mattmx.ktgui.components.button

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

class LoreCycleButton : GuiButton<LoreCycleButton>() {
    var selected = 0
        set(value) {
            val selectable = selectableLines
            field =
                if (value > selectable.size - 1) 0
                else if (value < 0) selectable.size - 1
                else value
            update()
        }
    private lateinit var lines: List<LoreCycleLine>
    val selectableLines: List<LoreCycleLine>
        get() {
            val found = arrayListOf<Int>()
            val lines = lines.filter { line ->
                (line.id >= 0 && line.id !in found).apply {
                    if (this) {
                        found.add(line.id)
                    }
                }
            }
            return lines
        }
    val selectedLoreLine: LoreCycleLine?
        get() = selectableLines.firstOrNull { it.id == selected }

    fun loreCycle(block: LoreCycleLineBuilder.() -> Unit) = apply {
        lines = LoreCycleLineBuilder().apply(block).lines
    }

    @Deprecated("This should not be used in this class.", ReplaceWith("this"))
    override fun lore(lore: MutableList<Component>.() -> Unit): LoreCycleButton {
        return this
    }

    override fun formatIntoItemStack(player: Player?): ItemStack? {
        // here we need to apply and format lores
        val loreToApply = mutableListOf<Component>()
        val item = item?.clone()
        lines.forEach { loreLine ->
            loreToApply.add(
                if (loreLine.id == selected) (loreLine.selectedText ?: loreLine.defaultText) else loreLine.defaultText
            )
        }
        item?.itemMeta?.let {
            it.lore(loreToApply.map { lore -> Component.empty().decoration(TextDecoration.ITALIC, false).append(lore) })
            item.itemMeta = it
        }
        return item
    }

    class LoreCycleLineBuilder {
        val lines = arrayListOf<LoreCycleLine>()

        fun add(line: LoreCycleLine) {
            lines.add(line)
        }

        fun add(line: Component) = add(LoreCycleLine(-1, line))

        infix fun Component.withSelectedText(component: Component) = LoreCycleLine(-1, this, component)
        infix fun Component.withId(id: Int) = LoreCycleLine(id, this)
    }

    class LoreCycleLine(
        var id: Int = -1,
        var defaultText: Component,
        var selectedText: Component? = null
    ) {
        infix fun withId(id: Int) = apply { this.id = id }
        infix fun withSelectedText(component: Component) = apply { this.selectedText = component }
        infix fun withDefaultText(component: Component) = apply { this.defaultText = component }
    }
}