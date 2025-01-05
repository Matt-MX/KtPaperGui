package com.mattmx.ktgui.settings

import net.kyori.adventure.text.Component

class ComponentProvider<I>(
    private val function: (I) -> Component
) {
    operator fun invoke(input: I) = function.invoke(input)
}