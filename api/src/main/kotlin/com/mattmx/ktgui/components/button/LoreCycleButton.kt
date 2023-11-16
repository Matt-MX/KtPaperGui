package com.mattmx.ktgui.components.button

import net.kyori.adventure.text.Component
import kotlin.math.max
import kotlin.math.min

class LoreCycleButton : GuiButton<LoreCycleButton>() {
    var selected = 0
        set(value) {
            val selectable = selectableLines
            field = max(min(value, selectable.size - 1), 0)
        }
    private lateinit var lines: List<LoreCycleLine>
    val selectableLines
        get() = lines.filter { line -> line.id >= 0 }
    val selectedLoreLine: LoreCycleLine?
        get() = selectableLines.getOrNull(selected)

    fun loreCycle(block: LoreCycleLineBuilder.() -> Unit) = apply {
        lines = LoreCycleLineBuilder().apply(block).lines
    }

    @Deprecated("This should not be used in this class.", ReplaceWith("this"))
    override fun lore(lore: MutableList<Component>.() -> Unit): LoreCycleButton {
        return this
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