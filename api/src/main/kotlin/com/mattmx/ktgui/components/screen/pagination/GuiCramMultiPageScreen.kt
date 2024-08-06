package com.mattmx.ktgui.components.screen.pagination

import com.mattmx.ktgui.components.GuiPattern
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.components.screen.pagination.GuiCramMultiPageScreen.NextSlotStrategy
import net.kyori.adventure.text.Component

open class GuiCramMultiPageScreen(
    title: Component,
    rows: Int = 6
) : GuiMultiPageScreen(title, rows) {
    val extraReservedSlots = arrayListOf<Int>()
    var tryGetNextSlot = NextSlotStrategy.next()
    var nextSlot: Int = -1

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

        val foundSlot = findNextEmptySpot(lastPage)
        println("placing at $foundSlot")
        if (foundSlot == null) {
            nextSlot = -1
            GuiScreen(Component.empty(), rows).apply { pages.add(this) }
            return cramAdd(child)
        }

        child childOf lastPage slot foundSlot
    }

    open fun findNextEmptySpot(sub: GuiScreen): Int? {
        while ((nextSlot in reservedSlots() || sub.items[nextSlot] != null) && (nextSlot < sub.totalSlots() || nextSlot < 0)) {
            nextSlot = tryGetNextSlot.getNextSlotToTry(nextSlot)
        }

        if (nextSlot >= sub.totalSlots()) return null

        return nextSlot
    }

    fun isFull(sub: GuiScreen) = sub.slotsUsed().size >= totalSlots() - reservedSlots().size

    fun reservedSlots() = (this.slotsUsed() + extraReservedSlots).toSet()

    fun interface NextSlotStrategy {
        fun getNextSlotToTry(previous: Int): Int

        companion object {
            @JvmStatic
            fun next(gap: Int = 1) = NextSlotStrategy { it + gap }

            @JvmStatic
            fun pattern(char: Char, pattern: GuiPattern): NextSlotStrategy {
                if (!pattern.pattern.contains(char)) {
                    // Prevent stack overflow exception
                    error("The pattern provided does not contain the char '$char'")
                }

                return NextSlotStrategy {
                    if (pattern.pattern.length < it) return@NextSlotStrategy Int.MAX_VALUE
                    val nextIndex = it + 1

                    val next = nextIndex + pattern.pattern
                        .substring(nextIndex + 1, pattern.pattern.length)
                        .indexOf(char)

                    println("char at $next")

                    if (next == -1) {
                        // No more slots
                        Int.MAX_VALUE
                    } else next
                }
            }
        }
    }
}

fun cramMultiPageScreen(title: Component, rows: Int = 6, block: GuiCramMultiPageScreen.() -> Unit) =
    GuiCramMultiPageScreen(title, rows).apply(block)