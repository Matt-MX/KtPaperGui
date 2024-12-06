package com.mattmx.ktgui

import org.bukkit.Material

fun button(material: Material) = PaperGuiButton(material)

fun button(material: Material, block: PaperGuiButton<*>.() -> Unit) =
    PaperGuiButton(material).apply(block)