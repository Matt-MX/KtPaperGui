package com.mattmx.ktgui

import com.mattmx.ktgui.screen.GuiScreen

class UpdateBlock(
    val block: (UpdateBlock) -> Unit,
    val owner: GuiScreen<*, *>
) {

    init {
        block(this)
    }

    fun update() {
        block(this)
        owner.refreshTitle()
        owner.refresh()
    }

}