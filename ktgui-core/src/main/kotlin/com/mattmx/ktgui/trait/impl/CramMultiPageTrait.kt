package com.mattmx.ktgui.trait.impl

import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.trait.AbstractTrait
import java.util.*

class CramMultiPageTrait(owner: GuiScreen<*, *>) : AbstractTrait<GuiScreen<*, *>>(owner) {
    var isAutoUpdate: Boolean = true
    var currentPage: Int = 0
        set(value) {
            field = value
            getOwner().refresh()
        }
    val immutableSlots = mutableListOf<Int>()

    override fun onEnable() {
        getOwner().visiblePagesOverride = Optional.of {
            val slots = getOwner().guiType.totalSlots
            val start = currentPage * slots
            val end = (currentPage + 1) * slots

            (start..end).toList() + immutableSlots
        }
    }

    override fun onDisable() {
        getOwner().visiblePagesOverride = Optional.empty()
    }

    fun isFirstPage() = currentPage == 0

    fun isLastPage() = getOwner().items.keys.max() >= (currentPage + 1) * getOwner().guiType.totalSlots
}

fun GuiScreen<*, *>.cramPages(block: MultiPageTrait.() -> Unit) =
    traits.getOrCreate(MultiPageTrait::class.java).apply(block)