package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.components.screen.IGuiScreen
import java.util.function.Supplier

// todo change this, it's unclean af
inline fun <reified T : GuiScreen> gui(constructor: Supplier<T> = Supplier{ GuiScreen() as T }, gui: T.() -> Unit) : T {
    val g = constructor.get()
    gui.invoke(g)
    return g
}

inline fun <reified T : GuiButton> button(constructor: Supplier<T> = Supplier { GuiButton() as T }, button: T.() -> Unit) : T {
    val b = constructor.get()
    button.invoke(b)
    return b
}

inline fun <reified T : GuiButton> IGuiScreen.button(constructor: Supplier<T> = Supplier { GuiButton() as T }, button: T.() -> Unit) : T {
    val b = constructor.get()
    button.invoke(b)
    b childOf this
    return b
}