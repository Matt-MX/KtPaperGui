package com.mattmx.ktgui.event

import com.mattmx.ktgui.button.GuiButton

class SlotUpdatedEvent<B : GuiButton<*, *, *, *>>(
    val old: B?,
    val new: B?,
    val slot: Int
) {

    fun isRemoval() = old != null && new == null

    fun isAdd() = old == null && new != null

    fun isChange() = old != null && new != null

}