package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.signal.SignalListener
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SignalButton(
    val material: Material,
    val builder: (SignalButton) -> Unit
) : GuiButton<SignalButton>(material), SignalListener<Any> {
    override fun onChange(value: Any) {

        // clear all data of the button
        lore { clear() }
        named(null)
        clickCallback.clear()
        item = ItemStack(material)

        builder(this)
        update()
    }
}