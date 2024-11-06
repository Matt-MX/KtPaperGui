package com.mattmx.ktgui.components.button

import net.kyori.adventure.text.Component

fun <T : GuiButton<T>> GuiButton<T>.named(supplier: () -> Component) {
    named(supplier())
    onUpdate {
        named(supplier())
    }
}