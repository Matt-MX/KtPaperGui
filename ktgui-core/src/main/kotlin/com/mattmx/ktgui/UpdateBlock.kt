package com.mattmx.ktgui

import com.mattmx.ktgui.screen.GuiScreen

open class UpdateBlock(
    protected val block: (UpdateBlock) -> Unit,
    protected val owner: GuiScreen<*, *>
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