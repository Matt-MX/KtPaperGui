package com.mattmx.ktgui.components.button

import net.kyori.adventure.text.Component

class LoreList(
    existing: List<Component>?
) : ArrayList<Component>(existing ?: emptyList()) {

    operator fun Component.unaryPlus() = add(this)

}