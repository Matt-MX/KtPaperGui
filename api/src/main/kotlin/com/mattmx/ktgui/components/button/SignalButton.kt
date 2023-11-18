package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.signal.SignalListener
import org.bukkit.Material

class SignalButton(
    val material: Material,
    val builder: (SignalButton) -> Unit
) : GuiButton<SignalButton>(material), SignalListener<Any> {
    override fun onChange(value: Any) {
        builder(this)
        update()
    }
}