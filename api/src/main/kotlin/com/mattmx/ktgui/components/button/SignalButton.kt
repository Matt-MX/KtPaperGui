package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.signal.SignalListener
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Deprecated("No longer supported", ReplaceWith("EffectBlock"))
class SignalButton(
    val material: Material,
    val builder: (SignalButton) -> Unit
) : GuiButton<SignalButton>(material), SignalListener<Any> {
    override fun onChange(value: Any) {

        // clear all data of the button
        lore { clear() }
        named(null)
        click.clear()
        item = ItemStack(material)

        builder(this)
        update()
    }
}