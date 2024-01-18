package com.mattmx.ktgui.components

import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.components.signal.SignalListener
import com.mattmx.ktgui.components.signal.SignalOwner

class EffectBlock<T : GuiScreen>(
    val owner: T,
    val block: (T) -> Unit
) : SignalListener<Any> {
    override fun onChange(value: Any) {
        block(owner)
        // todo we might want to keep track of what buttons are used exactly inside of the effect block
        owner.refresh()
    }
}