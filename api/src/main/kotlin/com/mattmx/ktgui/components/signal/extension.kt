package com.mattmx.ktgui.components.signal

import com.mattmx.ktgui.components.screen.GuiScreen

fun <T, V> GuiScreen.signal(value: V) =
    Signal<T, V>(value, this)