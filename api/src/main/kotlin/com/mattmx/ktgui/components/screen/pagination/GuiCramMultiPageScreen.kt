package com.mattmx.ktgui.components.screen.pagination

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.utils.not
import net.kyori.adventure.text.Component
import org.bukkit.Material

class GuiCramMultiPageScreen(
    title: Component,
    rows: Int = 6
) : GuiMultiPageScreen(title, rows) {
    val extraReservedSlots = arrayListOf<Int>()

    infix fun reserve(slots: IntRange) = extraReservedSlots.addAll(slots)
    infix fun reserve(slots: List<Int>) = extraReservedSlots.addAll(slots)

    operator fun GuiButton<*>.unaryPlus() = cramAdd(this)
    operator fun Collection<GuiButton<*>>.unaryPlus() = forEach { cramAdd(it) }
    open fun cramAdd(child: GuiButton<*>) {
        var lastPage = pages.lastOrNull()

        // If it's full then make a new one
        if (lastPage == null || isFull(lastPage)) {
            lastPage = GuiScreen(Component.empty(), rows).apply { pages.add(this) }
        }

        val nextSlot = nextSlotToFill(lastPage)
        if (nextSlot == null) {
            GuiScreen(Component.empty(), rows).apply { pages.add(this) }
            return cramAdd(child)
        }

        child childOf lastPage slot nextSlot
    }

    fun nextSlotToFill(sub: GuiScreen): Int? {
        var nextSlot = sub.slotsUsed().max() + 1

        while (nextSlot in reservedSlots() && nextSlot <= sub.totalSlots()) {
            nextSlot++
        }

        if (nextSlot >= sub.totalSlots()) return null

        return nextSlot
    }

    fun isFull(sub: GuiScreen) = sub.slotsUsed().size >= totalSlots() - reservedSlots().size

    fun reservedSlots() = this.slotsUsed() + extraReservedSlots

}

fun cramMultiPageScreen(title: Component, rows: Int = 6, block: GuiCramMultiPageScreen.() -> Unit) =
    GuiCramMultiPageScreen(title, rows).apply(block)

fun main() {
    val gui = cramMultiPageScreen(!"Materials") {
        reserve(last() - 8..last())

        button(Material.SPECTRAL_ARROW) {
            named(!"&aNext")
            click.left { navigateNextPage() }
        } slot last()

        button(Material.SPECTRAL_ARROW) {
            named(!"&cLast")
            click.left { navigatePreviousPage() }
        } slot last() - 8

        +Material.values().map { button(it) {} }
    }
}