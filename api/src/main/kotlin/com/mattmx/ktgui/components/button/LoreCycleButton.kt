package com.mattmx.ktgui.components.button

import net.kyori.adventure.text.Component

class LoreCycleButton : GuiButton<LoreCycleButton>() {
    private var selected = 0
    private lateinit var lines: List<LoreCycleLine>
    val selectedLoreLine: LoreCycleLine?
        get() = lines.getOrNull(selected)

    fun loreCycle(block: LoreCycleLineBuilder.() -> Unit) : LoreCycleButton {
        val builder = LoreCycleLineBuilder().apply(block)
        lines = builder.lines
        return this
    }

    @Deprecated("This should not be used in this class.", ReplaceWith("this"))
    override fun lore(lore: MutableList<Component>.() -> Unit): GuiButton<LoreCycleButton> {
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