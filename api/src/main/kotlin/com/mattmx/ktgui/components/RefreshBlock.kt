package com.mattmx.ktgui.components

import com.mattmx.ktgui.components.screen.GuiScreen

class RefreshBlock<T : GuiScreen>(
    val repeat: Long,
    private val owner: T,
    val block: (T) -> Unit
) {
    fun refresh() {
        block.invoke(owner)
        refresh()
    }
}