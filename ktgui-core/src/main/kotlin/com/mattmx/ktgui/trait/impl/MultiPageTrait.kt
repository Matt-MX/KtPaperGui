package com.mattmx.ktgui.trait.impl

import com.mattmx.ktgui.button.GuiButton
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.trait.AbstractTrait
import com.mattmx.ktgui.event.EventCallback

class MultiPageTrait(owner: GuiScreen<*, *>) : AbstractTrait<GuiScreen<*, *>>(owner) {
    var isAutoUpdate: Boolean = true
    val onPageChange = EventCallback<MultiPageTrait>()
    var currentPage: Int = 0
        set(value) {
            field = value
            onPageChange.apply(this)

            val slotsToClear = getOwner().getVisibleGuiButtons().toMutableSet()
            slotsToClear.removeAll(this.immutableSlots)

            getOwner().clear(slotsToClear.toList())
            pages[value]?.invoke()

            getOwner().refresh()
        }
    val pages = hashMapOf<Int, () -> Unit>()
    val immutableSlots = mutableSetOf<Int>()

    operator fun GuiButton<*, *, *, *>.unaryMinus() {
        lock(this)
    }

    fun lock(button: GuiButton<*, *, *, *>) = apply {
        this.immutableSlots.addAll(getOwner().getSlots(button))
    }

    fun page(pageIndex: Int, block: () -> Unit) = apply {
        this.pages[pageIndex] = block
    }

    fun isFirstPage() = currentPage == 0

    fun isLastPage() = getOwner().items.keys.max() >= (currentPage + 1) * getOwner().guiType.getTotalSlots()
}

fun GuiScreen<*, *>.pages(block: MultiPageTrait.() -> Unit) =
    traits.getOrCreate(MultiPageTrait::class.java).apply(block)