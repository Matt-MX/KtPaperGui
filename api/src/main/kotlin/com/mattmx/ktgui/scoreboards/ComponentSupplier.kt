package com.mattmx.ktgui.scoreboards

import net.kyori.adventure.text.Component

open class ComponentSupplier(
    val supplier: () -> Component
) {

    operator fun invoke() = supplier()

}