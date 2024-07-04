package com.mattmx.ktgui.scoreboards

import net.kyori.adventure.text.Component
import java.util.*

class ScoreboardComponentSupplier(
    supplier: () -> Component
) : ComponentSupplier(supplier) {
    val indexes = arrayListOf<Int>()
    var isTitleComponent: Boolean = false
}