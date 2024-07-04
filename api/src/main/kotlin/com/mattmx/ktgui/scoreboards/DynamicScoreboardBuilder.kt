package com.mattmx.ktgui.scoreboards

import com.mattmx.ktgui.scheduling.TaskTracker
import net.kyori.adventure.text.Component

open class DynamicScoreboardBuilder(
    title: Component = Component.empty()
) : ScoreboardBuilder(title) {
    private val tasks = TaskTracker()

    open infix fun title(supplier: () -> Component) =
        title(ScoreboardComponentSupplier(supplier))

    open infix fun title(supplier: ScoreboardComponentSupplier) = supplier.apply {
        val initialComponent = supplier()
        isTitleComponent = true
        title = initialComponent
    }

    operator fun ScoreboardComponentSupplier.unaryPlus() = apply { add(this) }

    operator fun (() -> Component).unaryPlus() =
        ScoreboardComponentSupplier(this).apply { add(this) }

    infix fun add(supplier: ScoreboardComponentSupplier) {
        val initialComponent = supplier()
        val index = add(initialComponent)
        supplier.indexes.add(index)
    }

    infix fun scoreboardLine(block: () -> Component) =
        ScoreboardComponentSupplier(block)

    infix fun ScoreboardComponentSupplier.updateEvery(ticks: Long) = apply {
        tasks.runAsyncRepeat(ticks) {
            val component = invoke()
            if (isTitleComponent) {
                title = component
            }
            indexes.forEach { set(it, component) }
        }
    }

    override fun clear() {
        super.clear()
        tasks.cancelAll()
    }
}

fun dynamicScoreboard(title: Component, block: DynamicScoreboardBuilder.() -> Unit) =
    DynamicScoreboardBuilder(title).apply(block)

fun dynamicScoreboard(block: DynamicScoreboardBuilder.() -> Unit) =
    DynamicScoreboardBuilder().apply(block)